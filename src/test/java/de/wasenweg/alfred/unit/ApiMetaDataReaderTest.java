package de.wasenweg.alfred.unit;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import de.wasenweg.alfred.comics.Comic;
import de.wasenweg.alfred.scanner.ApiMetaDataService;
import de.wasenweg.alfred.scanner.ComicVineService;
import de.wasenweg.alfred.scanner.ScannerIssue;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ApiMetaDataReaderTest {

  @Spy
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

  @Test
  public void parseWithInvalidIssueNumber() throws Exception {
    doReturn("").when(this.apiMetaDataService).findVolumeId("DC Comics", "Batman", "1940");
    final Comic comic = new Comic();
    comic.setPath("/c/DC Comics/Batman (1940)/Batman 1/3 (1940).cbz");

    final List<ScannerIssue> scannerIssues = this.apiMetaDataService.set(comic);

    assertThat(scannerIssues.size()).isEqualTo(1);
    assertThat(scannerIssues.get(0).getMessage()).isEqualTo("Couldn't read number '1/3'. Falling back to '0'.");
    assertThat(comic.getPosition()).isEqualTo("0000.0");
  }

  @Test
  public void parseWithMissingAttributes() throws Exception {
    doReturn("").when(this.apiMetaDataService).findVolumeId(null, null, null);
    final Comic comic = new Comic();
    comic.setPath("/c/Batman 3 (1940).cbz");

    final List<ScannerIssue> scannerIssues = this.apiMetaDataService.set(comic);

    assertThat(scannerIssues.size()).isEqualTo(1);
    assertThat(scannerIssues.get(0).getMessage()).isEqualTo("Missing meta data: publisher, series, volume, number");
  }

  @Test
  public void parseWithoutVolumeIssues() throws Exception {
    doReturn("456").when(this.apiMetaDataService).findVolumeId("DC Comics", "Batman", "1940");
    doReturn(new ArrayList<>()).when(this.apiMetaDataService).findVolumeIssues("456");
    final Comic comic = new Comic();
    comic.setSeries("Batman");
    comic.setPublisher("DC Comics");
    comic.setVolume("1940");
    comic.setNumber("701");

    final List<ScannerIssue> scannerIssues = this.apiMetaDataService.set(comic);
    assertThat(scannerIssues.size()).isEqualTo(0);
  }

  @Test
  public void findIssueDetailsUrlWithoutMatches() throws Exception {
    doReturn("111").when(this.apiMetaDataService).findVolumeId("DC Comics", "Batman", "1940");
    final List<JsonNode> issues = Arrays.asList(
        this.createJsonObject(Map.of("issue_number", "1")),
        this.createJsonObject(Map.of("issue_number", "2")),
        this.createJsonObject(Map.of("issue_number", "3")));
    doReturn(issues).when(this.apiMetaDataService).findVolumeIssues("111");
    doNothing().when(this.apiMetaDataService).applyIssueDetails(any(), any());
    final Comic comic = new Comic();
    comic.setPath("/c/DC Comics/Batman (1940)/Batman 4 (1940).cbz");

    final List<ScannerIssue> scannerIssues = this.apiMetaDataService.set(comic);

    assertThat(scannerIssues.size()).isEqualTo(1);
    assertThat(scannerIssues.get(0).getMessage()).isEqualTo("No matching issue found");
  }

  @Test
  public void findIssueDetailsUrlWithMoreThanOneMatch() throws Exception {
    doReturn("111").when(this.apiMetaDataService).findVolumeId("DC Comics", "Batman", "1940");
    final List<JsonNode> issues = Arrays.asList(
        this.createJsonObject(Map.of("issue_number", "4", "api_detail_url", "http://result")),
        this.createJsonObject(Map.of("issue_number", "3")),
        this.createJsonObject(Map.of("issue_number", "4")));
    doReturn(issues).when(this.apiMetaDataService).findVolumeIssues("111");
    doNothing().when(this.apiMetaDataService).applyIssueDetails(any(), any());
    final Comic comic = new Comic();
    comic.setPath("/c/DC Comics/Batman (1940)/Batman 4 (1940).cbz");

    final List<ScannerIssue> scannerIssues = this.apiMetaDataService.set(comic);

    assertThat(scannerIssues.size()).isEqualTo(1);
    assertThat(scannerIssues.get(0).getMessage()).isEqualTo("No unique issue found");
  }

  @Test
  public void findVolumeIdWithoutResults() throws Exception {
    when(this.comicVineService.findVolumesBySeries("Batman", 0))
        .thenReturn(this.createJsonObject(
          Map.of("number_of_total_results", 0, "limit", 10, "results", new ObjectMapper().createArrayNode())));
    assertThat(this.apiMetaDataService.findVolumeId("DC Comics", "Batman", "1940"))
        .isEqualTo("");
  }

  @Test
  public void findVolumeIssuesWithoutResults() throws Exception {
    when(this.comicVineService.findIssuesInVolume("123", 0))
        .thenReturn(this.createJsonObject(
          Map.of("number_of_total_results", 0, "limit", 10, "results", new ObjectMapper().createArrayNode())));
    assertThat(this.apiMetaDataService.findVolumeIssues("123").size())
        .isEqualTo(0);
  }

  @Test
  public void applyIssueDetailsWithoutDetails() throws Exception {
    final Comic comic = new Comic();
    when(this.comicVineService.getIssueDetails("http://issue.url"))
        .thenReturn(this.createJsonObject(Map.of("results",
          this.createJsonObject(Map.of(
            "cover_date", "2010-09-01",
            "person_credits", new ObjectMapper().createArrayNode(),
            "site_detail_url", "")))));
    this.apiMetaDataService.applyIssueDetails("http://issue.url", comic);
    assertThat(comic.getCharacters()).isEqualTo("");
  }

  /**
   * Creates an object compatible with JSON returned from the API.
   *
   * @param pairs Map of values
   * @return The JSON object.
   */
  private <T> JsonNode createJsonObject(final Map<String, T> pairs) {
    final JsonNode rootNode = new ObjectMapper().createObjectNode();
    for (final Entry<String, T> pair: pairs.entrySet()) {
      if (pair.getValue() instanceof String) {
        ((ObjectNode) rootNode).put(pair.getKey(), (String) pair.getValue());
      } else if (pair.getValue() instanceof Integer) {
        ((ObjectNode) rootNode).put(pair.getKey(), (Integer) pair.getValue());
      } else if (pair.getValue() instanceof JsonNode) {
        ((ObjectNode) rootNode).set(pair.getKey(), (JsonNode) pair.getValue());
      }
    }
    return rootNode;
  }
}
