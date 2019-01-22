package de.wasenweg.komix.scanner;

import de.wasenweg.komix.Comic;
import de.wasenweg.komix.ComicRepository;
import de.wasenweg.komix.preferences.PreferenceRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;

@RequestMapping("${spring.data.rest.base-path}")
@RestController
public class ScannerController {

    private final List<SseEmitter> emitters = new ArrayList<>();

    @Autowired
    private ComicRepository comicRepository;

    @Autowired
    private PreferenceRepository preferenceRepository;

    @GetMapping("/scan-progress")
    public SseEmitter streamScanProgress() {
        final SseEmitter emitter = new SseEmitter(Long.MAX_VALUE);
        this.emitters.add(emitter);

        emitter.onCompletion(() -> this.emitters.remove(emitter));
        emitter.onTimeout(() -> {
            emitter.complete();
            this.emitters.remove(emitter);
        });

        // TODO
        // * Make sure no new task is started if one is already running. Might be
        //   working out of the box. Write a test?
        Executors.newSingleThreadExecutor().execute(() -> {
            final String comicsPath = preferenceRepository.findByKey("comics.path").getValue();
            final ScannerService scanner = new ScannerService(emitters, comicsPath);
            final List<Comic> comics = scanner.run();
            comicRepository.deleteAll();
            comicRepository.saveAll(comics);
            scanner.reportFinish();
        });

        return emitter;
    }
}
