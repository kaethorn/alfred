package de.wasenweg.alfred.unit;

import de.wasenweg.alfred.comics.Comic;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ComicConstructionTest {

  @Test
  void constructWithNew() throws Exception {
    final Comic comic = new Comic();
    assertThat(comic.getCurrentPage()).isEqualTo(0);
    assertThat(comic.isRead()).isFalse();
  }

  @Test
  void constructWithBuilder() throws Exception {
    final Comic comic = Comic.builder()
        .path("")
        .fileName("")
        .title("")
        .series("")
        .volume("")
        .number("")
        .position("")
        .year(2000)
        .month(5)
        .publisher("")
        .build();
    assertThat(comic.getCurrentPage()).isEqualTo(0);
    assertThat(comic.isRead()).isFalse();
  }

  @Test
  void updatePosition() throws Exception {
    final Comic comic = new Comic();
    comic.setNumber("1/2");
    assertThat(comic.getPosition()).isEqualTo("0000.5");
    comic.setNumber("1");
    assertThat(comic.getPosition()).isEqualTo("0001.0");
    comic.setNumber("0.5");
    assertThat(comic.getPosition()).isEqualTo("0000.5");
  }
}
