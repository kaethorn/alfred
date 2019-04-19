package de.wasenweg.alfred.comics;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ComicRepository extends MongoRepository<Comic, String>, ComicQueryRepository {

    Optional<Comic> findByPath(@Param("path") final String path);

    List<Comic> findAllByOrderBySeriesAscVolumeAscPositionAsc();

    List<Comic> findAllBySeriesAndVolumeOrderByPosition(
            @Param("series") final String series,
            @Param("volume") final String volume);

    Optional<Comic> findFirstByPublisherAndSeriesAndVolumeOrderByPosition(
            @Param("publisher") final String publisher,
            @Param("series") final String series,
            @Param("volume") final String volume);
}
