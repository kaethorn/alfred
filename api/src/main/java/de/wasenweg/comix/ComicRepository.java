package de.wasenweg.comix;

import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

@RepositoryRestResource(collectionResourceRel = "comics", path = "comics")
public interface ComicRepository extends PagingAndSortingRepository<Comic, Long> {
}