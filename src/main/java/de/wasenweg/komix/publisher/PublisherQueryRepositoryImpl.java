package de.wasenweg.komix.publisher;

import com.mongodb.BasicDBObject;

import de.wasenweg.komix.comics.Comic;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.stereotype.Repository;

import java.util.List;

import static org.springframework.data.mongodb.core.aggregation.Aggregation.group;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.sort;

@Repository
@RepositoryRestResource(collectionResourceRel = "publishers", path = "publishers")
public class PublisherQueryRepositoryImpl implements PublisherQueryRepository {

    @Autowired
    private MongoTemplate mongoTemplate;

    @Override
    public List<Publisher> findAll() {
        return mongoTemplate.aggregate(Aggregation.newAggregation(
            sort(Sort.Direction.ASC, "position"),
            group("publisher", "series", "volume")
                .last("volume").as("volume")
                .count().as("issueCount")
                .first("thumbnail").as("thumbnail"),
            group("publisher", "series")
                .last("series").as("series")
                .addToSet(new BasicDBObject() {{
                    put("volume", "$_id.volume");
                    put("series", "$_id.series");
                    put("publisher", "$_id.publisher");
                    put("issueCount", "$issueCount");
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
