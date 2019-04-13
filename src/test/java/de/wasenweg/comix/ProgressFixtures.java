package de.wasenweg.comix;

import de.wasenweg.komix.comics.Comic;
import de.wasenweg.komix.progress.Progress;

import org.bson.types.ObjectId;

import java.util.GregorianCalendar;

public class ProgressFixtures {

    public ProgressFixtures() {
    }

    // FIXME why can't these be static?
    public Progress comicStarted(final Comic comic) {
        return Progress.builder()
            .comicId(new ObjectId(comic.getId()))
            .userId(OAuth2Helpers.MOCK_USER_ID)
            .currentPage((short) 4)
            .lastRead(new GregorianCalendar(2019, 3, 20).getTime())
            .build();
    }

    public Progress comicRead(final Comic comic) {
        return comicRead(comic, 0);
    }

    public Progress comicRead(final Comic comic, final int timeOffset) {
        return Progress.builder()
            .comicId(new ObjectId(comic.getId()))
            .userId(OAuth2Helpers.MOCK_USER_ID)
            .read(true)
            .lastRead(new GregorianCalendar(2019, 3, 1 + timeOffset).getTime())
            .build();
    }
}
