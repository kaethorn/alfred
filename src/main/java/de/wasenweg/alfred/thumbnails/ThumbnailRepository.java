package de.wasenweg.alfred.thumbnails;

import de.wasenweg.alfred.thumbnails.Thumbnail.ThumbnailType;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import java.util.Optional;

@RepositoryRestResource(collectionResourceRel = "thumbnail", path = "thumbnail")
public interface ThumbnailRepository extends MongoRepository<Thumbnail, String> {

  Optional<Thumbnail> findByComicIdAndType(ObjectId comicId, ThumbnailType type);
}
