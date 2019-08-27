package de.wasenweg.alfred;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import de.wasenweg.alfred.comics.Comic;
import de.wasenweg.alfred.scanner.ApiMetaDataReader;
import de.wasenweg.alfred.scanner.ComicVineService;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.powermock.reflect.Whitebox;
import org.springframework.test.context.ContextConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
@ContextConfiguration(classes = ApiMetaDataReader.class)
public class ApiMetaDataReaderTest {

  @InjectMocks
  private ApiMetaDataReader apiMetaDataReader;

  @Mock
  private ComicVineService comicVineService;

  @Test
  public void setPathPartsMatchesPath() throws Exception {
    final Comic comic = new Comic();
    comic.setPath("/foo/DC Comics/Batman (1940)/Batman 400 (1940) bar.cbz");
    Whitebox.invokeMethod(this.apiMetaDataReader, "setPathParts", comic);
    assertThat(comic.getPublisher()).isEqualTo("DC Comics");
    assertThat(comic.getSeries()).isEqualTo("Batman");
    assertThat(comic.getVolume()).isEqualTo("1940");
    assertThat(comic.getNumber()).isEqualTo("400");
    assertThat(comic.getPosition()).isEqualTo("0400.0");
  }

  @Test
  public void setPathPartsMatchesWithoutSuffix() throws Exception {
    final Comic comic = new Comic();
    comic.setPath("/home/foo/src/alfred/src/test/resources/fixtures/incomplete/DC Comics/Batman (1940)/Batman 701 (1940).cbz");
    Whitebox.invokeMethod(this.apiMetaDataReader, "setPathParts", comic);
    assertThat(comic.getPublisher()).isEqualTo("DC Comics");
    assertThat(comic.getSeries()).isEqualTo("Batman");
    assertThat(comic.getVolume()).isEqualTo("1940");
    assertThat(comic.getPosition()).isEqualTo("0701.0");
  }

  @Test
  public void setPathPartsMatchesFractionalIssueNumbers() throws Exception {
    final Comic comic = new Comic();
    comic.setPath("/foo/DC Comics/Batman (1940)/Batman 102a (1940) bar.cbz");
    Whitebox.invokeMethod(this.apiMetaDataReader, "setPathParts", comic);
    assertThat(comic.getPublisher()).isEqualTo("DC Comics");
    assertThat(comic.getSeries()).isEqualTo("Batman");
    assertThat(comic.getVolume()).isEqualTo("1940");
    assertThat(comic.getNumber()).isEqualTo("102a");
    assertThat(comic.getPosition()).isEqualTo("0102.5");

    comic.setPath("/foo/DC Comics/Batman (1940)/Batman 1/2 (1940) bar.cbz");
    Whitebox.invokeMethod(this.apiMetaDataReader, "setPathParts", comic);
    assertThat(comic.getPublisher()).isEqualTo("DC Comics");
    assertThat(comic.getSeries()).isEqualTo("Batman");
    assertThat(comic.getVolume()).isEqualTo("1940");
    assertThat(comic.getNumber()).isEqualTo("1/2");
    assertThat(comic.getPosition()).isEqualTo("0000.5");

    comic.setPath("/foo/DC Comics/Batman (1940)/Batman ½ (1940) bar.cbz");
    Whitebox.invokeMethod(this.apiMetaDataReader, "setPathParts", comic);
    assertThat(comic.getPublisher()).isEqualTo("DC Comics");
    assertThat(comic.getSeries()).isEqualTo("Batman");
    assertThat(comic.getVolume()).isEqualTo("1940");
    assertThat(comic.getNumber()).isEqualTo("½");
    assertThat(comic.getPosition()).isEqualTo("0000.5");
  }

  @Test
  public void setPathPartsRejectsNonMatchingVolumes() throws Exception {
    final Comic comic = new Comic();
    comic.setPath("/foo/DC Comics/Batman (2001)/Batman 400 (1940) bar.cbz");
    Whitebox.invokeMethod(this.apiMetaDataReader, "setPathParts", comic);
    assertThat(comic.getPublisher()).isNull();
    assertThat(comic.getSeries()).isNull();
    assertThat(comic.getVolume()).isNull();
    assertThat(comic.getNumber()).isNull();
    assertThat(comic.getPosition()).isNull();
  }

  @Test
  public void setPathPartsRejectsNonMatchingSeries() throws Exception {
    final Comic comic = new Comic();
    comic.setPath("/foo/DC Comics/Batman (1940)/Batwoman 400 (1940) bar.cbz");
    Whitebox.invokeMethod(this.apiMetaDataReader, "setPathParts", comic);
    assertThat(comic.getPublisher()).isNull();
    assertThat(comic.getSeries()).isNull();
    assertThat(comic.getVolume()).isNull();
    assertThat(comic.getNumber()).isNull();
    assertThat(comic.getPosition()).isNull();
  }

