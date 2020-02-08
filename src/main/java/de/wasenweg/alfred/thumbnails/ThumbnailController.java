package de.wasenweg.alfred.thumbnails;

import de.wasenweg.alfred.util.BaseController;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.Resource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping(value = "/api/thumbnails", produces = { "application/hal+json" })
@RestController
public class ThumbnailController extends BaseController<Thumbnail> {

  @Autowired
  private ThumbnailService thumbnailService;

  @GetMapping("/front-cover/{comicId}")
  public Resource<Thumbnail> findFrontCoverByComicId(@PathVariable("comicId") final String comicId) {
    return this.wrap(this.thumbnailService.findFrontCoverByComicId(comicId));
  }

  @GetMapping("/back-cover/{comicId}")
  public Resource<Thumbnail> findBackCoverByComicId(@PathVariable("comicId") final String comicId) {
    return this.wrap(this.thumbnailService.findBackCoverByComicId(comicId));
  }
}
