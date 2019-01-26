package de.wasenweg.komix.preferences;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import java.util.Optional;

@RepositoryRestResource(collectionResourceRel = "preferences", path = "preferences")
public interface PreferenceRepository extends MongoRepository<Preference, Long> {
    Optional<Preference> findByKey(@Param("key") String key);
}
