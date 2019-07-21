package de.wasenweg.alfred.publisher;

import de.wasenweg.alfred.volumes.Volume;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.Resource;
import org.springframework.hateoas.Resources;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;
import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;

@RestController
@RequestMapping(value = "/api/publishers", produces = { "application/hal+json" })
public class PublisherController {

  @Autowired
  private PublisherQueryRepositoryImpl repository;

  @GetMapping
  public Resources<Resource<Publisher>> findAllPublishers(final Principal principal) {
    final List<Publisher> publishers = this.repository.findAllPublishers(principal.getName());
    return new Resources<Resource<Publisher>>(
        publishers.stream()
        .map(publisher -> {
          return new Resource<Publisher>(
              publisher,
              this.getSeriesLink(publisher.getPublisher()));
        }).collect(Collectors.toList()),
        linkTo(PublisherController.class).withSelfRel());
  }

  @GetMapping("/{publisher}/series")
  public Resources<Resource<Series>> findAllSeriesByPublisher(
      final Principal principal,
      @PathVariable final String publisher) {
    final List<Series> series = this.repository.findAllSeries(principal.getName(), publisher);
    return new Resources<Resource<Series>>(
        series.stream()
        .map(serie -> {
          return new Resource<Series>(
              serie,
              this.getVolumeLink(serie.getPublisher(), serie.getSeries()));
        }).collect(Collectors.toList()),
        this.getSeriesLink(publisher));
  }

  @GetMapping("/{publisher}/series/{series}/volumes")
  public Resources<Volume> findAllVolumesByPublisherAndSeries(
      final Principal principal,
      @PathVariable final String publisher,
      @PathVariable final String series) {
    final List<Volume> volumes = this.repository
        .findAllVolumes(principal.getName(), publisher, series);
    final Link link = linkTo(PublisherController.class)
        .slash(publisher).slash("series").slash(series).slash("volumes").withSelfRel();
    return new Resources<Volume>(volumes, link);
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
