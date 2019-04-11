package de.wasenweg.komix.comics;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.ConditionalOperators;
import org.springframework.data.mongodb.core.query.Criteria;

import java.util.List;
import java.util.Optional;

import static org.springframework.data.mongodb.core.aggregation.Aggregation.group;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.limit;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.match;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.project;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.replaceRoot;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.sort;
import static org.springframework.data.mongodb.core.aggregation.ArrayOperators.ArrayElemAt.arrayOf;
import static org.springframework.data.mongodb.core.aggregation.ArrayOperators.Filter.filter;
import static org.springframework.data.mongodb.core.aggregation.ComparisonOperators.valueOf;
import static org.springframework.data.mongodb.core.query.Criteria.where;

public class ComicQueryRepositoryImpl implements ComicQueryRepository {

    @Autowired
    private MongoTemplate mongoTemplate;

    @Override
    public List<Comic> findAllLastReadPerVolume(final String userName) {
        return mongoTemplate.aggregate(Aggregation.newAggregation(
            sort(Sort.Direction.ASC, "position"),
            group("publisher", "series", "volume")
                .min("readState." + userName + ".read").as("read")
                .max("readState." + userName + ".lastRead").as("lastRead")
                .sum(ConditionalOperators
                        // Consider either partly read or completed issues
                        .when(new Criteria().orOperator(
                                where("readState." + userName + ".currentPage").gt(0),
                                where("readState." + userName + ".read").is(true)))
                        .then(1).otherwise(0))
                        .as("readCount")
                .push("$$ROOT").as("comics"),
            // Skip volumes where all issues are read
            match(where("read").is(false)),
            // Skip volumes that where never read
            match(where("readCount").gt(0)),
            sort(Sort.Direction.DESC, "lastRead"),

            // At this point, we have a list of volumes being read, each
            // of which contains all comics in that volume.

            // Now filter that list to find the first unread.
            project().and(filter("comics").as("comic")
                        .by(valueOf("comic.readState." + userName + ".read").notEqualToValue(true)))
                        .as("comics"),
            project().and(arrayOf("comics").elementAt(0)).as("comic"),

            // Replace the group hierarchy with the found comic.
            replaceRoot("comic")
        ), Comic.class, Comic.class).getMappedResults();
    }

    @Override
    public Optional<Comic> findLastReadForVolume(final String userName, final String publisher, final String series, final String volume) {
        return Optional.ofNullable(mongoTemplate.aggregate(Aggregation.newAggregation(
            match(where("publisher").is(publisher)),
            match(where("series").is(series)),
            match(where("volume").is(volume)),
            // FIXME this should work but result is empty
//            match(new Criteria().orOperator(
//                    where("readState." + userName + ".read").exists(false),
//                    where("readState." + userName + ".read").is(false))),
            sort(Sort.Direction.ASC, "position"),
            limit(1)
        ), Comic.class, Comic.class).getUniqueMappedResult());
    }
}
