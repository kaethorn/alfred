package de.wasenweg.komix.comics;

import com.mongodb.client.result.UpdateResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.Date;

@RequestMapping("/api/volumes")
@RestController
public class VolumeController {

    private final MongoTemplate mongoTemplate;

    @Autowired
    public VolumeController(final MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    @PutMapping("/markAsRead")
    @ResponseBody
    public String markAsRead(@Valid @RequestBody final Volume volume) {
        final Query select = Query.query(Criteria
          .where("publisher").is(volume.getPublisher())
          .and("series").is(volume.getSeries())
          .and("volume").is(volume.getVolume()));
        final Update update = new Update();
        update.set("read", true);
        update.set("lastRead", new Date());
        final UpdateResult updateResult = mongoTemplate
                .updateMulti(select, update, Comic.class);
        if (updateResult.wasAcknowledged()) {
            return "Success";
        }
        return "Error";
    }
}
