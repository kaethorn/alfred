package de.wasenweg.comix.preferences;

import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

@RepositoryRestResource(collectionResourceRel = "preferences", path = "preferences")
public interface PreferenceRepository extends CrudRepository<Preference, Long> {
    Preference findByKey(@Param("key") String key);
}
