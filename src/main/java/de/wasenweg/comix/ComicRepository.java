package de.wasenweg.comix;

import java.util.List;

import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

@RepositoryRestResource(collectionResourceRel = "comics", path = "comics")
public interface ComicRepository extends PagingAndSortingRepository<Comic, Long> {
	List<Comic> findByName(@Param("name") String name);
}