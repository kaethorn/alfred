package de.wasenweg.alfred.progress;

import de.wasenweg.alfred.comics.Comic;
import de.wasenweg.alfred.volumes.Volume;

import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public class ProgressService {

  @Autowired
  private MongoTemplate mongoTemplate;

  public Comic updateComic(final String userId, final Comic comic, final Boolean read) {
    final Query query = Query.query(Criteria
        .where("comicId").is(new ObjectId(comic.getId()))
        .and("userId").is(userId));

    comic.setRead(read);
    comic.setCurrentPage(null);
    if (read) {
      comic.setCurrentPage((short) 0);
      comic.setLastRead(new Date());
    }

    final Update update = new Update();
    update.set("read", comic.isRead());
    update.set("currentPage", comic.getCurrentPage());
    update.set("lastRead", comic.getLastRead());

    mongoTemplate.upsert(query, update, Progress.class);
    return comic;
  }

  public void updateVolume(final String userId, final Volume volume, final Boolean read) {
    mongoTemplate.find(Query.query(Criteria
        .where("publisher").is(volume.getPublisher())
        .and("series").is(volume.getSeries())
        .and("volume").is(volume.getVolume())), Comic.class)
      .stream().forEach(affectedComic -> updateComic(userId, affectedComic, read));
  }

  public void updateVolumeUntil(final String userId, final Comic comic) {
    mongoTemplate.find(Query.query(Criteria
        .where("publisher").is(comic.getPublisher())
        .and("series").is(comic.getSeries())
        .and("volume").is(comic.getVolume())
        .and("position").lte(comic.getPosition())), Comic.class)
      .forEach(affectedComic -> updateComic(userId, affectedComic, true));
  }
}
