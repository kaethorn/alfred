package de.wasenweg.alfred.stats;

import de.wasenweg.alfred.util.BaseController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.Resource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping(value = "/api/stats", produces = { "application/hal+json" })
@RestController
public class StatsController extends BaseController<Stats> {

  @Autowired
  private StatsService service;

  @GetMapping()
  public Resource<Stats> getStats() {
    return this.wrapRoot(this.service.getStats());
  }
}
