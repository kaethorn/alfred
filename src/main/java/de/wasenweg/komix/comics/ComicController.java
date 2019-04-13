package de.wasenweg.komix.comics;

import de.wasenweg.komix.progress.ProgressService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

import java.security.Principal;
import java.util.List;
import java.util.Optional;

@RequestMapping("/api/comics")
@RestController
public class ComicController {

    @Autowired
    private ProgressService progressService;

    @Autowired
    private ComicQueryRepositoryImpl queryRepository;

    @GetMapping("/search/findAllLastReadPerVolume")
    public List<Comic> findAllLastReadPerVolume(final Principal principal) {
        return this.queryRepository.findAllLastReadPerVolume(principal.getName());
    }

    @GetMapping("/search/findLastReadForVolume")
    public Optional<Comic> findLastReadForVolume(
            final Principal principal,
            @Param("publisher") final String publisher,
            @Param("series") final String series,
            @Param("volume") final String volume) {
        return this.queryRepository.findLastReadForVolume(principal.getName(), publisher, series, volume);
    }

    @GetMapping("/search/findAllByPublisherAndSeriesAndVolumeOrderByPosition")
    public List<Comic> findAllByPublisherAndSeriesAndVolumeOrderByPosition(
            final Principal principal,
            @Param("publisher") final String publisher,
            @Param("series") final String series,
            @Param("volume") final String volume) {
        return this.queryRepository.findAllByPublisherAndSeriesAndVolumeOrderByPosition(
                principal.getName(), publisher, series, volume);
    }

    @PutMapping("/markAsRead")
    public Optional<Comic> markAsRead(@Valid @RequestBody final Comic comic, final Principal principal) {
        return Optional.ofNullable(this.progressService.updateComic(principal.getName(), comic, true));
    }

    @PutMapping("/markAsUnread")
    public Optional<Comic> markAsUnread(@Valid @RequestBody final Comic comic, final Principal principal) {
        return Optional.ofNullable(this.progressService.updateComic(principal.getName(), comic, false));
    }
}
