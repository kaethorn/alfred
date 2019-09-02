package de.wasenweg.alfred.scanner;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import de.wasenweg.alfred.comics.Comic;
import de.wasenweg.alfred.comics.ComicRepository;
import de.wasenweg.alfred.settings.SettingsService;
import de.wasenweg.alfred.thumbnails.ThumbnailService;
import de.wasenweg.alfred.volumes.Volume;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.core.env.Profiles;
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
import java.util.Optional;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.zip.ZipFile;

@Service
public class ScannerService {

  private final EmitterProcessor<ServerSentEvent<String>> emitter = EmitterProcessor.create();

  private Logger logger = LoggerFactory.getLogger(ScannerService.class);

  @Autowired
  private ApiMetaDataReader apiMetaDataReader;

  @Autowired
  private FileMetaDataReader fileMetaDataReader;

  @Autowired
  private ObjectMapper objectMapper;

  @Autowired
  private Environment environment;

  @Autowired
  private ThumbnailService thumbnailService;

  @Autowired
  private ComicRepository comicRepository;

  @Autowired
  private SettingsService settingsService;

  private void sendEvent(final String data, final String name) {
    this.emitter.onNext(
        ServerSentEvent.builder(data)
          .id(String.valueOf(this.hashCode()))
          .event(name)
          .build()
    );
  }

  private void reportStart(final String path) {
    this.sendEvent("start", "start");
    this.logger.info("Reading comics in {}", path);
  }

  private void reportProgress(final String path) {
    this.sendEvent(path, "current-file");
    this.logger.debug("Parsing comic {}", path);
  }

  private void reportTotal(final int total) {
    this.sendEvent(String.valueOf(total), "total");
    this.logger.info("Found {} comics.", total);
  }

  private void reportCleanUp() {
    this.sendEvent("cleanUp", "cleanUp");
    this.logger.info("Purging orphaned comics.");
  }

  private void reportAssociation() {
    this.sendEvent("association", "association");
    this.logger.info("Associating volumes.");
  }

  private void reportFinish() {
    this.sendEvent("done", "done");
  }

  private void reportIssue(final Exception exception) {
    this.logger.error(exception.getLocalizedMessage(), exception);
    this.reportIssue(ScannerIssue.builder()
        .message(exception.getLocalizedMessage())
        .type(ScannerIssue.Type.ERROR)
        .build());
  }

  private void reportIssue(final Exception exception, final String path) {
    this.reportIssue(exception, path, ScannerIssue.Type.ERROR);
  }

  private void reportIssue(final Exception exception, final String path, final ScannerIssue.Type type) {
    this.logger.error(exception.getLocalizedMessage(), exception);
    this.reportIssue(ScannerIssue.builder()
        .message(exception.getLocalizedMessage())
        .type(type)
        .path(path)
        .build());
  }

  private void reportIssue(final ScannerIssue issue, final String path) {
    issue.setPath(path);
    this.reportIssue(issue);
  }

  private void reportIssue(final ScannerIssue issue) {
    if (this.environment.acceptsProfiles(Profiles.of("prod"))) {
      try {
        this.sendEvent(this.objectMapper.writeValueAsString(issue), "scan-issue");
      } catch (final JsonProcessingException exception) {
        exception.printStackTrace();
      }
    }
  }

  private void setThumbnail(final Optional<ZipFile> file, final Comic comic, final String pathString) {
    try {
      this.thumbnailService.setComic(file.get(), comic);
    } catch (final NoImagesException exception) {
      this.reportIssue(exception, pathString);
    } finally {
      try {
        file.get().close();
      } catch (final IOException exception) {
        this.reportIssue(exception, pathString);
      }
    }
  }

