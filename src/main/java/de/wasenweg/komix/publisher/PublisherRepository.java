package de.wasenweg.komix.publisher;

import org.springframework.data.mongodb.repository.MongoRepository;

public interface PublisherRepository extends MongoRepository<Publisher, String>, PublisherQueryRepository {

}
