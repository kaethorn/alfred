package de.wasenweg.alfred.publisher;

import de.wasenweg.alfred.comics.Comic;
import de.wasenweg.alfred.progress.ProgressHelper;
import de.wasenweg.alfred.volumes.Volume;

import org.springframework.beans.factory.annotation.Autowired;
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
public class PublisherQueryRepositoryImpl implements PublisherQueryRepository {

  @Autowired
  private MongoTemplate mongoTemplate;

  @Override
  public List<Publisher> findAllPublishers(final String userId) {
    return mongoTemplate.aggregate(ProgressHelper.aggregateWithProgress(userId,
        group("series", "volume")
        .last("publisher").as("publisher"),
        group("series")
        .last("_id.series").as("series")
        .last("publisher").as("publisher")
        .count().as("volumesCount"),
        sort(Sort.Direction.ASC, "series"),
        group("publisher")
        .last("publisher").as("publisher")
        .push(Aggregation.ROOT).as("series")
        .count().as("seriesCount"),
        sort(Sort.Direction.ASC, "publisher")
        ), Comic.class, Publisher.class).getMappedResults();
  }

  @Override
  public List<Series> findAllSeries(final String userId, final String publisher) {
    return mongoTemplate.aggregate(ProgressHelper.aggregateWithProgress(userId,
        match(where("publisher").is(publisher)),
        group("series", "volume")
        .last("publisher").as("publisher"),
        group("series")
        .last("_id.series").as("series")
        .last("publisher").as("publisher")
        .count().as("volumesCount"),
        sort(Sort.Direction.ASC, "series"),
        sort(Sort.Direction.ASC, "publisher")
        ), Comic.class, Series.class).getMappedResults();
  }

  @Override
  public List<Volume> findAllVolumes(final String userId, final String publisher, final String series) {
    return mongoTemplate.aggregate(ProgressHelper.aggregateWithProgress(userId,
        match(where("publisher").is(publisher).and("series").is(series)),
        sort(Sort.Direction.ASC, "position"),
        group("volume")
        .last("volume").as("volume")
        .last("series").as("series")
        .last("publisher").as("publisher")
        .count().as("issueCount")
        .min("read").as("read")
        .sum(ConditionalOperators
            .when(where("read").is(true))
            .then(1).otherwise(0))
        .as("readCount")
        .first("thumbnail").as("thumbnail"),
        sort(Sort.Direction.ASC, "volume")
        ), Comic.class, Volume.class).getMappedResults();
  }
}
