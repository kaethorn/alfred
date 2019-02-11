package de.wasenweg.komix.comics;

import com.mongodb.BasicDBObject;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import java.util.List;

import static org.springframework.data.mongodb.core.aggregation.Aggregation.group;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.sort;

@RepositoryRestResource(collectionResourceRel = "volumes", path = "volumes")
public class VolumeRepositoryImpl implements VolumeRepository {

    private final MongoTemplate mongoTemplate;

    @Autowired
    public VolumeRepositoryImpl(final MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    @Override
    public List<Series> findVolumesBySeries() {
        return mongoTemplate.aggregate(Aggregation.newAggregation(
            group("series")
                .last("series").as("series")
                .addToSet("volume").as("volumes")
        ), Comic.class, Series.class).getMappedResults();
    }

    @Override
    public List<Publisher> findVolumesBySeriesAndPublishers() {
        return mongoTemplate.aggregate(Aggregation.newAggregation(
            sort(Sort.Direction.DESC, "position"),
            group("publisher", "series", "volume")
                .last("volume").as("volume")
                .first("thumbnail").as("thumbnail"),
            group("publisher", "series")
                .last("series").as("series")
                .addToSet(new BasicDBObject() {{
                    put("volume", "$_id.volume");
                    put("series", "$_id.series");
                    put("publisher", "$_id.publisher");
                    put("thumbnail", "$thumbnail");
                }}).as("volumes"),
            group("_id.publisher")
                .last("_id.publisher").as("publisher")
                .addToSet(new BasicDBObject() {{
                    put("series", "$_id.series");
                    put("volumes", "$volumes");
                }}).as("series"),
            sort(Sort.Direction.ASC, "publisher")
        ), Comic.class, Publisher.class).getMappedResults();
    }
}
