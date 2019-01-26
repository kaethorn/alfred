package de.wasenweg.komix.comics;

import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface ComicRepository extends MongoRepository<Comic, String> {
    List<Comic> findAllByOrderBySeriesAscVolumeAscPositionAsc();
}
