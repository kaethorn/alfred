package de.wasenweg.alfred.thumbnails;

import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.Resource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;

@RequestMapping(value = "/api/thumbnails", produces = { "application/hal+json" })
@RestController
public class ThumbnailController {

  @Autowired
  private ThumbnailRepository thumbnailRepository;

  @GetMapping("/{comicId}")
  public Resource<Thumbnail> findByComicId(@PathVariable("comicId") final String comicId) {
    return addLink(this.thumbnailRepository.findByComicId(new ObjectId(comicId)));
  }

  // FIXME: inline these two methods:
  private Resource<Thumbnail> addLink(final Optional<Thumbnail> thumbnail) {
    if (thumbnail.isPresent()) {
      return addLink(thumbnail.get());
    } else {
      throw new ResourceNotFoundException();
    }
  }

  private Resource<Thumbnail> addLink(final Thumbnail thumbnail) {
    final Link link = linkTo(ThumbnailController.class).slash(thumbnail.getId()).withSelfRel();
    return new Resource<Thumbnail>(thumbnail, link);
  }
}
