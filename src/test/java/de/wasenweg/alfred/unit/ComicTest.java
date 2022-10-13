package de.wasenweg.alfred.unit;

import de.wasenweg.alfred.comics.Comic;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ComicTest {

  @Test
  void setPathPartsMatchesPath() throws Exception {
    final Comic comic = new Comic();
    comic.setPath("/foo/DC Comics/Batman (1940)/Batman 400 (1940) bar.cbz");
    comic.setPathParts();
    assertThat(comic.getPublisher()).isEqualTo("DC Comics");
    assertThat(comic.getSeries()).isEqualTo("Batman");
    assertThat(comic.getVolume()).isEqualTo("1940");
    assertThat(comic.getNumber()).isEqualTo("400");
    assertThat(comic.getPosition()).isEqualTo("0400.0");
  }

  @Test
  void setPathPartsMatchesWithoutSuffix() throws Exception {
    final Comic comic = new Comic();
    comic.setPath("/home/foo/src/alfred/src/test/resources/fixtures/incomplete/DC Comics/Batman (1940)/Batman 701 (1940).cbz");
    comic.setPathParts();
    assertThat(comic.getPublisher()).isEqualTo("DC Comics");
    assertThat(comic.getSeries()).isEqualTo("Batman");
    assertThat(comic.getVolume()).isEqualTo("1940");
    assertThat(comic.getPosition()).isEqualTo("0701.0");
  }

  @Test
  void setPathPartsMatchesFractionalIssueNumbers() throws Exception {
    final Comic comic = new Comic();
    comic.setPath("/foo/DC Comics/Batman (1940)/Batman 102a (1940) bar.cbz");
    comic.setPathParts();
    assertThat(comic.getPublisher()).isEqualTo("DC Comics");
    assertThat(comic.getSeries()).isEqualTo("Batman");
    assertThat(comic.getVolume()).isEqualTo("1940");
    assertThat(comic.getNumber()).isEqualTo("102a");
    assertThat(comic.getPosition()).isEqualTo("0102.5");

    comic.setPath("/foo/DC Comics/Batman (1940)/Batman 1/2 (1940) bar.cbz");
    comic.setPathParts();
    assertThat(comic.getPublisher()).isEqualTo("DC Comics");
    assertThat(comic.getSeries()).isEqualTo("Batman");
    assertThat(comic.getVolume()).isEqualTo("1940");
    assertThat(comic.getNumber()).isEqualTo("1/2");
    assertThat(comic.getPosition()).isEqualTo("0000.5");

    comic.setPath("/foo/DC Comics/Batman (1940)/Batman ½ (1940) bar.cbz");
    comic.setPathParts();
    assertThat(comic.getPublisher()).isEqualTo("DC Comics");
    assertThat(comic.getSeries()).isEqualTo("Batman");
    assertThat(comic.getVolume()).isEqualTo("1940");
    assertThat(comic.getNumber()).isEqualTo("½");
    assertThat(comic.getPosition()).isEqualTo("0000.5");
  }

  @Test
  void setPathPartsRejectsNonMatchingVolumes() throws Exception {
    final Comic comic = new Comic();
    comic.setPath("/foo/DC Comics/Batman (2001)/Batman 400 (1940) bar.cbz");
    comic.setPathParts();
    assertThat(comic.getPublisher()).isNull();
    assertThat(comic.getSeries()).isNull();
    assertThat(comic.getVolume()).isNull();
    assertThat(comic.getNumber()).isNull();
    assertThat(comic.getPosition()).isNull();
  }

  @Test
  void setPathPartsRejectsNonMatchingSeries() throws Exception {
    final Comic comic = new Comic();
    comic.setPath("/foo/DC Comics/Batman (1940)/Batwoman 400 (1940) bar.cbz");
    comic.setPathParts();
    assertThat(comic.getPublisher()).isNull();
    assertThat(comic.getSeries()).isNull();
    assertThat(comic.getVolume()).isNull();
    assertThat(comic.getNumber()).isNull();
    assertThat(comic.getPosition()).isNull();
  }

  @Test
  void issueNumberEquals() throws Exception {
    assertThat(Comic.issueNumberEquals("001", "001")).isTrue();
    assertThat(Comic.issueNumberEquals("1", "001")).isTrue();
    assertThat(Comic.issueNumberEquals("1", "foo")).isFalse();
    assertThat(Comic.issueNumberEquals("2", "3")).isFalse();
    assertThat(Comic.issueNumberEquals("2a", "2.5")).isTrue();
    assertThat(Comic.issueNumberEquals("1/2", "000.5")).isTrue();
  }
}
