package de.wasenweg.alfred.unit;

import com.fasterxml.jackson.databind.JsonNode;
import de.wasenweg.alfred.comics.Comic;
import de.wasenweg.alfred.scanner.ApiMetaDataService;
import de.wasenweg.alfred.scanner.ComicVineService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.ContextConfiguration;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@ContextConfiguration(classes = ApiMetaDataService.class)
public class ApiMetaDataReaderTest {

  @InjectMocks
  private transient ApiMetaDataService apiMetaDataService;

  @Mock
  private transient ComicVineService comicVineService;

  @Test
  public void findVolume() throws Exception {
    when(this.comicVineService.findVolumesBySeries("Batgirl", 0)).thenReturn(TestHelper.parseJson("search-batgirl.json"));
    final Comic comic = new Comic();
    comic.setSeries("Batgirl");
    comic.setPublisher("DC Comics");
    comic.setVolume("2011");
    final String result = this.apiMetaDataService.findVolumeId(
        comic.getPublisher(), comic.getSeries(), comic.getVolume());
    assertThat(result).isEqualTo("42604");
  }

  @Test
  public void findVolumeWithOffset() throws Exception {
    when(this.comicVineService.findVolumesBySeries("Batman", 0)).thenReturn(TestHelper.parseJson("search-batman.json"));
    when(this.comicVineService.findVolumesBySeries("Batman", 1)).thenReturn(TestHelper.parseJson("search-batman-page2.json"));
    when(this.comicVineService.findVolumesBySeries("Batman", 2)).thenReturn(TestHelper.parseJson("search-batman-page3.json"));

    final Comic comic = new Comic();
    comic.setSeries("Batman");
    comic.setPublisher("Carlsen Comics");
    comic.setVolume("1991");
    String result = this.apiMetaDataService.findVolumeId(
        comic.getPublisher(), comic.getSeries(), comic.getVolume());
    assertThat(result).isEqualTo("117375");

    comic.setPublisher("DC Comics");
    comic.setVolume("2011");
    result = this.apiMetaDataService.findVolumeId(
        comic.getPublisher(), comic.getSeries(), comic.getVolume());
    assertThat(result).isEqualTo("42721");
  }

  @Test
  public void findVolumeIssues() throws Exception {
    when(this.comicVineService.findIssuesInVolume("796", 0)).thenReturn(TestHelper.parseJson("issues-batman-page1.json"));
    when(this.comicVineService.findIssuesInVolume("796", 1)).thenReturn(TestHelper.parseJson("issues-batman-page2.json"));
    when(this.comicVineService.findIssuesInVolume("796", 2)).thenReturn(TestHelper.parseJson("issues-batman-page3.json"));
    when(this.comicVineService.findIssuesInVolume("796", 3)).thenReturn(TestHelper.parseJson("issues-batman-page4.json"));
    when(this.comicVineService.findIssuesInVolume("796", 4)).thenReturn(TestHelper.parseJson("issues-batman-page5.json"));
    when(this.comicVineService.findIssuesInVolume("796", 5)).thenReturn(TestHelper.parseJson("issues-batman-page6.json"));
    when(this.comicVineService.findIssuesInVolume("796", 6)).thenReturn(TestHelper.parseJson("issues-batman-page7.json"));
    when(this.comicVineService.findIssuesInVolume("796", 7)).thenReturn(TestHelper.parseJson("issues-batman-page8.json"));
    final List<JsonNode> result = this.apiMetaDataService.findVolumeIssues("796");
    assertThat(result.size()).isEqualTo(716);
  }

  @Test
  public void getIssueDetails() throws Exception {
    when(this.comicVineService.getIssueDetails("https://comicvine.gamespot.com/api/issue/4000-224555/"))
      .thenReturn(TestHelper.parseJson("batman-701.json"));

    final Comic comic = new Comic();
    comic.setSeries("Batman");
    comic.setPublisher("DC Comics");
    comic.setVolume("1940");
    comic.setNumber("701");

    this.apiMetaDataService.applyIssueDetails("https://comicvine.gamespot.com/api/issue/4000-224555/", comic);
    assertThat(comic.getTitle()).isEqualTo("R.I.P. The Missing Chapter, Part 1: The Hole In Things");
    assertThat(comic.getSummary().length()).isEqualTo(391);
    assertThat(comic.getYear()).isEqualTo(2010);
    assertThat(comic.getMonth()).isEqualTo(9);
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
}
