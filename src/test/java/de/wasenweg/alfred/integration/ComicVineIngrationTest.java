package de.wasenweg.alfred.integration;

import de.wasenweg.alfred.AlfredApplication;
import de.wasenweg.alfred.EnableEmbeddedMongo;
import de.wasenweg.alfred.TestUtil;
import de.wasenweg.alfred.comics.Comic;
import de.wasenweg.alfred.comics.ComicRepository;
import de.wasenweg.alfred.mockserver.MockServerUtil;
import de.wasenweg.alfred.progress.ProgressRepository;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.io.TempDir;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.w3c.dom.Document;
import reactor.test.StepVerifier;

import java.io.File;
import java.io.IOException;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = { AlfredApplication.class }, webEnvironment = WebEnvironment.RANDOM_PORT)
@DirtiesContext
@EnableAutoConfiguration
@EnableEmbeddedMongo
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@ActiveProfiles("test")
public class ComicVineIngrationTest {

  @TempDir
  public File testBed;

  @LocalServerPort
  private int port;

  private final ComicRepository comicRepository;
  private final ProgressRepository progressRepository;
  private final IntegrationTestHelper helper;

  @BeforeAll
  public static void startServer() throws IOException {
    MockServerUtil.startServer();
  }

  @AfterAll
  public static void stopServer() {
    MockServerUtil.stop();
  }

  @AfterEach
  public void tearDown() {
    this.comicRepository.deleteAll();
    this.progressRepository.deleteAll();
  }

  @Test
  public void scrapesMetaData() throws Exception {
    // Given
    this.helper.setComicsPath("src/test/resources/fixtures/incomplete", this.testBed);
    final String comicPath = this.testBed.getAbsolutePath() + "/DC Comics/Batman (1940)/Batman 701 (1940).cbz";
    assertThat(TestUtil.zipContainsFile(comicPath, "ComicInfo.xml")).isFalse();

    // When
    StepVerifier.create(TestUtil.triggerScan(this.port))
        .expectNext("start")
        .expectNext("1")
        .expectNextCount(1)
        .expectNext("cleanUp")
        .expectNext("association")
        .expectNext("done")
        .thenCancel()
        .verify();

    // Then
    // Verify that meta data is stored in the DB
    final List<Comic> comics = this.comicRepository.findAll();
    assertThat(comics.size()).isEqualTo(1);

    final Comic comic = comics.get(0);
    assertThat(comic.getSeries()).isEqualTo("Batman");
    assertThat(comic.getPublisher()).isEqualTo("DC Comics");
    assertThat(comic.getVolume()).isEqualTo("1940");
    assertThat(comic.getNumber()).isEqualTo("701");
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

    // Verify that meta data is stored in the embedded XML file
    assertThat(TestUtil.zipContainsFile(comicPath, "ComicInfo.xml")).isTrue();
    final Document document = TestUtil.parseComicInfo(comicPath);
    assertThat(TestUtil.getText(document, "Series")).isEqualTo("Batman");
    assertThat(TestUtil.getText(document, "Publisher")).isEqualTo("DC Comics");
    assertThat(TestUtil.getText(document, "Volume")).isEqualTo("1940");
    assertThat(TestUtil.getText(document, "Number")).isEqualTo("701");
    assertThat(TestUtil.getText(document, "Title")).isEqualTo("R.I.P. The Missing Chapter, Part 1: The Hole In Things");
    assertThat(TestUtil.getText(document, "Summary").length()).isEqualTo(391);
    assertThat(TestUtil.getText(document, "Year")).isEqualTo("2010");
    assertThat(TestUtil.getText(document, "Month")).isEqualTo("9");
    assertThat(TestUtil.getText(document, "Characters"))
        .isEqualTo("Alfred Pennyworth, Batman, Doctor Hurt, Ellie, Jezebel Jet, Martha Wayne, Superman, Thomas Wayne");
    assertThat(TestUtil.getText(document, "Teams")).isEqualTo("Superman/Batman");
    assertThat(TestUtil.getText(document, "Locations")).isEqualTo("Batcave, Gotham City, Wayne Manor");
    assertThat(TestUtil.getText(document, "Writer")).isEqualTo("Grant Morrison");
    assertThat(TestUtil.getText(document, "Penciller")).isEqualTo("");
    assertThat(TestUtil.getText(document, "Inker")).isEqualTo("");
    assertThat(TestUtil.getText(document, "Colorist")).isEqualTo("Ian Hannin");
    assertThat(TestUtil.getText(document, "Letterer")).isEqualTo("Jared K. Fletcher");
    assertThat(TestUtil.getText(document, "CoverArtist")).isEqualTo("Tony Daniel");
    assertThat(TestUtil.getText(document, "Editor")).isEqualTo("Dan DiDio, Janelle Asselin (Siegel), Mike Marts");
    assertThat(TestUtil.getText(document, "Web")).isEqualTo("https://comicvine.gamespot.com/batman-701-rip-the-missing-chapter-part-1-the-hole/4000-224555/");
  }
}
