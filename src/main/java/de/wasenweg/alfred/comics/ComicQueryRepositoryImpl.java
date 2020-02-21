package de.wasenweg.alfred.comics;

import de.wasenweg.alfred.progress.ProgressUtility;
import lombok.RequiredArgsConstructor;
import org.bson.types.ObjectId;
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
import static org.springframework.data.mongodb.core.aggregation.Aggregation.match;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.project;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.replaceRoot;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.sort;
import static org.springframework.data.mongodb.core.aggregation.ArrayOperators.ArrayElemAt.arrayOf;
import static org.springframework.data.mongodb.core.aggregation.ArrayOperators.Filter.filter;
import static org.springframework.data.mongodb.core.query.Criteria.where;

@RequiredArgsConstructor
public class ComicQueryRepositoryImpl implements ComicQueryRepository {

  private static final String COMIC = "comic";
  private static final String COMICS = "comics";
  private static final String PUBLISHER = "publisher";
  private static final String SERIES = "series";
  private static final String VOLUME = "volume";
  private static final String POSITION = "position";
  private static final String PATH = "path";
  private static final String LAST_READ = "lastRead";
  private static final String READ = "read";
  private static final String READ_COUNT = "readCount";
  private static final String VOLUME_READ = "volumeRead";

  private final MongoTemplate mongoTemplate;

  @Override
  public List<Comic> findAllWithErrors() {
    return this.mongoTemplate.aggregate(Aggregation.newAggregation(
        match(where("errors").exists(true)),

        sort(Sort.Direction.ASC, PATH)
        ), Comic.class, Comic.class).getMappedResults();
  }

  @Override
  public List<Comic> findAllWithoutErrors() {
    return this.mongoTemplate.aggregate(Aggregation.newAggregation(
        match(where("errors").exists(false)),

        sort(Sort.Direction.ASC, PATH)
        ), Comic.class, Comic.class).getMappedResults();
  }

  @Override
  public Optional<Comic> findById(
      final String userId,
      final String comicId) {
    return Optional.ofNullable(this.mongoTemplate.aggregate(ProgressUtility.aggregateWithProgress(userId,
        match(where("_id").is(new ObjectId(comicId))),
        limit(1)
        ), Comic.class, Comic.class).getUniqueMappedResult());
  }

  // Lists issues for volumes that are in progress, aka bookmarks.
  @Override
  public List<Comic> findAllLastReadPerVolume(final String userId) {
    return this.mongoTemplate.aggregate(ProgressUtility.aggregateWithProgress(userId,
        // Collect volumes with aggregated read stats and a list of issues
        sort(Sort.Direction.ASC, POSITION),
        group(PUBLISHER, SERIES, VOLUME)
          .min(ConditionalOperators
              .when(where(READ).is(true))
              .then(true).otherwise(false))
              .as(VOLUME_READ)
          .max(LAST_READ)
          .as(LAST_READ)
          .sum(ConditionalOperators
              // Consider either partly read or completed issues.
              .when(new Criteria().orOperator(
                  where("currentPage").gt(0),
                  where(READ).is(true)))
              .then(1).otherwise(0))
              .as(READ_COUNT)
          .push(Aggregation.ROOT).as(COMICS),

        // Skip volumes where all issues are read.
        match(where(VOLUME_READ).is(false)),

        // Skip volumes that where never read.
        match(where(READ_COUNT).gt(0)),

        // Sort by volume aggregated `lastRead` attribute as the comic
        // specific `lastRead` attribute might not be set, e.g. when
        // the next comic has not yet been started but the previous one
        // is set to `read`.
        sort(Sort.Direction.DESC, LAST_READ),

        // Search issues of each volume and return the first unread.
        project().and(filter(COMICS).as(COMIC)
            .by(ComparisonOperators.Ne.valueOf("comic.read").notEqualToValue(true)))
            .as(COMICS),
        project().and(arrayOf(COMICS).elementAt(0)).as(COMIC),

        // Replace the group hierarchy with the found comic.
        replaceRoot(COMIC)
        ), Comic.class, Comic.class).getMappedResults();
  }

  // Returns the last unread or in progress issue in the given volume, aka resume volume.
  @Override
  public Optional<Comic> findLastReadForVolume(
      final String userId,
      final String publisher,
      final String series,
      final String volume) {

    return Optional.ofNullable(this.mongoTemplate.aggregate(ProgressUtility.aggregateWithProgress(userId,
        match(where(PUBLISHER).is(publisher).and(SERIES).is(series).and(VOLUME).is(volume)),

        // If all comics are read, return the first, otherwise the first unread
        sort(Sort.Direction.ASC, POSITION),
        sort(Sort.Direction.ASC, READ),

        limit(1)
        ), Comic.class, Comic.class).getUniqueMappedResult());
  }

  @Override
  public List<Comic> findAllByPublisherAndSeriesAndVolumeOrderByPosition(
      final String userId,
      final String publisher,
      final String series,
      final String volume) {
    return this.mongoTemplate.aggregate(ProgressUtility.aggregateWithProgress(userId,
        match(where(PUBLISHER).is(publisher).and(SERIES).is(series).and(VOLUME).is(volume)),

        sort(Sort.Direction.ASC, POSITION)
        ), Comic.class, Comic.class).getMappedResults();
  }
}
