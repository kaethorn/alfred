package de.wasenweg.alfred.comics;

import de.wasenweg.alfred.publishers.Publisher;
import de.wasenweg.alfred.series.Series;
import de.wasenweg.alfred.volumes.Volume;

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

  List<Comic> findAllWithoutErrors(final String publisher, final String series, final String volume);

  List<Publisher> findAllPublishers(final String userId);

  List<Series> findAllSeries(final String userId, final String publisher);

  List<Volume> findAllVolumes(final String userId, final String publisher, final String series);
}
