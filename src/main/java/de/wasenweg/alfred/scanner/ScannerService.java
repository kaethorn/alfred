package de.wasenweg.alfred.scanner;

import de.wasenweg.alfred.comics.Comic;
import de.wasenweg.alfred.comics.ComicRepository;
import de.wasenweg.alfred.settings.SettingsService;
import de.wasenweg.alfred.volumes.Volume;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.stereotype.Service;

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
import java.util.zip.ZipFile;

@Service
public class ScannerService {

    private final EmitterProcessor<ServerSentEvent<String>> emitter = EmitterProcessor.create();

    Logger logger = LoggerFactory.getLogger(ScannerService.class);

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

    private void reportProgress(final String path) {
        this.sendEvent(path, "current-file");
    }

    private void reportTotal(final int total) {
        this.sendEvent(String.valueOf(total), "total");
    }

    private void reportFinish() {
        this.sendEvent("", "done");
    }

    private void reportError(final Exception exception) {
        this.sendEvent(exception.getClass().getSimpleName() + ": " + exception.getMessage(), "error");
    }

    private Comic createOrUpdateComic(final Path path) {
        reportProgress(path.toString());

        final String comicPath = path.toAbsolutePath().toString();

        final Comic comic = comicRepository.findByPath(comicPath)
                .orElse(new Comic());
        comic.setPath(comicPath);

        ZipFile file = null;
        try {
            file = new ZipFile(path.toString());
            MetaDataReader.set(file, comic);
            ThumbnailReader.set(file, comic);
        } catch (final Exception exception) {
            exception.printStackTrace();
            reportError(exception);
        } finally {
            try {
                file.close();
            } catch (final IOException exception) {
                exception.printStackTrace();
                reportError(exception);
            }
        }

        return comic;
    }

    /**
     * Scan for comics.
     *
     * Updates existing files, adds new files and removes files that are
     * not available anymore.
     */
    public Flux<ServerSentEvent<String>> scanComics() {
        final Path comicsPath = Paths.get(this.settingsService.get("comics.path"));

        Executors.newSingleThreadExecutor().execute(() -> {
            try {
                this.logger.info("Reading comics in {}", comicsPath.toString());

                final List<Path> comicFiles = Files.walk(comicsPath)
                        .filter(path -> Files.isRegularFile(path))
                        .filter(path -> path.getFileName().toString().endsWith(".cbz"))
                        .collect(Collectors.toList());

                reportTotal(comicFiles.size());

                this.logger.info("Parsing {} comics.", comicFiles.size());

                comicFiles.stream()
                        .map(path -> createOrUpdateComic(path))
                        .forEach(comic -> {
                            comicRepository.save(comic);
                        });

                this.logger.info("Parsed {} comics.", comicFiles.size());
                this.logger.info("Purging orphaned comics.");

                // Purge comics from the DB that don't have a corresponding file.
                final List<String> comicFilePaths = comicFiles.stream()
                        .map(path -> path.toAbsolutePath().toString())
                        .collect(Collectors.toList());
                final List<Comic> toDelete = comicRepository.findAll().stream()
                        .filter(comic -> !comicFilePaths.contains(comic.getPath()))
                        .collect(Collectors.toList());
                comicRepository.deleteAll(toDelete);

                this.logger.info("Associating volumes.");

                // Associate volume IDs
                // Find all volumes
                final List<Comic> comics = comicRepository
                  .findAllByOrderByPublisherAscSeriesAscVolumeAscPositionAsc();
                final Map<Volume, List<Comic>> volumes = comics.stream()
                    .collect(Collectors.groupingBy(comic -> { final Volume volume = new Volume();
                        volume.setPublisher(comic.getPublisher());
                        volume.setSeries(comic.getSeries());
                        volume.setVolume(comic.getVolume());
                        return volume;
                    }));

                // Traverse each volume
                volumes.forEach((volume, volumeComics) -> {
                    this.logger.info("Associating comics for {}.", volume.toString());
                    // TODO

                });
            } catch (final IOException exception) {
                exception.printStackTrace();
                reportError(exception);
            }

            reportFinish();
        });

        return emitter.log();
    }
}
