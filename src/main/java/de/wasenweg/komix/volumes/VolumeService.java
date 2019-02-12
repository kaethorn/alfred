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

    private final MongoTemplate mongoTemplate;

    @Autowired
    public VolumeService(final MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    public UpdateResult updateRead(final Volume volume, final Boolean read) {
        final Query select = Query.query(Criteria
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
                .updateMulti(select, update, Comic.class);
    }
}
