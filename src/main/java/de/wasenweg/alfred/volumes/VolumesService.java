package de.wasenweg.alfred.volumes;

import de.wasenweg.alfred.comics.ComicQueryRepositoryImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class VolumesService {

  private final ComicQueryRepositoryImpl repository;

  public List<Volume> findAllVolumesByPublisherAndSeries(final String userId, final String publisher, final String series) {
    return this.repository.findAllVolumes(userId, publisher, series);
  }
}