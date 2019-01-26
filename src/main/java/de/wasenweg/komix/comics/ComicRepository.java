package de.wasenweg.komix.comics;

import org.springframework.data.repository.CrudRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import java.util.List;

@RepositoryRestResource(collectionResourceRel = "comics", path = "comics")
public interface ComicRepository extends CrudRepository<Comic, Long> {
    List<Comic> findAllByOrderBySeriesAscVolumeAscPositionAsc();
}
