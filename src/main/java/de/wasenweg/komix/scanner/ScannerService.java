package de.wasenweg.komix.scanner;

import de.wasenweg.komix.comics.Comic;
import de.wasenweg.komix.comics.ComicRepository;
import de.wasenweg.komix.preferences.PreferencesService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter.SseEventBuilder;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.zip.ZipFile;

@Service
public class ScannerService {

    private List<SseEmitter> emitters = new ArrayList<>();

    private final ComicRepository comicRepository;

    private final PreferencesService preferencesService;

    @Autowired
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

    private Comic createComic(final Path path) {
        reportProgress(path.toString());

        final Comic comic = new Comic();
        comic.setPath(path.toAbsolutePath().toString());

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

    public void scanComics(final List<SseEmitter> emitters) {
        this.emitters = emitters;

        final String comicsPath = this.preferencesService.get("comics.path");
        final Path root = Paths.get(comicsPath);

        List<Path> comicFiles = null;

        comicRepository.deleteAll();

        try (Stream<Path> files = Files.walk(root)) {
            comicFiles = files.filter(path -> Files.isRegularFile(path))
                    .filter(path -> path.getFileName().toString().endsWith(".cbz"))
                    .collect(Collectors.toList());

            reportTotal(comicFiles.size());

            comicFiles.stream()
                    .map(path -> createComic(path))
                    .filter(path -> !path.getTitle().isEmpty())
                    .forEach(comic -> {
                        comicRepository.save(comic);
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
