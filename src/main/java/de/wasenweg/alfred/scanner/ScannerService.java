package de.wasenweg.alfred.scanner;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import de.wasenweg.alfred.comics.Comic;
import de.wasenweg.alfred.comics.ComicRepository;
import de.wasenweg.alfred.settings.SettingsService;
import de.wasenweg.alfred.thumbnails.ThumbnailService;
import de.wasenweg.alfred.volumes.Volume;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.stereotype.Service;
import org.xml.sax.SAXException;
import reactor.core.publisher.EmitterProcessor;
import reactor.core.publisher.Flux;

import javax.xml.parsers.ParserConfigurationException;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.ProviderNotFoundException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static java.lang.String.format;

@Service
@Slf4j
public class ScannerService {

  private EmitterProcessor<ServerSentEvent<String>> emitter;

  @Autowired
  private ApiMetaDataService apiMetaDataService;

  @Autowired
  private FileMetaDataService fileMetaDataService;

  @Autowired
  private ObjectMapper objectMapper;

  @Autowired
  private ThumbnailService thumbnailService;

  @Autowired
  private ComicRepository comicRepository;

  @Autowired
  private SettingsService settingsService;

  private void sendEvent(final String data, final String name) {
    final String timestamp = (new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")).format(new Date());
    this.emitter.onNext(ServerSentEvent.builder(data).id(timestamp).event(name).build());
  }

  private void reportStart(final String path) {
    this.sendEvent("start", "start");
    log.info("Reading comics in {}", path);
  }

  private void reportProgress(final String path) {
    this.sendEvent(path, "current-file");
    log.debug("Parsing comic {}", path);
  }

  private void reportTotal(final int total) {
    this.sendEvent(String.valueOf(total), "total");
    log.info("Found {} comics.", total);
  }

  private void reportCleanUp() {
    this.sendEvent("cleanUp", "cleanUp");
    log.info("Purging orphaned comics.");
  }

  private void reportAssociation() {
    this.sendEvent("association", "association");
    log.info("Associating volumes.");
  }

  private void reportFinish() {
    this.sendEvent("done", "done");
  }

  private void reportIssue(final Exception exception) {
    log.error(exception.getLocalizedMessage(), exception);
    this.reportIssue(
        ScannerIssue.builder()
          .message(exception.getLocalizedMessage())
          .severity(ScannerIssue.Severity.ERROR)
          .build());
  }

  private void reportIssue(final Comic comic, final Exception exception) {
    log.error(exception.getLocalizedMessage());
    this.reportIssue(
        comic,
        ScannerIssue.builder()
          .message(exception.getLocalizedMessage())
          .severity(ScannerIssue.Severity.ERROR)
          .build());
  }

  private void reportIssue(final Comic comic, final Exception exception, final ScannerIssue.Severity severity) {
    log.error(exception.getLocalizedMessage(), exception);
    this.reportIssue(
        comic,
        ScannerIssue.builder()
          .message(exception.getLocalizedMessage())
          .severity(severity)
          .build());
  }

  private void reportIssue(final Comic comic, final ScannerIssue issue) {
    final List<ScannerIssue> errors = Optional
        .ofNullable(comic.getErrors())
        .orElse(new ArrayList<ScannerIssue>());
    errors.add(issue);
    comic.setErrors(errors);
    this.reportIssue(issue);
  }

  private void reportIssue(final ScannerIssue issue) {
    try {
      this.sendEvent(this.objectMapper.writeValueAsString(issue), "scan-issue");
    } catch (final JsonProcessingException exception) {
      exception.printStackTrace();
    }
  }

  public void processComic(final Comic comic) {
    comic.setErrors(null);

    try {
      this.fileMetaDataService.read(comic).forEach(issue -> {
        this.reportIssue(comic, issue);
      });

      this.comicRepository.save(comic);
      this.thumbnailService.read(comic);
    } catch (final SAXException | IOException | ParserConfigurationException exception) {
      this.reportIssue(comic, exception, ScannerIssue.Severity.WARNING);
    } catch (final NoImagesException | ProviderNotFoundException | InvalidFileException exception) {
      this.reportIssue(comic, exception);
    } catch (final NoMetaDataException exception) {
      log.info(format("No metadata found for %s, querying ComicVine API.", comic.getPath()));
      final List<ScannerIssue> issues = this.apiMetaDataService.set(comic);
      this.fileMetaDataService.write(comic);
      issues.forEach(issue -> {
        this.reportIssue(comic, issue);
      });
      if (issues.size() == 0) {
        this.fileMetaDataService.write(comic);
      }
    } finally {
      // Save the comic again to store errors that might have occursed as Exceptions
      this.comicRepository.save(comic);
    }
  }

  private void processComicByPath(final Path path) {
    this.reportProgress(path.toString());

    final String comicPath = path.toAbsolutePath().toString();

    final Comic comic = this.comicRepository.findByPath(comicPath)
        .orElse(new Comic());
    comic.setPath(comicPath);
    comic.setFileName(path.getFileName().toString());
    this.processComic(comic);
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
  public Flux<ServerSentEvent<String>> scanComics() {
    log.info("Triggered scan-progress.");
    this.emitter = EmitterProcessor.create();
    final Path comicsPath = Paths.get(this.settingsService.get("comics.path"));
    this.reportStart(comicsPath.toString());

    Executors.newSingleThreadExecutor().execute(() -> {
      try {
        final List<Path> comicFiles = Files.walk(comicsPath).filter(path -> Files.isRegularFile(path))
            .filter(path -> path.getFileName().toString().endsWith(".cbz")).collect(Collectors.toList());

        this.reportTotal(comicFiles.size());
        comicFiles.forEach(path -> this.processComicByPath(path));
        log.info("Parsed {} comics.", comicFiles.size());

        this.cleanOrphans(comicFiles);
        this.associateVolumes();

        log.info("Done scanning.");
      } catch (final IOException exception) {
        this.reportIssue(exception);
      }

      this.reportFinish();
      this.emitter.onComplete();
    });

    return this.emitter.log();
  }

  private void cleanOrphans(final List<Path> comicFiles) {
    this.reportCleanUp();

    // Purge comics from the DB that don't have a corresponding file.
    final List<String> comicFilePaths = comicFiles.stream().map(path -> path.toAbsolutePath().toString())
        .collect(Collectors.toList());
    final List<Comic> toDelete = this.comicRepository.findAll().stream()
        .filter(comic -> !comicFilePaths.contains(comic.getPath())).collect(Collectors.toList());
    this.comicRepository.deleteAll(toDelete);
  }

  /**
   * Associates comics within a volume.
   *
   * Sets the `previousId` and `nextId` attributes for each comic which point to
   * the previous and next comic within the current volume.
   */
  public void associateVolumes() {
    this.reportAssociation();

    // Get all comics, grouped by volume.
    final Map<Volume, List<Comic>> volumes = this.comicRepository
        .findAllByOrderByPublisherAscSeriesAscVolumeAscPositionAsc().stream().collect(Collectors.groupingBy(comic -> {
          final Volume volume = new Volume();
          volume.setPublisher(comic.getPublisher());
          volume.setSeries(comic.getSeries());
          volume.setVolume(comic.getVolume());
          return volume;
        }));

    // Traverse each volume
    volumes.forEach((volume, comics) -> {
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
}
