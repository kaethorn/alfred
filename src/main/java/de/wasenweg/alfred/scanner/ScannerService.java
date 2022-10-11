package de.wasenweg.alfred.scanner;

import de.wasenweg.alfred.comics.Comic;
import de.wasenweg.alfred.comics.ComicRepository;
import de.wasenweg.alfred.settings.SettingsService;
import de.wasenweg.alfred.thumbnails.NoThumbnailsException;
import de.wasenweg.alfred.thumbnails.ThumbnailService;
import de.wasenweg.alfred.volumes.Volume;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.stereotype.Service;
import org.xml.sax.SAXException;
import reactor.core.publisher.Flux;

import javax.xml.parsers.ParserConfigurationException;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.ProviderNotFoundException;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Service
@Slf4j
@RequiredArgsConstructor
public class ScannerService {

  private final ApiMetaDataService apiMetaDataService;
  private final FileMetaDataService fileMetaDataService;
  private final ThumbnailService thumbnailService;
  private final ComicRepository comicRepository;
  private final SettingsService settingsService;
  private final ScanProgressService scanProgressService;

  public Comic processComic(final Comic comic) {
    comic.setErrors(null);

    try {
      this.fileMetaDataService.read(comic).forEach(issue -> {
        this.scanProgressService.reportIssue(comic, issue);
      });

      this.comicRepository.save(comic);
      this.thumbnailService.read(comic);
    } catch (final SAXException | IOException | ParserConfigurationException exception) {
      this.scanProgressService.reportIssue(comic, exception, ScannerIssue.Severity.WARNING);
    } catch (final NoImagesException | NoThumbnailsException | ProviderNotFoundException | InvalidFileException exception) {
      this.scanProgressService.reportIssue(comic, exception);
    } catch (final NoMetaDataException exception) {
      log.info("No metadata found for {}, querying ComicVine API.", comic.getPath());
      final List<ScannerIssue> issues = this.apiMetaDataService.set(comic);
      this.fileMetaDataService.write(comic);
      issues.forEach(issue -> {
        this.scanProgressService.reportIssue(comic, issue);
      });
      if (issues.isEmpty()) {
        this.fileMetaDataService.write(comic);
      }
    }
    return this.comicRepository.save(comic);
  }

  /**
   * Scan for comics.
   *
   * Updates existing files, adds new files and removes files that are not
   * available anymore.
   *
   * Mandatory fields are `publisher`, `series`, `volume` and `issue number`.
   *
   * Process:
   * 1. Ignore all files that do not end in `.cbz`.
   * 2. Attempt to parse mandatory fields from meta data XML. Exit on success.
   * 3. Ignore all files that do not match pattern containing mandatory fields, e.g.
   *    `{publisher}/{series} ({volume})/{series} ({volume}) {issue number}.cbz`.
   * 4. Attempt to match & scrape meta data from Comic Vine API.
   * 5. On match, write meta data XML and exit. Otherwise report error and ignore file.
   */
  public Flux<ServerSentEvent<String>> scan() {
    log.info("Triggered scan.");
    this.scanProgressService.createEmitter();
    final Path comicsPath = Paths.get(this.settingsService.get("comics.path"));
    this.scanProgressService.reportStart(comicsPath.toString());

    Executors.newSingleThreadExecutor().execute(() -> {
      try {
        final List<Path> comicFiles = Files.walk(comicsPath).filter(path -> Files.isRegularFile(path))
            .filter(path -> path.getFileName().toString().endsWith(".cbz")).collect(Collectors.toList());

        this.scanProgressService.reportTotal(comicFiles.size());
        comicFiles.stream().sorted().forEach(path -> this.processComicByPath(path));
        log.info("Parsed {} comics.", comicFiles.size());

        this.scanProgressService.reportCleanUp();
        this.cleanOrphans(comicFiles);
        this.scanProgressService.reportAssociation();
        this.associateVolumes();

        log.info("Done scanning.");
      } catch (final IOException exception) {
        this.scanProgressService.reportIssue(exception);
      }

      this.scanProgressService.reportFinish();
    });

    return this.resume();
  }

  public Flux<ServerSentEvent<String>> resume() {
    return this.scanProgressService.subscribeEmitter();
  }

  // Purge comics from the DB that don't have a corresponding file.
  public void cleanOrphans(final List<Path> comicFiles) {
    final List<String> comicFilePaths = comicFiles.stream()
        .map(path -> path.toAbsolutePath().toString())
        .collect(Collectors.toList());

    if (!comicFilePaths.isEmpty()) {
      final List<Comic> toDelete = this.comicRepository.findAll().stream()
          .filter(comic -> !comicFilePaths.contains(comic.getPath()))
          .collect(Collectors.toList());
      this.comicRepository.deleteAll(toDelete);
    }
  }

  /**
   * Associates comics within a volume.
   *
   * Sets the `previousId` and `nextId` attributes for each comic which point to
   * the previous and next comic within the current volume.
   */
  public void associateVolumes() {
    // Get all comics, grouped by volume.
    this.comicRepository
        .findAllByOrderByPublisherAscSeriesAscVolumeAscPositionAsc().stream().collect(Collectors.groupingBy(comic -> {
          final Volume volume = new Volume();
          volume.setPublisher(comic.getPublisher());
          volume.setSeries(comic.getSeries());
          volume.setName(comic.getVolume());
          return volume;
        })).forEach((volume, comics) -> {
          log.debug("Associating {} comics for {}.", comics.size(), volume.toString());
          // Traverse each comic in the volume
          IntStream.range(0, comics.size()).forEach(index -> {
            final Comic comic = comics.get(index);
            log.trace("Associating comic {}.", comic.getPosition());
            if (index > 0) {
              final Comic previousComic = comics.get(index - 1);
              comic.setPreviousId(previousComic.getId());
              log.trace("Associating comic {} with previous comic {}", comic.getPosition(),
                  previousComic.getPosition());
            }
            if (index < (comics.size() - 1)) {
              final Comic nextComic = comics.get(index + 1);
              comic.setNextId(nextComic.getId());
              log.trace("Associating comic {} with next comic {}", comic.getPosition(), nextComic.getPosition());
            }
            this.comicRepository.save(comic);
          });
        });
  }

  private void processComicByPath(final Path path) {
    this.scanProgressService.reportProgress(path.toString());

    final String comicPath = path.toAbsolutePath().toString();
    final Comic comic = this.comicRepository.findByPath(comicPath)
        .orElse(new Comic());

    comic.setPath(comicPath);
    comic.setFileName(
        Optional
            .ofNullable(path.getFileName())
            .orElse(Paths.get("null"))
            .toString());
    this.processComic(comic);
  }
}
