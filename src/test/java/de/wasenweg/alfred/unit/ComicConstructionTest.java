package de.wasenweg.alfred.unit;

import de.wasenweg.alfred.comics.Comic;

import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class ComicConstructionTest {

  @Test
  public void constructWithNew() throws Exception {
    final Comic comic = new Comic();
    assertThat(comic.getCurrentPage()).isEqualTo((short) 0);
    assertThat(comic.isRead()).isEqualTo(false);
  }

  @Test
  public void constructWithBuilder() throws Exception {
    final Comic comic = Comic.builder()
        .path("")
        .fileName("")
        .title("")
        .series("")
        .volume("")
        .number("")
        .position("")
        .year((short) 2000)
        .month((short) 5)
        .publisher("")
        .build();
    assertThat(comic.getCurrentPage()).isEqualTo((short) 0);
    assertThat(comic.isRead()).isEqualTo(false);
  }

  @Test
  public void updatePosition() throws Exception {
    final Comic comic = new Comic();
    comic.setNumber("1/2");
    assertThat(comic.getPosition()).isEqualTo("0000.5");
    comic.setNumber("1");
    assertThat(comic.getPosition()).isEqualTo("0001.0");
    comic.setNumber("0.5");
    assertThat(comic.getPosition()).isEqualTo("0000.5");
  }
}
