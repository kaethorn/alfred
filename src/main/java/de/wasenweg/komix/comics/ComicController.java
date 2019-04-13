package de.wasenweg.komix.comics;

import de.wasenweg.komix.progress.ProgressService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.Resource;
import org.springframework.hateoas.Resources;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

import java.security.Principal;
import java.util.List;
import java.util.Optional;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;

@RequestMapping(value = "/api/comics", produces = { "application/hal+json" })
@RestController
public class ComicController {

    @Autowired
    private ProgressService progressService;

    @Autowired
    private ComicQueryRepositoryImpl queryRepository;

    @GetMapping("/search/findAllLastReadPerVolume")
    public Resources<Comic> findAllLastReadPerVolume(final Principal principal) {
        final List<Comic> comics = this.queryRepository.findAllLastReadPerVolume(principal.getName());
        final Link link = linkTo(ComicController.class).withSelfRel();
        return new Resources<Comic>(comics, link);
    }

    @GetMapping("/search/findLastReadForVolume")
    public Resource<Optional<Comic>> findLastReadForVolume(
            final Principal principal,
            @Param("publisher") final String publisher,
            @Param("series") final String series,
            @Param("volume") final String volume) {
        final Optional<Comic> comic = this.queryRepository.findLastReadForVolume(principal.getName(), publisher, series, volume);
        // FIXME Return 404 when Optional is null
        final Link link = linkTo(ComicController.class).slash(comic.get().getId()).withSelfRel();
        return new Resource<Optional<Comic>>(comic, link);
    }

    @GetMapping("/search/findAllByPublisherAndSeriesAndVolumeOrderByPosition")
    public Resources<Comic> findAllByPublisherAndSeriesAndVolumeOrderByPosition(
            final Principal principal,
            @Param("publisher") final String publisher,
            @Param("series") final String series,
            @Param("volume") final String volume) {
        final List<Comic> comics = this.queryRepository.findAllByPublisherAndSeriesAndVolumeOrderByPosition(
                principal.getName(), publisher, series, volume);
        final Link link = linkTo(ComicController.class).withSelfRel();
        return new Resources<Comic>(comics, link);
    }

    @PutMapping("/markAsRead")
    public Resource<Optional<Comic>> markAsRead(@Valid @RequestBody final Comic comic, final Principal principal) {
        final Optional<Comic> markedComic = Optional.ofNullable(this.progressService.updateComic(principal.getName(), comic, true));
        // FIXME Return 404 when Optional is null
        final Link link = linkTo(ComicController.class).slash(markedComic.get().getId()).withSelfRel();
        return new Resource<Optional<Comic>>(markedComic, link);
    }

    @PutMapping("/markAsUnread")
    public Resource<Optional<Comic>> markAsUnread(@Valid @RequestBody final Comic comic, final Principal principal) {
        final Optional<Comic> markedComic = Optional.ofNullable(this.progressService.updateComic(principal.getName(), comic, false));
        // FIXME Return 404 when Optional is null
        final Link link = linkTo(ComicController.class).slash(markedComic.get().getId()).withSelfRel();
        return new Resource<Optional<Comic>>(markedComic, link);
    }
}
