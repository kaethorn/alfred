package de.wasenweg.alfred.publishers;

import de.wasenweg.alfred.series.SeriesController;
import lombok.RequiredArgsConstructor;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;
import java.util.stream.Collectors;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
@RequestMapping(value = "/api/publishers", produces = { "application/hal+json" })
@RequiredArgsConstructor
public class PublishersController {

  private final PublishersService publishersService;

  @GetMapping
  public CollectionModel<EntityModel<Publisher>> findAllPublishers(final Principal principal) {
    return CollectionModel.of(
        this.publishersService.findAllPublishers(principal.getName()).stream().map(publisher -> {
          return EntityModel.of(
              publisher,
              linkTo(methodOn(SeriesController.class)
                  .findAllSeriesByPublisher(principal, publisher.getName())).withSelfRel()
          );
        }).collect(Collectors.toList()),
        linkTo(PublishersController.class).slash("publishers").withSelfRel());
  }
}
