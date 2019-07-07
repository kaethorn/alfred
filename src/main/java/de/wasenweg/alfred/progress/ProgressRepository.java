package de.wasenweg.alfred.progress;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import java.util.Optional;

@RepositoryRestResource(collectionResourceRel = "progress", path = "progress")
public interface ProgressRepository extends MongoRepository<Progress, String> {

  Optional<Progress> findByUserIdAndComicId(final String userId, final ObjectId comicId);
}
