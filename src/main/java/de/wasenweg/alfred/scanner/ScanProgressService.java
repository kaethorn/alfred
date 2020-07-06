package de.wasenweg.alfred.scanner;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import de.wasenweg.alfred.comics.Comic;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.stereotype.Service;
import reactor.core.publisher.EmitterProcessor;
import reactor.core.publisher.Flux;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
public class ScanProgressService {

  private EmitterProcessor<ServerSentEvent<String>> emitter;
  private ScanProgress scanProgress;
  private final ObjectMapper objectMapper;
  private final ScanProgressRepository scanProgressRepository;

  public void reportStart(final String path) {
    this.sendEvent("start", ScanProgress.Status.START.toString());
    log.info("Reading comics in {}", path);
    this.scanProgress = this.scanProgressRepository.save(ScanProgress.builder()
        .started(new Date())
        .status(ScanProgress.Status.START)
        .build());
  }

  public void reportProgress(final String path) {
    this.sendEvent(path, ScanProgress.Status.CURRENT_FILE.toString());
    log.debug("Parsing comic {}", path);
  }

  public void reportTotal(final int total) {
    this.sendEvent(String.valueOf(total), ScanProgress.Status.TOTAL.toString());
    log.info("Found {} comics.", total);
    this.scanProgress.setCount(total);
    this.scanProgressRepository.save(this.scanProgress);
  }

  public void reportCleanUp() {
    this.sendEvent("cleanUp", ScanProgress.Status.CLEAN_UP.toString());
    log.info("Purging orphaned comics.");
  }

  public void reportAssociation() {
    this.sendEvent("association", ScanProgress.Status.ASSOCIATION.toString());
    log.info("Associating volumes.");
  }

  public void reportFinish() {
    this.sendEvent("done", "done");
    this.emitter.onComplete();
    this.scanProgress.setStatus(ScanProgress.Status.DONE);
    this.scanProgress.setFinished(new Date());
    this.scanProgressRepository.save(this.scanProgress);
  }

  public void reportIssue(final Exception exception) {
    log.error(exception.getLocalizedMessage(), exception);
    this.reportIssue(
        ScannerIssue.builder()
            .message(exception.getLocalizedMessage())
            .severity(ScannerIssue.Severity.ERROR)
            .build());
  }

  public void reportIssue(final Comic comic, final Exception exception) {
    log.error(exception.getLocalizedMessage());
    this.reportIssue(
        comic,
        ScannerIssue.builder()
            .message(exception.getLocalizedMessage())
            .severity(ScannerIssue.Severity.ERROR)
            .build());
  }

  public void reportIssue(final Comic comic, final Exception exception, final ScannerIssue.Severity severity) {
    log.error(exception.getLocalizedMessage(), exception);
    this.reportIssue(
        comic,
        ScannerIssue.builder()
            .message(exception.getLocalizedMessage())
            .severity(severity)
            .build());
  }

  public void reportIssue(final Comic comic, final ScannerIssue issue) {
    final List<ScannerIssue> errors = Optional
        .ofNullable(comic.getErrors())
        .orElse(new ArrayList<>());
    errors.add(issue);
    comic.setErrors(errors);
    this.reportIssue(issue);
  }

  public void reportIssue(final ScannerIssue issue) {
    try {
      this.sendEvent(this.objectMapper.writeValueAsString(issue), ScanProgress.Status.SCAN_ISSUE.toString());
    } catch (final JsonProcessingException exception) {
      log.error("Error while transmitting scanning issue.", exception);
    }
  }

  public void createEmitter() {
    this.emitter = EmitterProcessor.create();
  }

  public Flux<ServerSentEvent<String>> logEmitter() {
    return this.emitter.log();
  }

  private void sendEvent(final String data, final String name) {
    final String timestamp = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US).format(new Date());
    this.emitter.onNext(ServerSentEvent.builder(data).id(timestamp).event(name).build());
  }
}
