package de.wasenweg.komix.scanner;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import reactor.core.publisher.Flux;

@RestController
@RequestMapping("/api")
public class ScannerController {

    @Autowired
    private ScannerService scannerService;

    @GetMapping("/scan-progress")
    Flux<ServerSentEvent<String>> streamScanProgress() {
        return scannerService.scanComics();
    }
}
