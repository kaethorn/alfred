package de.wasenweg.alfred.queue;

import de.wasenweg.alfred.comics.Comic;
import de.wasenweg.alfred.util.BaseController;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/queue")
@RequiredArgsConstructor
public class QueueController extends BaseController<Comic> {

  @Autowired
  private QueueService queueService;

  @GetMapping
  public CollectionModel<EntityModel<Comic>> get() {
    return this.wrap(this.queueService.get());
  }

  @GetMapping("/valid")
  public CollectionModel<EntityModel<Comic>> getValid() {
    return this.wrap(this.queueService.getValid());
  }
}