  private void processComic(final Path path) {
    final String pathString = path.toString();
    this.reportProgress(pathString);

    final String comicPath = path.toAbsolutePath().toString();

    final Comic comic = this.comicRepository.findByPath(comicPath)
        .orElse(new Comic());
    comic.setPath(comicPath);

    final Optional<ZipFile> file;
    try {
      file = Optional.of(new ZipFile(pathString));
    } catch (final IOException exception) {
      this.reportIssue(exception, pathString);
      return;
    }

    try {
      this.fileMetaDataReader.set(file.get(), comic).forEach(issue -> {
        this.reportIssue(issue, pathString);
      });
    } catch (final SAXException | IOException exception) {
      this.reportIssue(exception, pathString, ScannerIssue.Type.WARNING);
    } catch (final NoMetaDataException e) {
      final List<ScannerIssue> issues = this.apiMetaDataReader.set(comic);
      comic.setErrors(issues.stream().map(issue -> issue.getMessage()).collect(Collectors.toList()));
      issues.forEach(issue -> {
        this.reportIssue(issue, pathString);
      });
    }

    this.comicRepository.save(comic);

    this.setThumbnail(file, comic, pathString);
  }

  /**
   * Scan for comics.
   *
   * Updates existing files, adds new files and removes files that are
   * not available anymore.
   *
   * Mandatory fields are `publisher`, `series`, `volume` and `issue number`.
   *
   * Process:
   * 1. Ignore all files that do not end in `.cbz`.
   * 2. Attempt to parse mandatory fields from meta data XML. Exit on success.
   * 3. Ignore all files that do not match pattern containing mandatory fields, e.g.
   *    `{publisher}/{series} ({volume})/{series} ({volume}) {issue number} .*.cbz`.
   * 4. Attempt to match & scrape meta data from Comic Vine API.
   * 5. On match, write meta data XML and exit. Otherwise report error and ignore file.
   */
  public Flux<ServerSentEvent<String>> scanComics() {
    final Path comicsPath = Paths.get(this.settingsService.get("comics.path"));
    this.reportStart(comicsPath.toString());

    Executors.newSingleThreadExecutor().execute(() -> {
      try {
        final List<Path> comicFiles = Files.walk(comicsPath)
            .filter(path -> Files.isRegularFile(path))
            .filter(path -> path.getFileName().toString().endsWith(".cbz"))
            .collect(Collectors.toList());

        this.reportTotal(comicFiles.size());
        comicFiles.forEach(path -> this.processComic(path));
        this.logger.info("Parsed {} comics.", comicFiles.size());

        this.cleanOrphans(comicFiles);
        this.associateVolumes();

        this.logger.info("Done scanning.");
      } catch (final IOException exception) {
        this.reportIssue(exception);
      }

      this.reportFinish();
    });

    return this.emitter.log();
  }

  private void cleanOrphans(final List<Path> comicFiles) {
    this.reportCleanUp();

    // Purge comics from the DB that don't have a corresponding file.
    final List<String> comicFilePaths = comicFiles.stream()
        .map(path -> path.toAbsolutePath().toString())
        .collect(Collectors.toList());
    final List<Comic> toDelete = this.comicRepository.findAll().stream()
        .filter(comic -> !comicFilePaths.contains(comic.getPath()))
        .collect(Collectors.toList());
    this.comicRepository.deleteAll(toDelete);
  }

  /**
   * Associates comics within a volume.
   *
   * Sets the `previousId` and `nextId` attributes for each comic which point to the
   * previous and next comic within the current volume.
   */
  public void associateVolumes() {
    this.reportAssociation();

    // Get all comics, grouped by volume.
    final Map<Volume, List<Comic>> volumes = this.comicRepository
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
      this.logger.debug("Associating {} comics for {}.", comics.size(), volume.toString());
      // Traverse each comic in the volume
      IntStream.range(0, comics.size()).forEach(index -> {
        final Comic comic = comics.get(index);
        this.logger.trace("Associating comic {}.", comic.getPosition());
        if (index > 0) {
          final Comic previousComic = comics.get(index - 1);
          comic.setPreviousId(previousComic.getId());
          this.logger.trace("Associating comic {} with previous comic {}", comic.getPosition(), previousComic.getPosition());
        }
        if (index < (comics.size() - 1)) {
          final Comic nextComic = comics.get(index + 1);
          comic.setNextId(nextComic.getId());
          this.logger.trace("Associating comic {} with next comic {}", comic.getPosition(), nextComic.getPosition());
        }
        this.comicRepository.save(comic);
      });
    });
  }
}
