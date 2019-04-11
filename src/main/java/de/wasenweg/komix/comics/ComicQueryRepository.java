package de.wasenweg.komix.comics;

import java.util.List;
import java.util.Optional;

public interface ComicQueryRepository {

    Optional<ComicDTO> findLastReadForVolume(
            final String userId,
            final String publisher,
            final String series,
            final String volume);

    List<ComicDTO> findAllLastReadPerVolume(final String userId);
}
