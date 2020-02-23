package de.wasenweg.alfred.comics;

import de.wasenweg.alfred.util.BaseController;
import lombok.RequiredArgsConstructor;
import org.springframework.data.repository.query.Param;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;

@RestController
@RequestMapping(value = "/api/comics/search", produces = { "application/hal+json" })
@RequiredArgsConstructor
public class ComicSearchController extends BaseController<Comic> {

  private final ComicSearchService comicSearchService;

  @GetMapping("/findAllLastReadPerVolume")
  public CollectionModel<EntityModel<Comic>> findAllLastReadPerVolume(final Principal principal) {
    return this.wrap(this.comicSearchService.findAllLastReadPerVolume(principal.getName()));
  }

  @GetMapping("/findAllByOrderByPublisherAscSeriesAscVolumeAscPositionAsc")
  public CollectionModel<EntityModel<Comic>> findAllByOrderByPublisherAscSeriesAscVolumeAscPositionAsc() {
    return this.wrap(this.comicSearchService.findAllByOrderByPublisherAscSeriesAscVolumeAscPositionAsc());
  }

  @GetMapping("/findLastReadForVolume")
  public EntityModel<Comic> findLastReadForVolume(
      final Principal principal,
      @Param("publisher") final String publisher,
      @Param("series") final String series,
      @Param("volume") final String volume) {
    return this.wrap(this.comicSearchService.findLastReadForVolume(principal.getName(), publisher, series, volume));
  }

  @GetMapping("/findAllByPublisherAndSeriesAndVolumeOrderByPosition")
  public CollectionModel<EntityModel<Comic>> findAllByPublisherAndSeriesAndVolumeOrderByPosition(
      final Principal principal,
      @Param("publisher") final String publisher,
      @Param("series") final String series,
      @Param("volume") final String volume) {
    return this.wrap(this.comicSearchService.findAllByPublisherAndSeriesAndVolumeOrderByPosition(
        principal.getName(), publisher, series, volume));
  }
}
