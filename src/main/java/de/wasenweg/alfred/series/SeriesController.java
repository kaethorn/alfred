package de.wasenweg.alfred.series;

import de.wasenweg.alfred.volumes.VolumesController;
import lombok.RequiredArgsConstructor;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;
import java.util.stream.Collectors;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
@RequestMapping(value = "/api/series", produces = { "application/hal+json" })
@RequiredArgsConstructor
public class SeriesController {

  private final SeriesService seriesService;

  @GetMapping
  public CollectionModel<EntityModel<Series>> findAllSeriesByPublisher(
      final Principal principal,
      @RequestParam("publisher") final String publisher) {
    return CollectionModel.of(
        this.seriesService.findAllSeriesByPublisher(principal.getName(), publisher).stream().map(serie -> {
          return EntityModel.of(
              serie,
              linkTo(methodOn(VolumesController.class)
                  .findAllVolumesByPublisherAndSeries(principal, publisher, serie.getName())).withSelfRel()
          );
        }).collect(Collectors.toList()),
        linkTo(methodOn(SeriesController.class).findAllSeriesByPublisher(principal, publisher)).withSelfRel()
    );
  }
}
