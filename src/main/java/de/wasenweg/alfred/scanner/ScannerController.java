package de.wasenweg.alfred.scanner;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

@Slf4j
@RestController
@RequestMapping("/api")
public class ScannerController {

  @Autowired
  private ScannerService scannerService;

  @GetMapping("/scan-progress")
  public Flux<ServerSentEvent<String>> streamScanProgress() {
    log.info("Triggered scan-progress.");
    return this.scannerService.scanComics();
  }
}