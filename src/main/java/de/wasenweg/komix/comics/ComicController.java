package de.wasenweg.komix.comics;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;
import java.util.List;
import java.util.Optional;

@RequestMapping("/api/comics")
@RestController
public class ComicController {

    @Autowired
    private ComicQueryRepositoryImpl repository;

    @GetMapping("/search/findAllLastReadPerVolume")
    public List<ComicDTO> findAllLastReadPerVolume(final Principal principal) {
        return this.repository.findAllLastReadPerVolume(principal.getName());
    }

    @GetMapping("/search/findLastReadForVolume")
    public Optional<ComicDTO> findLastReadForVolume(
            final Principal principal,
            @Param("publisher") final String publisher,
            @Param("series") final String series,
            @Param("volume") final String volume) {
        return this.repository.findLastReadForVolume(principal.getName(), publisher, series, volume);
    }
}
