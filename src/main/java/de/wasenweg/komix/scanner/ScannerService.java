package de.wasenweg.komix.scanner;

import de.wasenweg.komix.comics.Comic;
import de.wasenweg.komix.comics.ComicRepository;
import de.wasenweg.komix.preferences.PreferenceRepository;

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

    private final PreferenceRepository preferenceRepository;

    private final ComicRepository comicRepository;

    @Autowired
    public ScannerService(final ComicRepository comicRepository, final PreferenceRepository preferenceRepository) {
        this.comicRepository = comicRepository;
        this.preferenceRepository = preferenceRepository;
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

        final Comic comic = new Comic(path.toAbsolutePath().toString(), "", "", "", "0.0", (short) 0, (short) 0, "");

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

        List<Comic> comics = null;

        final String comicsPath = preferenceRepository.findByKey("comics.path").getValue();
        final Path root = Paths.get(comicsPath);

        List<Path> comicFiles = null;

        try (Stream<Path> files = Files.walk(root)) {
            comicFiles = files.filter(path -> Files.isRegularFile(path))
                    .filter(path -> path.getFileName().toString().endsWith(".cbz")).collect(Collectors.toList());

            reportTotal(comicFiles.size());

            comics = comicFiles.stream()
                    .map(path -> createComic(path))
                    .filter(path -> !path.getTitle().isEmpty())
                    .collect(Collectors.toList());
        } catch (final IOException e) {
            e.printStackTrace();
            reportError(e.getMessage());
        }

        comicRepository.deleteAll();
        comicRepository.saveAll(comics);
        reportFinish();
        emitters.forEach(emitter -> {
            emitter.complete();
        });
    }
}
