package de.wasenweg.alfred.comics;

import de.wasenweg.alfred.progress.ProgressService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.Resource;
import org.springframework.hateoas.Resources;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

import java.security.Principal;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;

@RequestMapping(value = "/api/comics", produces = { "application/hal+json" })
@RestController
public class ComicController {

  @Autowired
  private ProgressService progressService;

  @Autowired
  private ComicQueryRepositoryImpl queryRepository;

  @Autowired
  private ComicRepository comicRepository;

  @GetMapping("/{comicId}")
  public Resource<Comic> findById(@PathVariable("comicId") final String comicId) {
    return addLink(this.comicRepository.findById(comicId));
  }

  @GetMapping("/search/findAllLastReadPerVolume")
  public Resources<Resource<Comic>> findAllLastReadPerVolume(final Principal principal) {
    return addCollectionLink(this.queryRepository.findAllLastReadPerVolume(principal.getName()));
  }

  @GetMapping("/search/findLastReadForVolume")
  public Resource<Comic> findLastReadForVolume(
      final Principal principal,
      @Param("publisher") final String publisher,
      @Param("series") final String series,
      @Param("volume") final String volume) {
    return addLink(this.queryRepository.findLastReadForVolume(principal.getName(), publisher, series, volume));
  }

  @GetMapping("/search/findAllByPublisherAndSeriesAndVolumeOrderByPosition")
  public Resources<Resource<Comic>> findAllByPublisherAndSeriesAndVolumeOrderByPosition(
      final Principal principal,
      @Param("publisher") final String publisher,
      @Param("series") final String series,
      @Param("volume") final String volume) {
    return addCollectionLink(this.queryRepository.findAllByPublisherAndSeriesAndVolumeOrderByPosition(
        principal.getName(), publisher, series, volume));
  }

  @PutMapping("/markAsRead")
  public Resource<Comic> markAsRead(@Valid @RequestBody final Comic comic, final Principal principal) {
    return addLink(Optional.ofNullable(this.progressService.updateComic(principal.getName(), comic, true)));
  }

  @PutMapping("/markAsUnread")
  public Resource<Comic> markAsUnread(@Valid @RequestBody final Comic comic, final Principal principal) {
    return addLink(Optional.ofNullable(this.progressService.updateComic(principal.getName(), comic, false)));
  }

  private Resources<Resource<Comic>> addCollectionLink(final List<Comic> comics) {
    return new Resources<Resource<Comic>>(
        comics.stream()
        .map(comic -> {
          return addLink(comic);
        }).collect(Collectors.toList()),
        linkTo(ComicController.class).withSelfRel());
  }

  private Resource<Comic> addLink(final Optional<Comic> comic) {
    if (comic.isPresent()) {
      return addLink(comic.get());
    } else {
      throw new ResourceNotFoundException();
    }
  }

  private Resource<Comic> addLink(final Comic comic) {
    final Link link = linkTo(ComicController.class).slash(comic.getId()).withSelfRel();
    return new Resource<Comic>(comic, link);
  }
}
