package de.wasenweg.alfred;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import de.wasenweg.alfred.comics.Comic;
import de.wasenweg.alfred.scanner.ApiMetaDataReader;
import de.wasenweg.alfred.scanner.ComicVineService;
import de.wasenweg.alfred.settings.SettingsService;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.powermock.reflect.Whitebox;
import org.springframework.test.context.ContextConfiguration;

import java.io.File;
import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
@ContextConfiguration(classes = ApiMetaDataReader.class)
public class ApiMetaDataReaderTest {

  @InjectMocks
  private ApiMetaDataReader apiMetaDataReader;

  @Mock
  private ComicVineService comicVineService;

  @Mock
  private SettingsService settingsService;

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
    when(this.comicVineService.findVolumesBySeries("Batgirl", 1)).thenReturn(this.parseJson("search-batgirl.json"));
    final Comic comic = new Comic();
    comic.setSeries("Batgirl");
    comic.setPublisher("DC Comics");
    comic.setVolume("2011");
    final String result = Whitebox.invokeMethod(this.apiMetaDataReader, "findVolumeId", comic);
    assertThat(result).isEqualTo("42604");
  }

  @Test
  public void findVolumeWithOffset() throws Exception {
    when(this.comicVineService.findVolumesBySeries("Batman", 1)).thenReturn(this.parseJson("search-batman.json"));
    when(this.comicVineService.findVolumesBySeries("Batman", 2)).thenReturn(this.parseJson("search-batman-page2.json"));
    when(this.comicVineService.findVolumesBySeries("Batman", 3)).thenReturn(this.parseJson("search-batman-page3.json"));

    final Comic comic = new Comic();
    comic.setSeries("Batman");
    comic.setPublisher("Carlsen Comics");
    comic.setVolume("1991");
    String result = Whitebox.invokeMethod(this.apiMetaDataReader, "findVolumeId", comic);
    assertThat(result).isEqualTo("117375");

    comic.setPublisher("DC Comics");
    comic.setVolume("2011");
    result = Whitebox.invokeMethod(this.apiMetaDataReader, "findVolumeId", comic);
    assertThat(result).isEqualTo("42721");
  }

  private JsonNode parseJson(final String path) throws IOException {
    return new ObjectMapper().readTree(new File("src/test/resources/" + path));
  }
}
