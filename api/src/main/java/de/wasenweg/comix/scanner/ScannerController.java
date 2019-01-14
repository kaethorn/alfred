package de.wasenweg.comix.scanner;

import org.springframework.web.bind.annotation.RestController;

import de.wasenweg.comix.Comic;
import de.wasenweg.comix.ComicRepository;

import java.util.List;
import java.util.concurrent.Executors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import reactor.core.publisher.DirectProcessor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.FluxProcessor;
import reactor.core.publisher.FluxSink;

@RequestMapping("${spring.data.rest.base-path}")
@RestController
public class ScannerController {

	private DirectProcessor<String> directProcessor = DirectProcessor.create();
	private FluxProcessor<String, String> processor = directProcessor.serialize();
	private FluxSink<String> sink = processor.sink();
	
	@Autowired
	private ComicRepository comicRepository;
	
	@GetMapping("/scan-progress")
	public Flux<ServerSentEvent<String>> streamScanProgress() {
		return processor.map(e -> ServerSentEvent.builder(e).build());
	}
	
    @RequestMapping("/scan")
    @ResponseBody
    public void scan() {
		sink.next("Starting");
    	Executors.newScheduledThreadPool(1).execute(() -> {
			Scanner scanner = new Scanner(sink);
			List<Comic> comics = scanner.run();
			comicRepository.saveAll(comics);
			scanner.reportFinish();
    	});
    }
}