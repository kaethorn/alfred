package de.wasenweg.komix.comics;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.GroupOperation;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import java.util.List;

import static org.springframework.data.mongodb.core.aggregation.Aggregation.group;

@RepositoryRestResource(collectionResourceRel = "volumes", path = "volumes")
public class VolumeRepositoryImpl implements VolumeRepository {

    private final MongoTemplate mongoTemplate;

    @Autowired
    public VolumeRepositoryImpl(final MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    @Override
    public List<Volume> findVolumesBySeries() {
        final GroupOperation groupOperation = getGroupOperation();

        return mongoTemplate.aggregate(Aggregation.newAggregation(
            groupOperation
        ), Comic.class, Volume.class).getMappedResults();
    }

    private GroupOperation getGroupOperation() {
        return group("series")
            .last("series").as("series")
            .last("series").as("id")
            .addToSet("volume").as("volumes");
    }
}
