package de.wasenweg.alfred.progress;

import de.wasenweg.alfred.comics.Comic;
import de.wasenweg.alfred.volumes.Volume;
import lombok.RequiredArgsConstructor;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
@RequiredArgsConstructor
public class ProgressService {

  private final MongoTemplate mongoTemplate;
  private final ProgressRepository progressRepository;

  public void deleteProgress() {
    this.progressRepository.deleteAll();
  }

  public void deleteProgressForCurrentUser(final String userId) {
    this.progressRepository.deleteByUserId(userId);
  }

  public Comic updateComic(final String userId, final Comic comic, final Boolean read) {
    final Query query = Query.query(Criteria
        .where("comicId").is(comic.getId())
        .and("userId").is(userId));

    final Update update = new Update();
    update.set("read", read);
    update.set("currentPage", read ? Integer.valueOf(0) : comic.getCurrentPage());
    update.set("lastRead", new Date());

    this.mongoTemplate.upsert(query, update, Progress.class);

    // Read state is saved in the progress schema
    comic.setRead(false);
    comic.setCurrentPage(0);
    comic.setLastRead(null);

    return comic;
  }

  public void updateVolume(final String userId, final Volume volume, final Boolean read) {
    this.mongoTemplate.find(Query.query(Criteria
        .where("publisher").is(volume.getPublisher())
        .and("series").is(volume.getSeries())
        .and("volume").is(volume.getName())), Comic.class)
      .stream().forEach(affectedComic -> this.updateComic(userId, affectedComic, read));
  }

  public void updateVolumeUntil(final String userId, final Comic comic) {
    this.mongoTemplate.find(Query.query(Criteria
        .where("publisher").is(comic.getPublisher())
        .and("series").is(comic.getSeries())
        .and("volume").is(comic.getVolume())
        .and("position").lte(comic.getPosition())), Comic.class)
        .forEach(affectedComic -> this.updateComic(userId, affectedComic, true));
  }
}
