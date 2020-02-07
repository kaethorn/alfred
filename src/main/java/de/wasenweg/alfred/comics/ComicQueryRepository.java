package de.wasenweg.alfred.comics;

import java.util.List;
import java.util.Optional;

public interface ComicQueryRepository {

  Optional<Comic> findById(final String userId, final String comicId);

  Optional<Comic> findLastReadForVolume(
      final String userId,
      final String publisher,
      final String series,
      final String volume);

  List<Comic> findAllLastReadPerVolume(final String userId);

  List<Comic> findAllByPublisherAndSeriesAndVolumeOrderByPosition(
      final String userId,
      final String publisher,
      final String series,
      final String volume);

  List<Comic> findAllWithErrors();

  List<Comic> findAllWithoutErrors();
}
