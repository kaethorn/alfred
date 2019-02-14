package de.wasenweg.komix.scanner;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;

@RestController
@RequestMapping("/api")
public class ScannerController {

    private final List<SseEmitter> emitters = new ArrayList<>();

    private ScannerService scannerService;

    @GetMapping("/scan-progress")
    SseEmitter streamScanProgress() {
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
            scannerService.scanComics(emitters);
        });

        return emitter;
    }
}
