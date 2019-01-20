package de.wasenweg.komix.scanner;

import de.wasenweg.komix.Comic;
import de.wasenweg.komix.ComicRepository;
import de.wasenweg.komix.preferences.PreferenceRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;

@RequestMapping("${spring.data.rest.base-path}")
@RestController
public class ScannerController {

    private final List<SseEmitter> emitters = new ArrayList<>();

    private SseEmitter emitter;

    @Autowired
    private ComicRepository comicRepository;

    @Autowired
    private PreferenceRepository preferenceRepository;

    @GetMapping("/scan-progress")
    public SseEmitter streamScanProgress() {
        emitter = new SseEmitter();
        this.emitters.add(emitter);
        emitter.onCompletion(() -> this.emitters.remove(emitter));
        emitter.onTimeout(() -> {
            emitter.complete();
            this.emitters.remove(emitter);
        });
        return emitter;
    }

    @RequestMapping("/scan")
    @ResponseBody
    public void scan() {
        final String comicsPath = preferenceRepository.findByKey("comics.path").getValue();
        Executors.newScheduledThreadPool(1).execute(() -> {
            final Scanner scanner = new Scanner(emitter, comicsPath);
            final List<Comic> comics = scanner.run();
            comicRepository.saveAll(comics);
            scanner.reportFinish();
        });
    }
}
