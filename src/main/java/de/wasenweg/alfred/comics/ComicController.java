package de.wasenweg.alfred.comics;

import de.wasenweg.alfred.util.BaseController;
import lombok.RequiredArgsConstructor;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

import java.security.Principal;

@RestController
@RequestMapping(value = "/api/comics", produces = { "application/hal+json" })
@RequiredArgsConstructor
public class ComicController extends BaseController<Comic> {

  private final ComicService comicService;

  @GetMapping
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
  public EntityModel<Comic> updateProgress(@Valid @RequestBody final Comic comic, final Principal principal) {
    return this.wrap(this.comicService.updateProgress(comic, principal.getName()));
  }

  @PutMapping("/scrape")
  public EntityModel<Comic> scrape(@Valid @RequestBody final Comic comic) {
    return this.wrap(this.comicService.scrape(comic));
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

  @DeleteMapping("/{comicId}/page")
  public EntityModel<Comic> deletePage(
      @PathVariable("comicId") final String comicId,
      @RequestParam("path") final String path) {
    return this.wrap(this.comicService.deletePage(comicId, path));
  }

  @PutMapping("/bundle")
  public void bundle() {
    this.comicService.bundle();
  }
}
