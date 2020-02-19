package de.wasenweg.alfred.stats;

import de.wasenweg.alfred.util.BaseController;
import lombok.RequiredArgsConstructor;
import org.springframework.hateoas.EntityModel;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/api/stats", produces = { "application/hal+json" })
@RequiredArgsConstructor
public class StatsController extends BaseController<Stats> {

  private final StatsService service;

  @GetMapping()
  public EntityModel<Stats> getStats() {
    return this.wrapRoot(this.service.getStats());
  }
}
