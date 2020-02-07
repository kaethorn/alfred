package de.wasenweg.alfred.thumbnails;

import de.wasenweg.alfred.thumbnails.Thumbnail.ThumbnailType;
import de.wasenweg.alfred.util.BaseController;

import org.bson.types.ObjectId;
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
  private ThumbnailRepository thumbnailRepository;

  @GetMapping("/front-cover/{comicId}")
  public Resource<Thumbnail> findFrontCoverByComicId(@PathVariable("comicId") final String comicId) {
    return this.wrap(this.thumbnailRepository.findByComicIdAndType(new ObjectId(comicId), ThumbnailType.FRONT_COVER));
  }

  @GetMapping("/back-cover/{comicId}")
  public Resource<Thumbnail> findBackCoverByComicId(@PathVariable("comicId") final String comicId) {
    return this.wrap(this.thumbnailRepository.findByComicIdAndType(new ObjectId(comicId), ThumbnailType.BACK_COVER));
  }
}
