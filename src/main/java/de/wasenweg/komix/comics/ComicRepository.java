package de.wasenweg.komix.comics;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ComicRepository extends MongoRepository<Comic, String>, VolumeRepository {

    List<Comic> findAllByOrderBySeriesAscVolumeAscPositionAsc();

    List<Comic> findAllBySeriesAndVolume(
            @Param("series") final String series,
            @Param("volume") final String volume);
}
