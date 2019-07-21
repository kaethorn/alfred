package de.wasenweg.alfred.scanner;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import reactor.core.publisher.Flux;

@RestController
@RequestMapping("/api")
public class ScannerController {

  private Logger logger = LoggerFactory.getLogger(ScannerController.class);

  @Autowired
  private ScannerService scannerService;

  @GetMapping("/scan-progress")
  Flux<ServerSentEvent<String>> streamScanProgress() {
    this.logger.info("Triggered scan-progress.");
    return this.scannerService.scanComics();
  }
}