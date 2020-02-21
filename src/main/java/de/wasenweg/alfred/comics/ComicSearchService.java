package de.wasenweg.alfred.comics;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ComicSearchService {

  private final ComicQueryRepositoryImpl queryRepository;
  private final ComicRepository comicRepository;

  public List<Comic> findAllLastReadPerVolume(final String userId) {
    return this.queryRepository.findAllLastReadPerVolume(userId);
  }

  public List<Comic> findAllByOrderByPublisherAscSeriesAscVolumeAscPositionAsc() {
    return this.comicRepository.findAllByOrderByPublisherAscSeriesAscVolumeAscPositionAsc();
  }

  public Optional<Comic> findLastReadForVolume(
      final String userId, final String publisher, final String series, final String volume) {
    return this.queryRepository.findLastReadForVolume(userId, publisher, series, volume);
  }

  public List<Comic> findAllByPublisherAndSeriesAndVolumeOrderByPosition(
      final String userId, final String publisher, final String series, final String volume) {
    return this.queryRepository.findAllByPublisherAndSeriesAndVolumeOrderByPosition(
        userId, publisher, series, volume);
  }
}
