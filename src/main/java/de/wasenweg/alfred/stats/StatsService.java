package de.wasenweg.alfred.stats;

import de.wasenweg.alfred.comics.Comic;
import de.wasenweg.alfred.progress.Progress;
import de.wasenweg.alfred.scanner.ScanProgress;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class StatsService {

  private final MongoTemplate mongoTemplate;

  public Stats getStats() {
    final Query query = Query.query(Criteria.where("status").is("DONE")).with(Sort.by(Sort.Direction.DESC, "finished"));
    final ScanProgress scanProgress = Optional
        .ofNullable(this.mongoTemplate.findOne(query, ScanProgress.class))
        .orElse(ScanProgress.builder().build());

    return Stats.builder()
        .issues(this.mongoTemplate.count(new Query(), Comic.class))
        .publishers(this.mongoTemplate.findDistinct("publisher", Comic.class, String.class).size())
        .series(this.mongoTemplate.findDistinct("series", Comic.class, String.class).size())
        .volumes(this.mongoTemplate.findDistinct("volume", Comic.class, String.class).size())
        .users(this.mongoTemplate.findDistinct("userId", Progress.class, String.class).size())
        .lastScanFinished(scanProgress.getFinished())
        .lastScanStarted(scanProgress.getStarted())
        .build();
  }
}
