package de.wasenweg.komix.volumes;

import com.mongodb.client.result.UpdateResult;
import de.wasenweg.komix.comics.Comic;
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

    public UpdateResult updateRead(final Volume volume, final Boolean read) {
        final Query query = Query.query(Criteria
            .where("publisher").is(volume.getPublisher())
            .and("series").is(volume.getSeries())
            .and("volume").is(volume.getVolume()));

        final Update update = new Update();
        update.set("read", read);
        update.set("currentPage", null);
        if (read) {
            update.set("currentPage", 0);
            update.set("lastRead", new Date());
        }
        return mongoTemplate
                .updateMulti(query, update, Comic.class);
    }

    public UpdateResult updateReadUntil(final Comic comic) {
        final Query query = Query.query(Criteria
            .where("publisher").is(comic.getPublisher())
            .and("series").is(comic.getSeries())
            .and("volume").is(comic.getVolume())
            .and("position").lte(comic.getPosition()));

        final Update update = new Update();
        update.set("read", true);
        update.set("lastRead", new Date());
        update.set("currentPage", 0);

        return mongoTemplate
                .updateMulti(query, update, Comic.class);

    }
}
