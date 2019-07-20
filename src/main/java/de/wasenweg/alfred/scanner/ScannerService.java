package de.wasenweg.alfred.scanner;

import de.wasenweg.alfred.comics.Comic;
import de.wasenweg.alfred.comics.ComicRepository;
import de.wasenweg.alfred.settings.SettingsService;
import de.wasenweg.alfred.thumbnails.ThumbnailSetter;
import de.wasenweg.alfred.volumes.Volume;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.stereotype.Service;
import org.xml.sax.SAXException;

import reactor.core.publisher.EmitterProcessor;
import reactor.core.publisher.Flux;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.zip.ZipFile;

@Service
public class ScannerService {

  private final EmitterProcessor<ServerSentEvent<String>> emitter = EmitterProcessor.create();

  private Logger logger = LoggerFactory.getLogger(ScannerService.class);

  @Autowired
  private ComicRepository comicRepository;

  @Autowired
  private SettingsService settingsService;

  private void sendEvent(final String data, final String name) {
    emitter.onNext(
        ServerSentEvent.builder(data)
          .id(String.valueOf(this.hashCode()))
          .event(name)
          .build()
    );
  }

  private void reportStart(final String path) {
    this.sendEvent("start", "start");
    logger.info("Reading comics in {}", path);
  }

  private void reportProgress(final String path) {
    this.sendEvent(path, "current-file");
    logger.debug("Parsing comic {}", path);
  }

  private void reportTotal(final int total) {
    this.sendEvent(String.valueOf(total), "total");
    logger.info("Found {} comics.", total);
  }

  private void reportCleanUp() {
    this.sendEvent("cleanUp", "cleanUp");
    logger.info("Purging orphaned comics.");
  }

  private void reportAssociation() {
    this.sendEvent("association", "association");
    logger.info("Associating volumes.");
  }

  private void reportFinish() {
    this.sendEvent("done", "done");
  }

  private void reportError(final String path, final Exception exception) {
    this.sendEvent(path + "|" + exception.getMessage(), "error");
  }

  private void reportError(final Exception exception) {
    this.reportError("", exception);
  }

  private void createOrUpdateComic(final Path path) {
    final String pathString = path.toString();
    reportProgress(pathString);

    final String comicPath = path.toAbsolutePath().toString();

    final Comic comic = comicRepository.findByPath(comicPath)
        .orElse(new Comic());
    comic.setPath(comicPath);

    ZipFile file = null;
    try {
      file = new ZipFile(pathString);
    } catch (final IOException exception) {
      logger.error(exception.getLocalizedMessage(), exception);
      reportError(pathString, exception);
      return;
    }
    try {
      MetaDataReader.set(file, comic);
    } catch (final SAXException | IOException exception) {
      logger.error(exception.getLocalizedMessage(), exception);
      reportError(pathString, exception);
    } finally {
      try {
        file.close();
      } catch (final IOException exception) {
        logger.error(exception.getLocalizedMessage(), exception);
        reportError(pathString, exception);
      }
    }

    comicRepository.save(comic);

    try {
      ThumbnailSetter.set(file, comic);
    } catch (final NoImagesException exception) {
      logger.error(exception.getLocalizedMessage(), exception);
      reportError(pathString, exception);
    }
  }

  /**
   * Scan for comics.
   *
   * Updates existing files, adds new files and removes files that are
   * not available anymore.
   */
  public Flux<ServerSentEvent<String>> scanComics() {
    final Path comicsPath = Paths.get(this.settingsService.get("comics.path"));
    reportStart(comicsPath.toString());

    Executors.newSingleThreadExecutor().execute(() -> {
      try {
        final List<Path> comicFiles = Files.walk(comicsPath)
            .filter(path -> Files.isRegularFile(path))
            .filter(path -> path.getFileName().toString().endsWith(".cbz"))
            .collect(Collectors.toList());

        reportTotal(comicFiles.size());
        comicFiles.forEach(path -> createOrUpdateComic(path));
        logger.info("Parsed {} comics.", comicFiles.size());

        this.cleanOrphans(comicFiles);
        this.associateVolumes();

        logger.info("Done scanning.");
      } catch (final IOException exception) {
        logger.error(exception.getLocalizedMessage(), exception);
        reportError(exception);
      }

      reportFinish();
    });

    return emitter.log();
  }

  private void cleanOrphans(final List<Path> comicFiles) {
    reportCleanUp();

    // Purge comics from the DB that don't have a corresponding file.
    final List<String> comicFilePaths = comicFiles.stream()
        .map(path -> path.toAbsolutePath().toString())
        .collect(Collectors.toList());
    final List<Comic> toDelete = comicRepository.findAll().stream()
        .filter(comic -> !comicFilePaths.contains(comic.getPath()))
        .collect(Collectors.toList());
    comicRepository.deleteAll(toDelete);
  }

  /**
   * Associates comics within a volume.
   *
   * Sets the `previousId` and `nextId` attributes for each comic which point to the
   * previous and next comic within the current volume.
   */
  public void associateVolumes() {
    reportAssociation();

    // Get all comics, grouped by volume.
    final Map<Volume, List<Comic>> volumes = comicRepository
        .findAllByOrderByPublisherAscSeriesAscVolumeAscPositionAsc().stream()
        .collect(Collectors.groupingBy(comic -> {
          final Volume volume = new Volume();
          volume.setPublisher(comic.getPublisher());
          volume.setSeries(comic.getSeries());
          volume.setVolume(comic.getVolume());
          return volume;
        }));

    // Traverse each volume
    volumes.forEach((volume, comics) -> {
      logger.debug("Associating {} comics for {}.", comics.size(), volume.toString());
      // Traverse each comic in the volume
      IntStream.range(0, comics.size()).forEach(index -> {
        final Comic comic = comics.get(index);
        logger.trace("Associating comic {}.", comic.getPosition());
        if (index > 0) {
          final Comic previousComic = comics.get(index - 1);
          comic.setPreviousId(previousComic.getId());
          logger.trace("Associating comic {} with previous comic {}", comic.getPosition(), previousComic.getPosition());
        }
        if (index < (comics.size() - 1)) {
          final Comic nextComic = comics.get(index + 1);
          comic.setNextId(nextComic.getId());
          logger.trace("Associating comic {} with next comic {}", comic.getPosition(), nextComic.getPosition());
        }
        comicRepository.save(comic);
      });
    });
  }
}
