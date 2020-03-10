package de.wasenweg.alfred.fixtures;

import de.wasenweg.alfred.volumes.Volume;

public final class VolumeFixtures {

  public static final Volume VOLUME_1 = Volume.builder()
      .name(ComicFixtures.COMIC_V1_1.getVolume())
      .series(ComicFixtures.COMIC_V1_1.getSeries())
      .publisher(ComicFixtures.COMIC_V1_1.getPublisher())
      .issueCount(3)
      .read(false)
      .readCount(0)
      .firstComicId(ComicFixtures.COMIC_V1_1.getId())
      .build();

  private VolumeFixtures() {
    throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
  }
}
