package de.wasenweg.komix.comics;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import java.util.List;

import static org.springframework.data.mongodb.core.aggregation.Aggregation.group;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.match;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.replaceRoot;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.sort;
import static org.springframework.data.mongodb.core.query.Criteria.where;

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
            match(where("read").is(true)),
            sort(Sort.Direction.ASC, "position"),
            group("publisher", "series", "volume")
                .last("$$ROOT").as("comic"),
            replaceRoot("comic")
        ), Comic.class, Comic.class).getMappedResults();
    }
}
