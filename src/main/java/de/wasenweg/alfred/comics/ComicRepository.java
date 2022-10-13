package de.wasenweg.alfred.comics;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ComicRepository extends MongoRepository<Comic, String>, ComicQueryRepository {

  Optional<Comic> findByPath(@Param("path") String path);

  List<Comic> findAllByOrderByPublisherAscSeriesAscVolumeAscPositionAsc();

  List<Comic> findAllBySeriesAndVolumeOrderByPosition(
      @Param("series") String series,
      @Param("volume") String volume);

  Optional<Comic> findFirstByPublisherAndSeriesAndVolumeOrderByPosition(
      @Param("publisher") String publisher,
      @Param("series") String series,
      @Param("volume") String volume);
}
