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
import static org.springframework.data.mongodb.core.aggregation.Aggregation.limit;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.match;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.project;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.replaceRoot;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.sort;
import static org.springframework.data.mongodb.core.aggregation.ArrayOperators.ArrayElemAt.arrayOf;
import static org.springframework.data.mongodb.core.aggregation.ArrayOperators.Filter.filter;
import static org.springframework.data.mongodb.core.aggregation.ComparisonOperators.Eq.valueOf;

@RepositoryRestResource
public class ComicQueryRepositoryImpl implements ComicQueryRepository {

    private final MongoTemplate mongoTemplate;

    @Autowired
    public ComicQueryRepositoryImpl(final MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    @Override
    public List<Comic> findAllLastReadPerVolume() {
        return mongoTemplate.aggregate(Aggregation.newAggregation(
            sort(Sort.Direction.ASC, "position"),
            group("publisher", "series", "volume")
                .min("read").as("read")
                .max("lastRead").as("lastRead")
                .sum(ConditionalOperators
                        .when(new Criteria("read").is(true))
                        .then(1).otherwise(0)).as("readCount")
                .push("$$ROOT").as("comics"),
            match(Criteria.where("read").is(false)),
            match(Criteria.where("readCount").gt(0)),
            sort(Sort.Direction.DESC, "lastRead"),
            // At this point, we have a list of volumes being read, each
            // of which contains all comics in that volume.

            // Now filter that list to find the first unread.
            project().and(filter("comics").as("comic")
                        .by(valueOf("comic.read").equalToValue(false)))
                        .as("comics"),
            project().and(arrayOf("comics").elementAt(0)).as("comic"),

            // Replace the group hierarchy with the found comic.
            replaceRoot("comic")
        ), Comic.class, Comic.class).getMappedResults();
    }

    @Override
    public Comic findLastReadForVolume(final String publisher, final String series, final String volume) {
        return mongoTemplate.aggregate(Aggregation.newAggregation(
            match(Criteria.where("publisher").is(publisher)),
            match(Criteria.where("series").is(series)),
            match(Criteria.where("volume").is(volume)),
            match(Criteria.where("read").is(false)),
            sort(Sort.Direction.ASC, "position"),
            limit(1)
        ), Comic.class, Comic.class).getUniqueMappedResult();
    }
}
