package de.wasenweg.alfred.publisher;

import de.wasenweg.alfred.volumes.Volume;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PublisherService {

  @Autowired
  private PublisherQueryRepositoryImpl repository;

  public List<Publisher> findAllPublishers(final String userId) {
    return this.repository.findAllPublishers(userId);
  }

  public List<Series> findAllSeriesByPublisher(final String userId, final String publisher) {
    return this.repository.findAllSeries(userId, publisher);
  }

  public List<Volume> findAllVolumesByPublisherAndSeries(final String userId, final String publisher, final String series) {
    return this.repository.findAllVolumes(userId, publisher, series);
  }
}