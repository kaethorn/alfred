package de.wasenweg.alfred.queue;

import de.wasenweg.alfred.comics.Comic;
import de.wasenweg.alfred.scanner.ScannerIssue;
import de.wasenweg.alfred.util.BaseController;
import lombok.RequiredArgsConstructor;
import org.springframework.data.repository.query.Param;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

import java.io.IOException;

@RestController
@RequestMapping("/api/queue")
@RequiredArgsConstructor
public class QueueController extends BaseController<Comic> {

  private final QueueService queueService;

  @GetMapping
  public CollectionModel<EntityModel<Comic>> get() {
    return this.wrap(this.queueService.get());
  }

  @GetMapping("/valid")
  public CollectionModel<EntityModel<Comic>> getValid(
      @Param("publisher") final String publisher,
      @Param("series") final String series,
      @Param("volume") final String volume) {
    return this.wrap(this.queueService.getValid(publisher, series, volume));
  }

  @PutMapping("/fix/{errorType}")
  public EntityModel<Comic> fixIssue(@Valid @RequestBody final Comic comic, @PathVariable final ScannerIssue.Type errorType)
      throws IOException {
    if (errorType == ScannerIssue.Type.NOT_FLAT) {
      return this.wrap(this.queueService.flatten(comic));
    }
    return this.wrap(comic);
  }
}
