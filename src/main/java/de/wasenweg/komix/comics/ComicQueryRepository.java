package de.wasenweg.komix.comics;

import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ComicQueryRepository {

    List<Comic> findAllLastReadPerVolume();

    Comic findLastReadForVolume(
            @Param("publisher") final String publisher,
            @Param("series") final String series,
            @Param("volume") final String volume);
}
