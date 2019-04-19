package de.wasenweg.alfred.comics;

import com.mongodb.BasicDBObject;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationOperation;
import org.springframework.data.mongodb.core.aggregation.ComparisonOperators;
import org.springframework.data.mongodb.core.aggregation.ConditionalOperators;
import org.springframework.data.mongodb.core.query.Criteria;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.springframework.data.mongodb.core.aggregation.Aggregation.group;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.limit;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.lookup;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.match;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.project;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.replaceRoot;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.sort;
import static org.springframework.data.mongodb.core.aggregation.ArrayOperators.ArrayElemAt.arrayOf;
import static org.springframework.data.mongodb.core.aggregation.ArrayOperators.Filter.filter;
import static org.springframework.data.mongodb.core.aggregation.ObjectOperators.MergeObjects.merge;
import static org.springframework.data.mongodb.core.query.Criteria.where;

public class ComicQueryRepositoryImpl implements ComicQueryRepository {

    @Autowired
    private MongoTemplate mongoTemplate;

    // Merge progress flags for the current user
    private List<AggregationOperation> mergeProgress(final String userId) {
        return Stream.of(
            lookup("progress", "_id", "comicId", "progress"),
            replaceRoot().withValueOf(
                merge(Aggregation.ROOT)
                .mergeWithValuesOf(
                    arrayOf(
                        filter("progress").as("item").by(ComparisonOperators.Eq.valueOf("item.userId").equalToValue(userId))
                    ).elementAt(0))
                // Restore comic _id previously overwritten with progress _id.
                .mergeWith(new BasicDBObject("_id", Aggregation.ROOT + "._id"))),
            project().andExclude("progress", "comicId", "userId")
        ).collect(Collectors.toList());
    }

    // Syntactic sugar
    private Aggregation aggregateWithProgress(final String userId, final AggregationOperation... operations) {
        return Aggregation.newAggregation(
                Stream.concat(
                        mergeProgress(userId).stream(),
                        Stream.of(operations))
                .collect(Collectors.toList()));
    }

    // Lists issues for volumes that are in progress, aka bookmarks.
    @Override
    public List<Comic> findAllLastReadPerVolume(final String userId) {
        return mongoTemplate.aggregate(aggregateWithProgress(userId,
            // Collect volumes with aggregated read stats and a list of issues
            sort(Sort.Direction.ASC, "position"),
            group("publisher", "series", "volume")
                .min(ConditionalOperators
                    .when(where("read").is(true))
                    .then(true).otherwise(false))
                    .as("volumeRead")
                .max("lastRead")
                    .as("lastRead")
                .sum(ConditionalOperators
                    // Consider either partly read or completed issues.
                    .when(new Criteria().orOperator(
                            where("currentPage").gt(0),
                            where("read").is(true)))
                    .then(1).otherwise(0))
                    .as("readCount")
                .push(Aggregation.ROOT).as("comics"),

            // Skip volumes where all issues are read.
            match(where("volumeRead").is(false)),

            // Skip volumes that where never read.
            match(where("readCount").gt(0)),

            // Sort by volume aggregated `lastRead` attribute as the comic
            // specific `lastRead` attribute might not be set, e.g. when
            // the next comic has not yet been started but the previous one
            // is set to `read`.
            sort(Sort.Direction.DESC, "lastRead"),

            // Search issues of each volume and return the first unread.
            project().and(filter("comics").as("comic")
                        .by(ComparisonOperators.Ne.valueOf("comic.read").notEqualToValue(true)))
                        .as("comics"),
            project().and(arrayOf("comics").elementAt(0)).as("comic"),

            // Replace the group hierarchy with the found comic.
            replaceRoot("comic")
        ), Comic.class, Comic.class).getMappedResults();
    }

    // Returns the last unread or in progress issue in the given volume, aka resume volume.
    @Override
    public Optional<Comic> findLastReadForVolume(
            final String userId,
            final String publisher,
            final String series,
            final String volume) {

        return Optional.ofNullable(mongoTemplate.aggregate(aggregateWithProgress(userId,
            match(where("publisher").is(publisher).and("series").is(series).and("volume").is(volume)),

            // If all comics are read, return the first, otherwise the first unread
            sort(Sort.Direction.ASC, "position"),
            sort(Sort.Direction.ASC, "read"),

            limit(1)
        ), Comic.class, Comic.class).getUniqueMappedResult());
    }

    @Override
    public List<Comic> findAllByPublisherAndSeriesAndVolumeOrderByPosition(
            final String userId,
            final String publisher,
            final String series,
            final String volume) {
        return mongoTemplate.aggregate(aggregateWithProgress(userId,
            match(where("publisher").is(publisher).and("series").is(series).and("volume").is(volume)),

            sort(Sort.Direction.ASC, "position")
        ), Comic.class, Comic.class).getMappedResults();
    }
}