  @Test
  public void findVolume() throws Exception {
    when(this.comicVineService.findVolumesBySeries("Batgirl", 0)).thenReturn(this.parseJson("search-batgirl.json"));
    final Comic comic = new Comic();
    comic.setSeries("Batgirl");
    comic.setPublisher("DC Comics");
    comic.setVolume("2011");
    final String result = Whitebox.invokeMethod(this.apiMetaDataReader, "findVolumeId",
        comic.getPublisher(), comic.getSeries(), comic.getVolume());
    assertThat(result).isEqualTo("42604");
  }

  @Test
  public void findVolumeWithOffset() throws Exception {
    when(this.comicVineService.findVolumesBySeries("Batman", 0)).thenReturn(this.parseJson("search-batman.json"));
    when(this.comicVineService.findVolumesBySeries("Batman", 1)).thenReturn(this.parseJson("search-batman-page2.json"));
    when(this.comicVineService.findVolumesBySeries("Batman", 2)).thenReturn(this.parseJson("search-batman-page3.json"));

    final Comic comic = new Comic();
    comic.setSeries("Batman");
    comic.setPublisher("Carlsen Comics");
    comic.setVolume("1991");
    String result = Whitebox.invokeMethod(this.apiMetaDataReader, "findVolumeId",
        comic.getPublisher(), comic.getSeries(), comic.getVolume());
    assertThat(result).isEqualTo("117375");

    comic.setPublisher("DC Comics");
    comic.setVolume("2011");
    result = Whitebox.invokeMethod(this.apiMetaDataReader, "findVolumeId",
        comic.getPublisher(), comic.getSeries(), comic.getVolume());
    assertThat(result).isEqualTo("42721");
  }

  @Test
  public void findVolumeIssues() throws Exception {
    when(this.comicVineService.findIssuesInVolume("796", 0)).thenReturn(this.parseJson("issues-batman-page1.json"));
    when(this.comicVineService.findIssuesInVolume("796", 1)).thenReturn(this.parseJson("issues-batman-page2.json"));
    when(this.comicVineService.findIssuesInVolume("796", 2)).thenReturn(this.parseJson("issues-batman-page3.json"));
    when(this.comicVineService.findIssuesInVolume("796", 3)).thenReturn(this.parseJson("issues-batman-page4.json"));
    when(this.comicVineService.findIssuesInVolume("796", 4)).thenReturn(this.parseJson("issues-batman-page5.json"));
    when(this.comicVineService.findIssuesInVolume("796", 5)).thenReturn(this.parseJson("issues-batman-page6.json"));
    when(this.comicVineService.findIssuesInVolume("796", 6)).thenReturn(this.parseJson("issues-batman-page7.json"));
    when(this.comicVineService.findIssuesInVolume("796", 7)).thenReturn(this.parseJson("issues-batman-page8.json"));
    final List<JsonNode> result = Whitebox.invokeMethod(this.apiMetaDataReader, "findVolumeIssues", "796");
    assertThat(result.size()).isEqualTo(716);
  }

  @Test
  public void getIssueDetails() throws Exception {
    when(this.comicVineService.getIssueDetails("https://comicvine.gamespot.com/api/issue/4000-224555/")).thenReturn(this.parseJson("batman-701.json"));
    final Comic comic = new Comic();
    comic.setSeries("Batman");
    comic.setPublisher("DC Comics");
    comic.setVolume("1940");
    comic.setNumber("701");
    Whitebox.invokeMethod(this.apiMetaDataReader, "applyIssueDetails", "https://comicvine.gamespot.com/api/issue/4000-224555/", comic);
    assertThat(comic.getTitle()).isEqualTo("R.I.P. The Missing Chapter, Part 1: The Hole In Things");
    assertThat(comic.getSummary().length()).isEqualTo(391);
    assertThat(comic.getYear()).isEqualTo((short) 2010);
    assertThat(comic.getMonth()).isEqualTo((short) 9);
    assertThat(comic.getCharacters())
      .isEqualTo("Alfred Pennyworth, Batman, Doctor Hurt, Ellie, Jezebel Jet, Martha Wayne, Superman, Thomas Wayne");
    assertThat(comic.getTeams())
      .isEqualTo("Superman/Batman");
    assertThat(comic.getLocations())
      .isEqualTo("Batcave, Gotham City, Wayne Manor");
    assertThat(comic.getWriter()).isEqualTo("Grant Morrison");
    assertThat(comic.getPenciller()).isNull();
    assertThat(comic.getInker()).isNull();
    assertThat(comic.getColorist()).isEqualTo("Ian Hannin");
    assertThat(comic.getLetterer()).isEqualTo("Jared K. Fletcher");
    assertThat(comic.getCoverArtist()).isEqualTo("Tony Daniel");
    assertThat(comic.getEditor()).isEqualTo("Dan DiDio, Janelle Asselin (Siegel), Mike Marts");
    assertThat(comic.getWeb()).isEqualTo("https://comicvine.gamespot.com/batman-701-rip-the-missing-chapter-part-1-the-hole/4000-224555/");
  }

  private JsonNode parseJson(final String path) throws IOException {
    return new ObjectMapper().readTree(new File("src/test/resources/" + path));
  }
}
