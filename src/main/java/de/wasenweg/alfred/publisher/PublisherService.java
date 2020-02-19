package de.wasenweg.alfred.publisher;

import de.wasenweg.alfred.volumes.Volume;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PublisherService {

  private final PublisherQueryRepositoryImpl repository;

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