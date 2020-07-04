package de.wasenweg.alfred.series;

import de.wasenweg.alfred.comics.ComicQueryRepositoryImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SeriesService {

  private final ComicQueryRepositoryImpl repository;

  public List<Series> findAllSeriesByPublisher(final String userId, final String publisher) {
    return this.repository.findAllSeries(userId, publisher);
  }
}