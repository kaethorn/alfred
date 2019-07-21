package de.wasenweg.alfred.stats;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.Resource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;

@RequestMapping(value = "/api/stats", produces = { "application/hal+json" })
@RestController
public class StatsController {

  @Autowired
  private StatsService service;

  @GetMapping()
  public Resource<Stats> getStats() {
    return this.addLink(this.service.getStats());
  }

  private Resource<Stats> addLink(final Stats stats) {
    final Link link = linkTo(StatsController.class).withSelfRel();
    return new Resource<Stats>(stats, link);
  }
}
