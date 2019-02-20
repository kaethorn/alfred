package de.wasenweg.komix.scanner;

import de.wasenweg.komix.comics.Comic;
import de.wasenweg.komix.comics.ComicRepository;
import de.wasenweg.komix.preferences.PreferencesService;

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
import java.util.Optional;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;
import java.util.zip.ZipFile;

@Service
public class ScannerService {

    private final EmitterProcessor<ServerSentEvent<String>> emitter = EmitterProcessor.create();

    @Autowired
    private ComicRepository comicRepository;

    @Autowired
    private PreferencesService preferencesService;

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

    private void reportError(final String error) {
        this.sendEvent(error, "error");
    }

    private Comic createOrUpdateComic(final Path path) {
        reportProgress(path.toString());

        Comic comic = new Comic();
        comic.setPath(path.toAbsolutePath().toString());

        final Optional<Comic> existingComic = comicRepository.findByPath(comic.getPath());
        if (existingComic.isPresent()) {
            comic = existingComic.get();
        }

        ZipFile file = null;
        try {
            file = new ZipFile(path.toString());
            MetaDataReader.set(file, comic);
            ThumbnailReader.set(file, comic);
        } catch (final Exception e) {
            e.printStackTrace();
            reportError(e.getMessage());
        } finally {
            try {
                file.close();
            } catch (final IOException e) {
                e.printStackTrace();
                reportError(e.getMessage());
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
        final Path comicsPath = Paths.get(this.preferencesService.get("comics.path"));

        Executors.newSingleThreadExecutor().execute(() -> {
            try {
                final List<Path> comicFiles = Files.walk(comicsPath)
                        .filter(path -> Files.isRegularFile(path))
                        .filter(path -> path.getFileName().toString().endsWith(".cbz"))
                        .collect(Collectors.toList());

                reportTotal(comicFiles.size());

                comicFiles.stream()
                        .map(path -> createOrUpdateComic(path))
                        .forEach(comic -> {
                            comicRepository.save(comic);
                        });

                // Purge comics from the DB that don't have a corresponding file.
                final List<String> comicFilePaths = comicFiles.stream()
                        .map(path -> path.toAbsolutePath().toString())
                        .collect(Collectors.toList());
                final List<Comic> toDelete = comicRepository.findAll().stream()
                        .filter(comic -> !comicFilePaths.contains(comic.getPath()))
                        .collect(Collectors.toList());
                comicRepository.deleteAll(toDelete);
            } catch (final IOException e) {
                e.printStackTrace();
                reportError(e.getMessage());
            }

            reportFinish();
        });

        return emitter.log();
    }
}
