package de.wasenweg.alfred.comics;

import de.wasenweg.alfred.util.BaseController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
import org.springframework.hateoas.Resource;
import org.springframework.hateoas.Resources;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

import java.security.Principal;

@RequestMapping(value = "/api/comics", produces = { "application/hal+json" })
@RestController
public class ComicController extends BaseController<Comic> {

  @Autowired
  private ComicService comicService;

  @GetMapping("")
  public Resources<Resource<Comic>> findAll() {
    return this.wrap(this.comicService.findAll());
  }

  @GetMapping("/{comicId}")
  public Resource<Comic> findById(
      final Principal principal,
      @PathVariable("comicId") final String comicId) {
    return this.wrap(this.comicService.findById(principal.getName(), comicId));
  }

  @PutMapping("")
  public Resource<Comic> update(@Valid @RequestBody final Comic comic) {
    return this.wrap(this.comicService.update(comic));
  }

  @PutMapping("/progress")
  public Resource<Comic> updateProgress(@Valid @RequestBody final Comic comic) {
    return this.wrap(this.comicService.updateProgress(comic));
  }

  @PutMapping("/scrape")
  public Resource<Comic> scrape(@Valid @RequestBody final Comic comic) {
    return this.wrap(this.comicService.scrape(comic));
  }

  @GetMapping("/search/findAllLastReadPerVolume")
  public Resources<Resource<Comic>> findAllLastReadPerVolume(final Principal principal) {
    return this.wrap(this.comicService.findAllLastReadPerVolume(principal.getName()));
  }

  @GetMapping("/search/findAllByOrderByPublisherAscSeriesAscVolumeAscPositionAsc")
  public Resources<Resource<Comic>> findAllByOrderByPublisherAscSeriesAscVolumeAscPositionAsc() {
    return this.wrap(this.comicService.findAllByOrderByPublisherAscSeriesAscVolumeAscPositionAsc());
  }

  @GetMapping("/search/findLastReadForVolume")
  public Resource<Comic> findLastReadForVolume(
      final Principal principal,
      @Param("publisher") final String publisher,
      @Param("series") final String series,
      @Param("volume") final String volume) {
    return this.wrap(this.comicService.findLastReadForVolume(principal.getName(), publisher, series, volume));
  }

  @GetMapping("/search/findAllByPublisherAndSeriesAndVolumeOrderByPosition")
  public Resources<Resource<Comic>> findAllByPublisherAndSeriesAndVolumeOrderByPosition(
      final Principal principal,
      @Param("publisher") final String publisher,
      @Param("series") final String series,
      @Param("volume") final String volume) {
    return this.wrap(this.comicService.findAllByPublisherAndSeriesAndVolumeOrderByPosition(
        principal.getName(), publisher, series, volume));
  }

  @PutMapping("/markAsRead")
  public Resource<Comic> markAsRead(@Valid @RequestBody final Comic comic, final Principal principal) {
    return this.wrap(this.comicService.markAsRead(comic, principal.getName()));
  }

  @PutMapping("/markAsUnread")
  public Resource<Comic> markAsUnread(@Valid @RequestBody final Comic comic, final Principal principal) {
    return this.wrap(this.comicService.markAsUnread(comic, principal.getName()));
  }

  @DeleteMapping("")
  public void deleteComics() {
    this.comicService.deleteComics();
  }

  @DeleteMapping("/{comicId}/page/{filePath}")
  public Resource<Comic> deletePage(
      @PathVariable("comicId") final String comicId,
      @PathVariable("filePath") final String filePath) {
    return this.wrap(this.comicService.deletePage(comicId, filePath));
  }

  @GetMapping("/bundle")
  public void bundle() {
    this.comicService.bundle();
  }
}
