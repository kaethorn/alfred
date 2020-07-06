package de.wasenweg.alfred.scanner;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

@RepositoryRestResource(collectionResourceRel = "scan_progress", path = "scan_progress")
public interface ScanProgressRepository extends MongoRepository<ScanProgress, String> {
}
