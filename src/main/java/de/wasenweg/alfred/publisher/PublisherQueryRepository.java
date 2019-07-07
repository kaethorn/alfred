package de.wasenweg.alfred.publisher;

import de.wasenweg.alfred.volumes.Volume;

import java.util.List;

public interface PublisherQueryRepository {

  List<Publisher> findAllPublishers(final String userId);

  List<Series> findAllSeries(final String userId, final String publisher);

  List<Volume> findAllVolumes(final String userId, final String publisher, final String series);
}
