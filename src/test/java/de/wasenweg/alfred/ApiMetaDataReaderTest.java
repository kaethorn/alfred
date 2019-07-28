package de.wasenweg.alfred;

import de.wasenweg.alfred.comics.Comic;
import de.wasenweg.alfred.scanner.ApiMetaDataReader;
import de.wasenweg.alfred.settings.SettingsService;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.powermock.reflect.Whitebox;
import org.springframework.test.context.ContextConfiguration;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(MockitoJUnitRunner.class)
@ContextConfiguration(classes = ApiMetaDataReader.class)
public class ApiMetaDataReaderTest {

  @InjectMocks
  private ApiMetaDataReader apiMetaDataReader;

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
    // TODO mock API requests
    final String result = Whitebox.invokeMethod(this.apiMetaDataReader, "findVolumeId", "Batgirl");
    assertThat(result).isEqualTo("0");
  }
}
