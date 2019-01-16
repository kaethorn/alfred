package de.wasenweg.comix.scanner;

import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import de.wasenweg.comix.Comic;
import de.wasenweg.comix.ComicRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@RequestMapping("${spring.data.rest.base-path}")
@RestController
public class ScannerController {

	private final List<SseEmitter> emitters = new ArrayList<>();

	private SseEmitter emitter;

	@Autowired
	private ComicRepository comicRepository;

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
		Executors.newScheduledThreadPool(1).execute(() -> {
			Scanner scanner = new Scanner(emitter);
			List<Comic> comics = scanner.run();
			comicRepository.saveAll(comics);
			scanner.reportFinish();
		});
	}
}