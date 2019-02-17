package de.wasenweg.komix.scanner;

import de.wasenweg.komix.comics.Comic;
import de.wasenweg.komix.comics.ComicRepository;
import de.wasenweg.komix.preferences.PreferencesService;

import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter.SseEventBuilder;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.zip.ZipFile;

@Service
public class ScannerService {

    private List<SseEmitter> emitters = new ArrayList<>();

    private final ComicRepository comicRepository;

    private final PreferencesService preferencesService;

    public ScannerService(final ComicRepository comicRepository, final PreferencesService preferencesService) {
        this.comicRepository = comicRepository;
        this.preferencesService = preferencesService;
    }

    private void sendEvent(final String data, final String name) {
        final SseEventBuilder event = SseEmitter.event().data(data).id(String.valueOf(this.hashCode())).name(name);
        emitters.forEach(emitter -> {
            try {
                emitter.send(event);
            } catch (final IOException e) {
                reportError(e.getMessage());
                emitter.completeWithError(e);
            }
        });
    }

    private void reportProgress(final String path) {
        this.sendEvent(path, "current-file");
    }

    private void reportTotal(final int total) {
        this.sendEvent(String.valueOf(total), "total");
    }

    public void reportFinish() {
        this.sendEvent("", "done");
    }

    public void reportError(final String error) {
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
     *
     * Process:
     * 1. Extract a list of paths of all existing comics.
     * 2. Scan for and upsert comics.
     * 3. Overwrite all XML/scraper data.
     * 4. Remove each existing/updated comic from the list of 1.
     * 5. Finally, remove any remaining comic in the list of 1.
     *
     * @param emitters Emitter object to report scan progress on.
     */
    public void scanComics(final List<SseEmitter> emitters) {
        this.emitters = emitters;

        final Path comicsPath = Paths.get(this.preferencesService.get("comics.path"));

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
                    .map(path -> path.toString())
                    .collect(Collectors.toList());
            comicRepository.findAll().stream()
                    .filter(comic -> !comicFilePaths.contains(comic.getPath()))
                    .forEach(comic -> {
                        comicRepository.delete(comic);
                    });
        } catch (final IOException e) {
            e.printStackTrace();
            reportError(e.getMessage());
        }

        reportFinish();
        emitters.forEach(emitter -> {
            emitter.complete();
        });
    }
}
