package de.wasenweg.alfred.comics;

import de.wasenweg.alfred.publishers.Publisher;
import de.wasenweg.alfred.series.Series;
import de.wasenweg.alfred.volumes.Volume;

import java.util.List;
import java.util.Optional;

public interface ComicQueryRepository {

  Optional<Comic> findById(String userId, String comicId);

  Optional<Comic> findLastReadForVolume(
      String userId,
      String publisher,
      String series,
      String volume);

  List<Comic> findAllLastReadPerVolume(String userId);

  List<Comic> findAllByPublisherAndSeriesAndVolumeOrderByPosition(
      String userId,
      String publisher,
      String series,
      String volume);

  List<Comic> findAllWithErrors();

  List<Comic> findAllWithoutErrors(String publisher, String series, String volume);

  List<Publisher> findAllPublishers(String userId);

  List<Series> findAllSeries(String userId, String publisher);

  List<Volume> findAllVolumes(String userId, String publisher, String series);
}
