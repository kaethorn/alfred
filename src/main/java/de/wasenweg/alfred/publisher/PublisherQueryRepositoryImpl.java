package de.wasenweg.alfred.publisher;

import de.wasenweg.alfred.comics.Comic;
import de.wasenweg.alfred.progress.ProgressUtility;
import de.wasenweg.alfred.volumes.Volume;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.ConditionalOperators;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.stereotype.Repository;

import java.util.List;

import static org.springframework.data.mongodb.core.aggregation.Aggregation.group;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.match;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.sort;
import static org.springframework.data.mongodb.core.query.Criteria.where;

@Repository
@RepositoryRestResource(collectionResourceRel = "publishers", path = "publishers")
@RequiredArgsConstructor
public class PublisherQueryRepositoryImpl implements PublisherQueryRepository {

  private static final String PUBLISHER = "publisher";
  private static final String SERIES = "series";
  private static final String VOLUME = "volume";
  private static final String ERRORS = "errors";
  private static final String NAME = "name";

  private final MongoTemplate mongoTemplate;

  @Override
  public List<Publisher> findAllPublishers(final String userId) {
    return this.mongoTemplate.aggregate(ProgressUtility.aggregateWithProgress(userId,
        match(where(ERRORS).exists(false)),
        group(PUBLISHER, SERIES, VOLUME)
        .last(PUBLISHER).as(PUBLISHER)
        .last(SERIES).as(SERIES),
        group(PUBLISHER, SERIES)
        .last("_id.series").as(NAME)
        .last("_id.publisher").as(PUBLISHER)
        .count().as("volumesCount"),
        sort(Sort.Direction.ASC, NAME),
        group(PUBLISHER)
        .last(PUBLISHER).as(NAME)
        .push(Aggregation.ROOT).as(SERIES)
        .count().as("seriesCount"),
        sort(Sort.Direction.ASC, NAME)
        ), Comic.class, Publisher.class).getMappedResults();
  }

  @Override
  public List<Series> findAllSeries(final String userId, final String publisher) {
    return this.mongoTemplate.aggregate(ProgressUtility.aggregateWithProgress(userId,
        match(where(PUBLISHER).is(publisher).and(ERRORS).exists(false)),
        group(SERIES, VOLUME)
        .last(PUBLISHER).as(PUBLISHER),
        group(SERIES)
        .last("_id.series").as(NAME)
        .last(PUBLISHER).as(PUBLISHER)
        .count().as("volumesCount"),
        sort(Sort.Direction.ASC, NAME),
        sort(Sort.Direction.ASC, PUBLISHER)
        ), Comic.class, Series.class).getMappedResults();
  }

  @Override
  public List<Volume> findAllVolumes(final String userId, final String publisher, final String series) {
    return this.mongoTemplate.aggregate(ProgressUtility.aggregateWithProgress(userId,
        match(where(PUBLISHER).is(publisher).and(SERIES).is(series).and(ERRORS).exists(false)),
        sort(Sort.Direction.ASC, "position"),
        group(VOLUME)
          .last(VOLUME).as(NAME)
          .last(SERIES).as(SERIES)
          .last(PUBLISHER).as(PUBLISHER)
          .count().as("issueCount")
          .min("read").as("read")
          .sum(ConditionalOperators
              .when(where("read").is(true))
              .then(1).otherwise(0))
              .as("readCount")
          .first("_id").as("firstComicId"),
        sort(Sort.Direction.ASC, NAME)
        ), Comic.class, Volume.class).getMappedResults();
  }
}
