package de.wasenweg.komix.comics;

import com.mongodb.BasicDBObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
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
    public List<Series> findVolumesBySeries() {
        final AggregationResults<Series> result = mongoTemplate.aggregate(Aggregation.newAggregation(
            group("series")
                .last("series").as("series")
                .addToSet("volume").as("volumes")
        ), Comic.class, Series.class);
        return result.getMappedResults();
    }

    @Override
    public List<Publisher> findVolumesBySeriesAndPublishers() {
        final AggregationResults<Publisher> result = mongoTemplate.aggregate(Aggregation.newAggregation(
            group("publisher", "series")
                .last("series").as("series")
                .addToSet("volume").as("volumes"),
            group("_id.publisher")
                .last("_id.publisher").as("publisher")
                .addToSet(new BasicDBObject() {{
                    put("series", "$_id.series");
                    put("volumes", "$volumes");
                }}).as("series")
        ), Comic.class, Publisher.class);
        return result.getMappedResults();
    }
}
