package de.wasenweg.alfred.comics;

import de.wasenweg.alfred.util.BaseController;
import lombok.RequiredArgsConstructor;
import org.springframework.data.repository.query.Param;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

import java.security.Principal;

@RestController
@RequestMapping(value = "/api/comics", produces = { "application/hal+json" })
@RequiredArgsConstructor
public class ComicController extends BaseController<Comic> {

  private final ComicService comicService;

  @GetMapping("")
  public CollectionModel<EntityModel<Comic>> findAll() {
    return this.wrap(this.comicService.findAll());
  }

  @GetMapping("/{comicId}")
  public EntityModel<Comic> findById(
      final Principal principal,
      @PathVariable("comicId") final String comicId) {
    return this.wrap(this.comicService.findById(principal.getName(), comicId));
  }

  @PutMapping("")
  public EntityModel<Comic> update(@Valid @RequestBody final Comic comic) {
    return this.wrap(this.comicService.update(comic));
  }

  @PutMapping("/progress")
  public EntityModel<Comic> updateProgress(@Valid @RequestBody final Comic comic) {
    return this.wrap(this.comicService.updateProgress(comic));
  }

  @PutMapping("/scrape")
  public EntityModel<Comic> scrape(@Valid @RequestBody final Comic comic) {
    return this.wrap(this.comicService.scrape(comic));
  }

  @GetMapping("/search/findAllLastReadPerVolume")
  public CollectionModel<EntityModel<Comic>> findAllLastReadPerVolume(final Principal principal) {
    return this.wrap(this.comicService.findAllLastReadPerVolume(principal.getName()));
  }

  @GetMapping("/search/findAllByOrderByPublisherAscSeriesAscVolumeAscPositionAsc")
  public CollectionModel<EntityModel<Comic>> findAllByOrderByPublisherAscSeriesAscVolumeAscPositionAsc() {
    return this.wrap(this.comicService.findAllByOrderByPublisherAscSeriesAscVolumeAscPositionAsc());
  }

  @GetMapping("/search/findLastReadForVolume")
  public EntityModel<Comic> findLastReadForVolume(
      final Principal principal,
      @Param("publisher") final String publisher,
      @Param("series") final String series,
      @Param("volume") final String volume) {
    return this.wrap(this.comicService.findLastReadForVolume(principal.getName(), publisher, series, volume));
  }

  @GetMapping("/search/findAllByPublisherAndSeriesAndVolumeOrderByPosition")
  public CollectionModel<EntityModel<Comic>> findAllByPublisherAndSeriesAndVolumeOrderByPosition(
      final Principal principal,
      @Param("publisher") final String publisher,
      @Param("series") final String series,
      @Param("volume") final String volume) {
    return this.wrap(this.comicService.findAllByPublisherAndSeriesAndVolumeOrderByPosition(
        principal.getName(), publisher, series, volume));
  }

  @PutMapping("/markAsRead")
  public EntityModel<Comic> markAsRead(@Valid @RequestBody final Comic comic, final Principal principal) {
    return this.wrap(this.comicService.markAsRead(comic, principal.getName()));
  }

  @PutMapping("/markAsUnread")
  public EntityModel<Comic> markAsUnread(@Valid @RequestBody final Comic comic, final Principal principal) {
    return this.wrap(this.comicService.markAsUnread(comic, principal.getName()));
  }

  @DeleteMapping("")
  public void deleteComics() {
    this.comicService.deleteComics();
  }

  @DeleteMapping("/{comicId}/page/{filePath}")
  public EntityModel<Comic> deletePage(
      @PathVariable("comicId") final String comicId,
      @PathVariable("filePath") final String filePath) {
    return this.wrap(this.comicService.deletePage(comicId, filePath));
  }

  @GetMapping("/bundle")
  public void bundle() {
    this.comicService.bundle();
  }
}
