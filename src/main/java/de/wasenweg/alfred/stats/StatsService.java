package de.wasenweg.alfred.stats;

import de.wasenweg.alfred.comics.Comic;
import de.wasenweg.alfred.progress.Progress;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

@Service
public class StatsService {

  @Autowired
  private MongoTemplate mongoTemplate;

  public Stats getStats() {
    return Stats.builder()
        .issues(this.mongoTemplate.count(new Query(), Comic.class))
        .publishers(this.mongoTemplate.findDistinct("publisher", Comic.class, String.class).size())
        .series(this.mongoTemplate.findDistinct("series", Comic.class, String.class).size())
        .volumes(this.mongoTemplate.findDistinct("volume", Comic.class, String.class).size())
        .users(this.mongoTemplate.findDistinct("userId", Progress.class, String.class).size())
        .build();
  }
}
