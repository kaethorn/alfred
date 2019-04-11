package de.wasenweg.komix.comics;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.ComparisonOperators;
import org.springframework.data.mongodb.core.aggregation.ConditionalOperators;
import org.springframework.data.mongodb.core.query.Criteria;

import java.util.List;
import java.util.Optional;

import static org.springframework.data.mongodb.core.aggregation.Aggregation.group;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.limit;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.lookup;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.match;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.project;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.replaceRoot;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.sort;
import static org.springframework.data.mongodb.core.aggregation.ArrayOperators.ArrayElemAt.arrayOf;
import static org.springframework.data.mongodb.core.aggregation.ArrayOperators.Filter.filter;
import static org.springframework.data.mongodb.core.aggregation.ObjectOperators.valueOf;
import static org.springframework.data.mongodb.core.query.Criteria.where;

public class ComicQueryRepositoryImpl implements ComicQueryRepository {

    @Autowired
    private MongoTemplate mongoTemplate;

    // Lists issues for volumes that are in progress, aka bookmarks.
    @Override
    public List<ComicDTO> findAllLastReadPerVolume(final String userId) {
        return mongoTemplate.aggregate(Aggregation.newAggregation(
            // Merge progress flags for the current user
            lookup("progress", "_id", "comicId", "progress"),
            replaceRoot().withValueOf(valueOf(arrayOf(
                    filter("progress")
                        .as("item")
                        .by(ComparisonOperators.Eq.valueOf("item.userId").equalToValue(userId)))
                    .elementAt(0)).mergeWith(Aggregation.ROOT)),
            project().andExclude("progress", "comicId", "userId"),

            // Collect volumes with aggregated read stats and a list of issues
            sort(Sort.Direction.ASC, "position"),
            group("publisher", "series", "volume")
                .min("read").as("volumeRead")
                .sum(ConditionalOperators
                    // Consider either partly read or completed issues.
                    .when(new Criteria().orOperator(
                            where("currentPage").gt(0),
                            where("read").is(true)))
                    .then(1).otherwise(0))
                    .as("readCount")
                .push("$$ROOT").as("comics"),

            // Skip volumes where all issues are read.
            match(where("volumeRead").is(false)),

            // Skip volumes that where never read.
            match(where("readCount").gt(0)),

            // Search issues of each volume and return the first unread.
            project().and(filter("comics").as("comic")
                        .by(ComparisonOperators.Ne.valueOf("comic.read").notEqualToValue(true)))
                        .as("comics"),
            project().and(arrayOf("comics").elementAt(0)).as("comic"),

            // Replace the group hierarchy with the found comic.
            replaceRoot("comic"),
            sort(Sort.Direction.DESC, "lastRead")
        ), Comic.class, ComicDTO.class).getMappedResults();
    }

    // Returns the last unread or in progress issue in the given volume, aka resume volume.
    // FIXME Test:
    // 1. A volume with an issue in progress should result in that issue.
    // 2. A volume that has not been started should result in the first issue of that volume.
    // 3. A volume with issues that are not in progress while some have been completed will result in the next unread
    // 4. A volume that has been completed should result in last issue of that volume.
    @Override
    public Optional<ComicDTO> findLastReadForVolume(final String userId, final String publisher, final String series, final String volume) {
        return Optional.ofNullable(mongoTemplate.aggregate(Aggregation.newAggregation(
            match(where("publisher").is(publisher).and("series").is(series).and("volume").is(volume)),

            // Merge progress flags for the current user
            lookup("progress", "_id", "comicId", "progress"),
            replaceRoot().withValueOf(valueOf(arrayOf(
                    filter("progress")
                        .as("item")
                        .by(ComparisonOperators.Eq.valueOf("item.userId").equalToValue(userId)))
                    .elementAt(0)).mergeWith(Aggregation.ROOT)),
            project().andExclude("progress", "comicId", "userId"),

            sort(Sort.Direction.ASC, "position"),
            match(where("read").ne(true)),
            limit(1)
        ), Comic.class, ComicDTO.class).getUniqueMappedResult());
    }
}
