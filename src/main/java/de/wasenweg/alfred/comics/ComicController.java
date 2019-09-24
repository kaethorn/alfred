package de.wasenweg.alfred.comics;

import de.wasenweg.alfred.progress.ProgressService;
import de.wasenweg.alfred.scanner.ScannerService;
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
import java.util.Optional;

@RequestMapping(value = "/api/comics", produces = { "application/hal+json" })
@RestController
public class ComicController extends BaseController<Comic> {

  @Autowired
  private ProgressService progressService;

  @Autowired
  private ScannerService scannerService;

  @Autowired
  private ComicQueryRepositoryImpl queryRepository;

  @Autowired
  private ComicRepository comicRepository;

  @GetMapping("")
  public Resources<Resource<Comic>> findAll() {
    return this.wrap(this.comicRepository.findAll());
  }

  @GetMapping("/{comicId}")
  public Resource<Comic> findById(
      final Principal principal,
      @PathVariable("comicId") final String comicId) {
    return this.wrap(this.queryRepository.findById(principal.getName(), comicId));
  }

  @PutMapping("")
  public Resource<Comic> update(@Valid @RequestBody final Comic comic) {
    // TODO:
    // * update `position` field if `number` changes
    // * persist changes in XML and DB (implicit?)
    // * re-validate (in order to purge `errors`)
    return this.wrap(this.comicRepository.save(comic));
  }

  @GetMapping("/search/findAllLastReadPerVolume")
  public Resources<Resource<Comic>> findAllLastReadPerVolume(final Principal principal) {
    return this.wrap(this.queryRepository.findAllLastReadPerVolume(principal.getName()));
  }

  @GetMapping("/search/findLastReadForVolume")
  public Resource<Comic> findLastReadForVolume(
      final Principal principal,
      @Param("publisher") final String publisher,
      @Param("series") final String series,
      @Param("volume") final String volume) {
    return this.wrap(this.queryRepository.findLastReadForVolume(principal.getName(), publisher, series, volume));
  }

  @GetMapping("/search/findAllByPublisherAndSeriesAndVolumeOrderByPosition")
  public Resources<Resource<Comic>> findAllByPublisherAndSeriesAndVolumeOrderByPosition(
      final Principal principal,
      @Param("publisher") final String publisher,
      @Param("series") final String series,
      @Param("volume") final String volume) {
    return this.wrap(this.queryRepository.findAllByPublisherAndSeriesAndVolumeOrderByPosition(
        principal.getName(), publisher, series, volume));
  }

  @PutMapping("/markAsRead")
  public Resource<Comic> markAsRead(@Valid @RequestBody final Comic comic, final Principal principal) {
    return this.wrap(Optional.ofNullable(this.progressService.updateComic(principal.getName(), comic, true)));
  }

  @PutMapping("/markAsUnread")
  public Resource<Comic> markAsUnread(@Valid @RequestBody final Comic comic, final Principal principal) {
    return this.wrap(Optional.ofNullable(this.progressService.updateComic(principal.getName(), comic, false)));
  }

  @DeleteMapping("")
  public void deleteComics() {
    this.comicRepository.deleteAll();
  }

  @GetMapping("/bundle")
  public void bundle() {
    this.scannerService.associateVolumes();
  }
}
