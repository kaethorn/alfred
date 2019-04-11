package de.wasenweg.komix.comics;

import java.util.List;
import java.util.Optional;

public interface ComicQueryRepository {

    Optional<Comic> findLastReadForVolume(
            final String userName,
            final String publisher,
            final String series,
            final String volume);

    List<Comic> findAllLastReadPerVolume(final String userName);
}
