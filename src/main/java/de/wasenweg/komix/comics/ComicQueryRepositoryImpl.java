package de.wasenweg.komix.comics;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.ConditionalOperators;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import java.util.List;

import static org.springframework.data.mongodb.core.aggregation.Aggregation.group;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.match;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.replaceRoot;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.sort;

@RepositoryRestResource
public class ComicQueryRepositoryImpl implements ComicQueryRepository {

    private final MongoTemplate mongoTemplate;

    @Autowired
    public ComicQueryRepositoryImpl(final MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    @Override
    public List<Comic> findAllLastReadByVolume() {
        return mongoTemplate.aggregate(Aggregation.newAggregation(
            sort(Sort.Direction.ASC, "position"),
            group("publisher", "series", "volume")
                .min("read").as("read")
                .sum(ConditionalOperators
                        .when(new Criteria("read").is(true))
                        .then(1).otherwise(0)).as("readCount")
                .last("$$ROOT").as("comic"),
            match(Criteria.where("read").is(false).andOperator(
                    Criteria.where("readCount").gt(0))),
            replaceRoot("comic"),
            sort(Sort.Direction.DESC, "lastRead")
        ), Comic.class, Comic.class).getMappedResults();
    }
}
