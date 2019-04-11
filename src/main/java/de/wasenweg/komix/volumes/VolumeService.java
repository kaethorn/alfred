package de.wasenweg.komix.volumes;

import de.wasenweg.komix.comics.Comic;
import de.wasenweg.komix.progress.Progress;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public class VolumeService {

    @Autowired
    private MongoTemplate mongoTemplate;

    public boolean updateRead(final String userId, final Volume volume, final Boolean read) {
        return mongoTemplate.find(Query.query(Criteria
            .where("publisher").is(volume.getPublisher())
            .and("series").is(volume.getSeries())
            .and("volume").is(volume.getVolume())), Comic.class)
            .stream()
            .map(affectedComic -> {
                final Query query = Query.query(Criteria
                            .where("comicId").is(affectedComic.getId())
                            .and("userId").is(userId));

                final Update update = new Update();
                update.set("read", read);
                update.set("currentPage", null);
                if (read) {
                    update.set("currentPage", 0);
                    update.set("lastRead", new Date());
                }

                return mongoTemplate.upsert(query, update, Progress.class).wasAcknowledged();
            }).allMatch(Boolean::valueOf);
    }

    public boolean updateReadUntil(final String userId, final Comic comic) {
        return mongoTemplate.find(Query.query(Criteria
            .where("publisher").is(comic.getPublisher())
            .and("series").is(comic.getSeries())
            .and("volume").is(comic.getVolume())
            .and("position").lte(comic.getPosition())), Comic.class)
            .stream()
            .map(affectedComic -> {
                final Query query = Query.query(Criteria
                            .where("comicId").is(affectedComic.getId())
                            .and("userId").is(userId));

                final Update update = new Update();
                update.set("read", true);
                update.set("currentPage", 0);
                update.set("lastRead", new Date());

                return mongoTemplate.upsert(query, update, Progress.class).wasAcknowledged();
            }).allMatch(Boolean::valueOf);
    }
}
