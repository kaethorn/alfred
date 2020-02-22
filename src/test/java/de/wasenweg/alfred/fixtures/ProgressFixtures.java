package de.wasenweg.alfred.fixtures;

import de.wasenweg.alfred.comics.Comic;
import de.wasenweg.alfred.progress.Progress;

import org.bson.types.ObjectId;

import java.util.GregorianCalendar;

public final class ProgressFixtures {

  private ProgressFixtures() {
    throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
  }

  public static Progress comicStarted(final Comic comic) {
    return Progress.builder()
        .comicId(new ObjectId(comic.getId()))
        .userId("mock-user-1")
        .currentPage(4)
        .lastRead(new GregorianCalendar(2019, 3, 20).getTime())
        .build();
  }

  public static Progress comicRead(final Comic comic, final int timeOffset) {
    return Progress.builder()
        .comicId(new ObjectId(comic.getId()))
        .userId("mock-user-1")
        .read(true)
        .lastRead(new GregorianCalendar(2019, 3, 1 + timeOffset).getTime())
        .build();
  }

  public static Progress comicRead(final Comic comic) {
    return comicRead(comic, 0);
  }
}
