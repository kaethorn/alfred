package de.wasenweg.alfred.settings;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import java.util.Optional;

@RepositoryRestResource(collectionResourceRel = "settings", path = "settings")
public interface SettingRepository extends MongoRepository<Setting, String> {
  Optional<Setting> findByKey(@Param("key") String key);
}
