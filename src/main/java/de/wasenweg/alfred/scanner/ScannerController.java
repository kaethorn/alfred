package de.wasenweg.alfred.scanner;

import lombok.RequiredArgsConstructor;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class ScannerController {

  private final ScannerService scannerService;

  @GetMapping("/scan-progress")
  public Flux<ServerSentEvent<String>> streamScanProgress() {
    return this.scannerService.scanComics();
  }
}