package de.wasenweg.alfred.publisher;

import de.wasenweg.alfred.volumes.Volume;
import lombok.RequiredArgsConstructor;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;
import java.util.stream.Collectors;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;

@RestController
@RequestMapping(value = "/api/publishers", produces = { "application/hal+json" })
@RequiredArgsConstructor
public class PublisherController {

  private final PublisherService publisherService;

  @GetMapping
  public CollectionModel<EntityModel<Publisher>> findAllPublishers(final Principal principal) {
    return new CollectionModel<EntityModel<Publisher>>(
        this.publisherService.findAllPublishers(principal.getName()).stream().map(publisher -> {
          return new EntityModel<Publisher>(publisher, this.getSeriesLink(publisher.getPublisher()));
        }).collect(Collectors.toList()),
        linkTo(PublisherController.class).withSelfRel());
  }

  @GetMapping("/{publisher}/series")
  public CollectionModel<EntityModel<Series>> findAllSeriesByPublisher(
      final Principal principal,
      @PathVariable final String publisher) {
    return new CollectionModel<EntityModel<Series>>(
        this.publisherService.findAllSeriesByPublisher(principal.getName(), publisher).stream().map(serie -> {
          return new EntityModel<Series>(serie, this.getVolumeLink(serie.getPublisher(), serie.getSeries()));
        }).collect(Collectors.toList()),
        this.getSeriesLink(publisher));
  }

  @GetMapping("/{publisher}/series/{series}/volumes")
  public CollectionModel<Volume> findAllVolumesByPublisherAndSeries(
      final Principal principal,
      @PathVariable final String publisher,
      @PathVariable final String series) {
    return new CollectionModel<Volume>(
        this.publisherService.findAllVolumesByPublisherAndSeries(principal.getName(), publisher, series),
        this.getVolumeLink(publisher, series));
  }

  private Link getSeriesLink(final String publisher) {
    return linkTo(PublisherController.class)
        .slash(publisher).slash("series").withSelfRel();
  }

  private Link getVolumeLink(final String publisher, final String series) {
    return linkTo(PublisherController.class)
        .slash(publisher).slash("series").slash(series).slash("volumes").withSelfRel();
  }
}
