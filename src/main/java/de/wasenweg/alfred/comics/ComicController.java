package de.wasenweg.alfred.comics;

import de.wasenweg.alfred.progress.ProgressService;
import de.wasenweg.alfred.scanner.ApiMetaDataService;
import de.wasenweg.alfred.scanner.FileMetaDataService;
import de.wasenweg.alfred.scanner.ScannerIssue;
import de.wasenweg.alfred.scanner.ScannerService;
import de.wasenweg.alfred.util.BaseController;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;
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
import java.util.List;
import java.util.Optional;

@RequestMapping(value = "/api/comics", produces = { "application/hal+json" })
@RestController
public class ComicController extends BaseController<Comic> {

  @Autowired
  private ProgressService progressService;

  @Autowired
  private ScannerService scannerService;

  @Autowired
  private FileMetaDataService fileMetaDataService;

  @Autowired
  private ApiMetaDataService apiMetaDataService;

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
    this.comicRepository.save(comic);
    this.fileMetaDataService.write(comic);
    comic.getErrors().clear();
    this.scannerService.processComic(comic);
    return this.wrap(comic);
  }

  @PutMapping("/scrape")
  public Resource<Comic> scrape(@Valid @RequestBody final Comic comic) {
    final List<ScannerIssue> issues = this.apiMetaDataService.set(comic);
    if (issues.size() > 0) {
      throw new ResourceNotFoundException("Error while querying ComicVine.");
    }
    this.comicRepository.save(comic);
    return this.wrap(comic);
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
