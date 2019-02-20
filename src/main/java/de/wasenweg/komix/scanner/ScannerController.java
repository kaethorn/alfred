package de.wasenweg.komix.scanner;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.Semaphore;

@RestController
@RequestMapping("/api")
public class ScannerController {

    private final Semaphore running = new Semaphore(1);

    private final List<SseEmitter> emitters = new ArrayList<>();

    @Autowired
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

        if (running.tryAcquire()) {
            Executors.newSingleThreadExecutor().execute(() -> {
                scannerService.scanComics(emitters);
                running.release();
            });
        } else {
            emitter.complete();
        }

        return emitter;
    }
}
