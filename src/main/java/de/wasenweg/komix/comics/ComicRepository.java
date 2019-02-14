package de.wasenweg.komix.comics;

import de.wasenweg.komix.volumes.VolumeRepository;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ComicRepository extends MongoRepository<Comic, String>, VolumeRepository, ComicQueryRepository {

    List<Comic> findAllByOrderBySeriesAscVolumeAscPositionAsc();

    List<Comic> findAllBySeriesAndVolumeOrderByPosition(
            @Param("series") final String series,
            @Param("volume") final String volume);

    List<Comic> findAllByPublisherAndSeriesAndVolumeOrderByPosition(
            @Param("publisher") final String publisher,
            @Param("series") final String series,
            @Param("volume") final String volume);

    Comic findFirstByPublisherAndSeriesAndVolumeOrderByPosition(
            @Param("publisher") final String publisher,
            @Param("series") final String series,
            @Param("volume") final String volume);
}
