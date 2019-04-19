package de.wasenweg.alfred.publisher;

import com.mongodb.BasicDBObject;

import de.wasenweg.alfred.comics.Comic;
import de.wasenweg.alfred.progress.ProgressHelper;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.ConditionalOperators;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.stereotype.Repository;

import java.util.List;

import static org.springframework.data.mongodb.core.aggregation.Aggregation.group;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.sort;
import static org.springframework.data.mongodb.core.query.Criteria.where;

@Repository
@RepositoryRestResource(collectionResourceRel = "publishers", path = "publishers")
public class PublisherQueryRepositoryImpl implements PublisherQueryRepository {

    @Autowired
    private MongoTemplate mongoTemplate;

    @Override
    public List<Publisher> findAll(final String userId) {
        return mongoTemplate.aggregate(ProgressHelper.aggregateWithProgress(userId,
            sort(Sort.Direction.ASC, "position"),
            group("publisher", "series", "volume")
                .last("volume").as("volume")
                .count().as("issueCount")
                .min("read").as("read")
                .sum(ConditionalOperators
                     .when(where("read").is(true))
                     .then(1).otherwise(0))
                     .as("readCount")
                .first("thumbnail").as("thumbnail"),
            group("publisher", "series")
                .last("series").as("series")
                .addToSet(new BasicDBObject() {{
                    put("volume", "$_id.volume");
                    put("series", "$_id.series");
                    put("publisher", "$_id.publisher");
                    put("issueCount", "$issueCount");
                    put("read", "$read");
                    put("readCount", "$readCount");
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
