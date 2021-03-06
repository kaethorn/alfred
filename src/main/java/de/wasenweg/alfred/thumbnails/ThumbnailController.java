package de.wasenweg.alfred.thumbnails;

import de.wasenweg.alfred.util.BaseController;
import lombok.RequiredArgsConstructor;
import org.springframework.hateoas.EntityModel;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/api/thumbnails", produces = { "application/hal+json" })
@RequiredArgsConstructor
public class ThumbnailController extends BaseController<Thumbnail> {

  private final ThumbnailService thumbnailService;

  @GetMapping("/front-cover/{comicId}")
  public EntityModel<Thumbnail> findFrontCoverByComicId(@PathVariable("comicId") final String comicId) {
    return this.wrap(this.thumbnailService.findFrontCoverByComicId(comicId));
  }

  @GetMapping("/back-cover/{comicId}")
  public EntityModel<Thumbnail> findBackCoverByComicId(@PathVariable("comicId") final String comicId) {
    return this.wrap(this.thumbnailService.findBackCoverByComicId(comicId));
  }
}
