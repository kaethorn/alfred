package de.wasenweg.alfred.integration;

import de.wasenweg.alfred.EnableEmbeddedMongo;
import de.wasenweg.alfred.TestUtil;
import de.wasenweg.alfred.comics.Comic;
import de.wasenweg.alfred.comics.ComicRepository;
import de.wasenweg.alfred.fixtures.ComicFixtures;
import de.wasenweg.alfred.fixtures.ProgressFixtures;
import de.wasenweg.alfred.mockserver.MockServerUtil;
import de.wasenweg.alfred.progress.Progress;
import de.wasenweg.alfred.progress.ProgressRepository;
import de.wasenweg.alfred.scanner.ScannerIssue;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.hateoas.MediaTypes;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.w3c.dom.Document;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@DirtiesContext
@EnableEmbeddedMongo
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@ActiveProfiles("test")
public class ComicsIntegrationTest {

  @TempDir
  public File testBed;

  private final MockMvc mockMvc;
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
  public void getAllComics() throws Exception {
    this.comicRepository.saveAll(Arrays.asList(
        ComicFixtures.COMIC_V1_1,
        ComicFixtures.COMIC_V1_2));

    this.mockMvc.perform(get("/api/comics"))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaTypes.HAL_JSON_VALUE))
        .andExpect(jsonPath("$._embedded.comics.length()").value(2))
        .andExpect(jsonPath("$._embedded.comics[0].title").value(ComicFixtures.COMIC_V1_1.getTitle()))
        .andExpect(jsonPath("$._embedded.comics[1].title").value(ComicFixtures.COMIC_V1_2.getTitle()));
  }

  @Test
  public void findById() throws Exception {
    // Given
    final Comic comic = this.comicRepository.save(ComicFixtures.COMIC_V1_1);
    this.progressRepository.save(ProgressFixtures.comicStarted(ComicFixtures.COMIC_V1_1));

    // When / Then
    this.mockMvc.perform(get("/api/comics/" + comic.getId()))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaTypes.HAL_JSON_VALUE))
        .andExpect(jsonPath("$.path").value(ComicFixtures.COMIC_V1_1.getPath()))
        .andExpect(jsonPath("$.currentPage").value(4));
  }

  @Test
  public void updateProgress() throws Exception {
    final Comic comic = this.comicRepository.save(Comic.builder()
        .path("/701.cbz")
        .fileName("Batman 701 (1940).cbz")
        .number("")
        .publisher("")
        .series("")
        .volume("")
        .build());
    comic.setCurrentPage(7);

    this.mockMvc.perform(put("/api/comics/progress")
        .contentType(MediaType.APPLICATION_JSON_VALUE)
        .accept(MediaTypes.HAL_JSON_VALUE)
        .content(TestUtil.toJson(comic)))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaTypes.HAL_JSON_VALUE))
        .andExpect(jsonPath("$.currentPage").value(7));

    final List<Progress> progress = this.progressRepository.findAll();
    assertThat(progress.size()).isEqualTo(1);
    assertThat(progress.get(0).getComicId().toString()).isEqualTo(comic.getId());
    assertThat(progress.get(0).getCurrentPage()).isEqualTo(7);
    assertThat(this.comicRepository.findById(comic.getId()).get().getCurrentPage()).isEqualTo(0);
  }

  @Test
  public void findLastReadForVolumeWithReadIssue() throws Exception {
    this.comicRepository.saveAll(Arrays.asList(
        ComicFixtures.COMIC_V1_1, // read
        ComicFixtures.COMIC_V1_2,
        ComicFixtures.COMIC_V1_3));

    this.progressRepository.save(ProgressFixtures.comicRead(ComicFixtures.COMIC_V1_1));

    this.mockMvc.perform(get("/api/comics/search/findLastReadForVolume")
        .param("publisher", ComicFixtures.COMIC_V1_1.getPublisher())
        .param("series", ComicFixtures.COMIC_V1_1.getSeries())
        .param("volume", ComicFixtures.COMIC_V1_1.getVolume()))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaTypes.HAL_JSON_VALUE))
        .andExpect(jsonPath("$.title").value(ComicFixtures.COMIC_V1_2.getTitle()));
  }

  @Test
  public void findLastReadForVolumeWithStartedIssue() throws Exception {
    this.comicRepository.saveAll(Arrays.asList(
        ComicFixtures.COMIC_V1_1, // started
        ComicFixtures.COMIC_V1_2,
        ComicFixtures.COMIC_V1_3));

    this.progressRepository.save(ProgressFixtures.comicStarted(ComicFixtures.COMIC_V1_1));

    this.mockMvc.perform(get("/api/comics/search/findLastReadForVolume")
        .param("publisher", ComicFixtures.COMIC_V1_1.getPublisher())
        .param("series", ComicFixtures.COMIC_V1_1.getSeries())
        .param("volume", ComicFixtures.COMIC_V1_1.getVolume()))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaTypes.HAL_JSON_VALUE))
        .andExpect(jsonPath("$.title").value(ComicFixtures.COMIC_V1_1.getTitle()));
  }

  @Test
  public void findLastReadForUnstartedVolume() throws Exception {
    this.comicRepository.saveAll(Arrays.asList(
        ComicFixtures.COMIC_V1_1,
        ComicFixtures.COMIC_V1_2,
        ComicFixtures.COMIC_V1_3));

    this.mockMvc.perform(get("/api/comics/search/findLastReadForVolume")
        .param("publisher", ComicFixtures.COMIC_V1_1.getPublisher())
        .param("series", ComicFixtures.COMIC_V1_1.getSeries())
        .param("volume", ComicFixtures.COMIC_V1_1.getVolume()))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaTypes.HAL_JSON_VALUE))
        .andExpect(jsonPath("$.title").value(ComicFixtures.COMIC_V1_1.getTitle()));
  }

  @Test
  public void findLastReadForVolumeWithMixedState() throws Exception {
    this.comicRepository.saveAll(Arrays.asList(
        ComicFixtures.COMIC_V1_1,   // read
        ComicFixtures.COMIC_V1_2,
        ComicFixtures.COMIC_V1_3)); // read

    this.progressRepository.saveAll(Arrays.asList(
        ProgressFixtures.comicRead(ComicFixtures.COMIC_V1_1),
        ProgressFixtures.comicRead(ComicFixtures.COMIC_V1_3)));

    this.mockMvc.perform(get("/api/comics/search/findLastReadForVolume")
        .param("publisher", ComicFixtures.COMIC_V1_1.getPublisher())
        .param("series", ComicFixtures.COMIC_V1_1.getSeries())
        .param("volume", ComicFixtures.COMIC_V1_1.getVolume()))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaTypes.HAL_JSON_VALUE))
        .andExpect(jsonPath("$.title").value(ComicFixtures.COMIC_V1_2.getTitle()));
  }

  @Test
  public void findLastReadForCompletedVolume() throws Exception {
    this.comicRepository.saveAll(Arrays.asList(
        ComicFixtures.COMIC_V1_1,   // read
        ComicFixtures.COMIC_V1_2,   // read
        ComicFixtures.COMIC_V1_3)); // read

    this.progressRepository.saveAll(Arrays.asList(
        ProgressFixtures.comicRead(ComicFixtures.COMIC_V1_1),
        ProgressFixtures.comicRead(ComicFixtures.COMIC_V1_2),
        ProgressFixtures.comicRead(ComicFixtures.COMIC_V1_3)));

    this.mockMvc.perform(get("/api/comics/search/findLastReadForVolume")
        .param("publisher", ComicFixtures.COMIC_V1_1.getPublisher())
        .param("series", ComicFixtures.COMIC_V1_1.getSeries())
        .param("volume", ComicFixtures.COMIC_V1_1.getVolume()))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaTypes.HAL_JSON_VALUE))
        .andExpect(jsonPath("$.title").value(ComicFixtures.COMIC_V1_1.getTitle()));
  }

  @Test
  public void findBookmarksMultipleVolumes() throws Exception {
    this.comicRepository.saveAll(Arrays.asList(
        // Partly read volume at second issue
        ComicFixtures.COMIC_V1_1, // read
        ComicFixtures.COMIC_V1_2,
        ComicFixtures.COMIC_V1_3,

        // Unread volume
        ComicFixtures.COMIC_V2_1,
        ComicFixtures.COMIC_V2_2,
        ComicFixtures.COMIC_V2_3,

        // Partly read volume at third issue
        ComicFixtures.COMIC_V3_1, // read
        ComicFixtures.COMIC_V3_2, // read
        ComicFixtures.COMIC_V3_3));

    this.progressRepository.saveAll(Arrays.asList(
        ProgressFixtures.comicRead(ComicFixtures.COMIC_V1_1, 3),
        ProgressFixtures.comicRead(ComicFixtures.COMIC_V3_1, 1),
        ProgressFixtures.comicRead(ComicFixtures.COMIC_V3_2, 2)));

    this.mockMvc.perform(get("/api/comics/search/findAllLastReadPerVolume"))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaTypes.HAL_JSON_VALUE))
        .andExpect(jsonPath("$._embedded.comics.length()").value(2))
        .andExpect(jsonPath("$._embedded.comics[0].title").value(ComicFixtures.COMIC_V1_2.getTitle()))
        .andExpect(jsonPath("$._embedded.comics[1].title").value(ComicFixtures.COMIC_V3_3.getTitle()));
  }

  @Test
  public void findBookmarksFirstrunnerStarted() throws Exception {
    this.comicRepository.saveAll(Arrays.asList(
        // Partly read volume at first issue
        ComicFixtures.COMIC_V1_1, // started
        ComicFixtures.COMIC_V1_2,
        ComicFixtures.COMIC_V1_3));

    this.progressRepository.save(ProgressFixtures.comicStarted(ComicFixtures.COMIC_V1_1));

    this.mockMvc.perform(get("/api/comics/search/findAllLastReadPerVolume"))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaTypes.HAL_JSON_VALUE))
        .andExpect(jsonPath("$._embedded.comics.length()").value(1))
        .andExpect(jsonPath("$._embedded.comics[0].title").value(ComicFixtures.COMIC_V1_1.getTitle()));
  }

  @Test
  public void findBookmarksAllRead() throws Exception {
    this.comicRepository.saveAll(Arrays.asList(
        // Completely read volume
        ComicFixtures.COMIC_V3_1,   // read
        ComicFixtures.COMIC_V3_2,   // read
        ComicFixtures.COMIC_V3_3)); // read

    this.progressRepository.saveAll(Arrays.asList(
        ProgressFixtures.comicRead(ComicFixtures.COMIC_V3_1, 1),
        ProgressFixtures.comicRead(ComicFixtures.COMIC_V3_2, 2),
        ProgressFixtures.comicRead(ComicFixtures.COMIC_V3_3, 3)));

    this.mockMvc.perform(get("/api/comics/search/findAllLastReadPerVolume"))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaTypes.HAL_JSON_VALUE))
        .andExpect(jsonPath("$._embedded.comics").doesNotExist());
  }

  @Test
  public void findBookmarksNoneRead() throws Exception {
    this.comicRepository.saveAll(Arrays.asList(
        // Completely unread volume
        ComicFixtures.COMIC_V3_1,
        ComicFixtures.COMIC_V3_2,
        ComicFixtures.COMIC_V3_3));

    this.mockMvc.perform(get("/api/comics/search/findAllLastReadPerVolume"))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaTypes.HAL_JSON_VALUE))
        .andExpect(jsonPath("$._embedded.comics").doesNotExist());
  }

  @Test
  public void findBookmarksLastStarted() throws Exception {
    this.comicRepository.saveAll(Arrays.asList(
        // Almost read volume
        ComicFixtures.COMIC_V3_1, // read
        ComicFixtures.COMIC_V3_2, // read
        ComicFixtures.COMIC_V3_3));

    this.progressRepository.saveAll(Arrays.asList(
        ProgressFixtures.comicRead(ComicFixtures.COMIC_V3_1, 1),
        ProgressFixtures.comicRead(ComicFixtures.COMIC_V3_2, 2)));

    this.mockMvc.perform(get("/api/comics/search/findAllLastReadPerVolume"))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaTypes.HAL_JSON_VALUE))
        .andExpect(jsonPath("$._embedded.comics.length()").value(1))
        .andExpect(jsonPath("$._embedded.comics[0].title").value(ComicFixtures.COMIC_V3_3.getTitle()));
  }

  @Test
  public void findBookmarksWithGaps() throws Exception {
    this.comicRepository.saveAll(Arrays.asList(
        // A volume with unread first issue
        ComicFixtures.COMIC_V3_1,
        ComicFixtures.COMIC_V3_2, // read
        ComicFixtures.COMIC_V3_3));

    this.progressRepository.save(ProgressFixtures.comicRead(ComicFixtures.COMIC_V3_2));

    this.mockMvc.perform(get("/api/comics/search/findAllLastReadPerVolume"))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaTypes.HAL_JSON_VALUE))
        .andExpect(jsonPath("$._embedded.comics.length()").value(1))
        .andExpect(jsonPath("$._embedded.comics[0].title").value(ComicFixtures.COMIC_V3_1.getTitle()));
  }

  @Test
  public void updateIncompleteComic() throws Exception {
    // Given
    this.helper.setComicsPath("src/test/resources/fixtures/incomplete", this.testBed);
    final String comicPath = this.testBed.getAbsolutePath() + "/DC Comics/Batman (1940)/Batman 701 (1940).cbz";
    final Comic comic = Comic.builder()
        .path(comicPath)
        .fileName("Batman 701 (1940).cbz")
        .number("")
        .publisher("")
        .series("")
        .volume("")
        .build();

    this.comicRepository.save(comic);

    comic.setPublisher("DC Comics");
    comic.setSeries("Batman");
    comic.setVolume("1940");
    comic.setNumber("701");
    comic.setYear(2010);
    comic.setMonth(10);
    comic.setCurrentPage(5);
    comic.setErrors(Arrays.asList(
        ScannerIssue.builder().severity(ScannerIssue.Severity.ERROR).message("Mock Error").build()));

    // Returns the comic with new values and without errors or progress information
    this.mockMvc.perform(put("/api/comics")
        .contentType(MediaType.APPLICATION_JSON_VALUE)
        .accept(MediaTypes.HAL_JSON_VALUE)
        .content(TestUtil.toJson(comic)))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaTypes.HAL_JSON_VALUE))
        .andExpect(jsonPath("$.series").value("Batman"))
        .andExpect(jsonPath("$.publisher").value("DC Comics"))
        .andExpect(jsonPath("$.volume").value("1940"))
        .andExpect(jsonPath("$.number").value("701"))
        .andExpect(jsonPath("$.position").value("0701.0"))
        .andExpect(jsonPath("$.year").value("2010"))
        .andExpect(jsonPath("$.month").value("10"))
        .andExpect(jsonPath("$.title").value(""))
        .andExpect(jsonPath("$.currentPage").value(0))
        .andExpect(jsonPath("$.errors").doesNotExist());

    // Stores the information
    final Comic persistedComic = this.comicRepository.findById(comic.getId()).get();
    assertThat(persistedComic.getPublisher()).isEqualTo("DC Comics");
    assertThat(persistedComic.getSeries()).isEqualTo("Batman");
    assertThat(persistedComic.getVolume()).isEqualTo("1940");
    assertThat(persistedComic.getNumber()).isEqualTo("701");
    assertThat(persistedComic.getPosition()).isEqualTo("0701.0");
    assertThat(persistedComic.getErrors()).isNull();
    assertThat(persistedComic.getTitle()).isEqualTo("");

    // Writes the XML to the file
    assertThat(TestUtil.zipContainsFile(comicPath, "ComicInfo.xml")).isTrue();
    final Document document = TestUtil.parseComicInfo(comicPath);
    assertThat(TestUtil.getText(document, "Series")).isEqualTo("Batman");
    assertThat(TestUtil.getText(document, "Publisher")).isEqualTo("DC Comics");
    assertThat(TestUtil.getText(document, "Volume")).isEqualTo("1940");
    assertThat(TestUtil.getText(document, "Number")).isEqualTo("701");
    assertThat(TestUtil.getText(document, "Title")).isEqualTo("");
  }

  @Test
  public void updateComic() throws Exception {
    // Given
    this.helper.setComicsPath("src/test/resources/fixtures/simple", this.testBed);
    final String comicPath = this.testBed.getAbsolutePath() + "/Batman 402 (1940).cbz";
    final Comic comic = Comic.builder()
        .path(comicPath)
        .fileName("Batman 402 (1940).cbz")
        .number("402")
        .publisher("DC Comics")
        .series("Batman")
        .volume("1940")
        .year(1986)
        .month(12)
        .build();

    this.comicRepository.save(comic);

    comic.setNumber("502");
    comic.setYear(1993);

    // Returns the comic with new values
    this.mockMvc.perform(put("/api/comics")
        .contentType(MediaType.APPLICATION_JSON_VALUE)
        .accept(MediaTypes.HAL_JSON_VALUE)
        .content(TestUtil.toJson(comic)))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaTypes.HAL_JSON_VALUE))
        .andExpect(jsonPath("$.series").value("Batman"))
        .andExpect(jsonPath("$.publisher").value("DC Comics"))
        .andExpect(jsonPath("$.volume").value("1940"))
        .andExpect(jsonPath("$.number").value("502"))
        .andExpect(jsonPath("$.position").value("0502.0"))
        .andExpect(jsonPath("$.year").value("1993"))
        .andExpect(jsonPath("$.month").value("12"))
        .andExpect(jsonPath("$.title").value(""))
        .andExpect(jsonPath("$.errors").doesNotExist());

    // Stores the information
    final Comic persistedComic = this.comicRepository.findById(comic.getId()).get();
    assertThat(persistedComic.getPublisher()).isEqualTo("DC Comics");
    assertThat(persistedComic.getSeries()).isEqualTo("Batman");
    assertThat(persistedComic.getVolume()).isEqualTo("1940");
    assertThat(persistedComic.getNumber()).isEqualTo("502");
    assertThat(persistedComic.getPosition()).isEqualTo("0502.0");
    assertThat(persistedComic.getTitle()).isEqualTo("");
    assertThat(persistedComic.getErrors()).isNull();

    // Writes the XML to the file
    assertThat(TestUtil.zipContainsFile(comicPath, "ComicInfo.xml")).isTrue();
    final Document document = TestUtil.parseComicInfo(comicPath);
    assertThat(TestUtil.getText(document, "Series")).isEqualTo("Batman");
    assertThat(TestUtil.getText(document, "Publisher")).isEqualTo("DC Comics");
    assertThat(TestUtil.getText(document, "Volume")).isEqualTo("1940");
    assertThat(TestUtil.getText(document, "Number")).isEqualTo("502");
    assertThat(TestUtil.getText(document, "Title")).isEqualTo("");
  }

  @Test
  public void scrape() throws Exception {
    // Given
    this.helper.setComicsPath("src/test/resources/fixtures/incomplete", this.testBed);
    final String comicPath = this.testBed.getAbsolutePath() + "/DC Comics/Batman (1940)/Batman 701 (1940).cbz";
    final Comic comic = Comic.builder()
        .path(comicPath)
        .fileName("Batman 701 (1940).cbz")
        .number("")
        .publisher("")
        .series("")
        .volume("")
        .build();

    this.comicRepository.save(comic);
    final ScannerIssue error = ScannerIssue.builder()
        .severity(ScannerIssue.Severity.ERROR)
        .message("Mock Error")
        .build();

    comic.setPublisher("DC Comics");
    comic.setSeries("Batman");
    comic.setVolume("1940");
    comic.setNumber("701");
    comic.setErrors(Collections.singletonList(error));

    // Returns the comic with scraped values but keeps errors
    this.mockMvc.perform(put("/api/comics/scrape")
        .contentType(MediaType.APPLICATION_JSON_VALUE)
        .accept(MediaTypes.HAL_JSON_VALUE)
        .content(TestUtil.toJson(comic)))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaTypes.HAL_JSON_VALUE))
        .andExpect(jsonPath("$.series").value("Batman"))
        .andExpect(jsonPath("$.publisher").value("DC Comics"))
        .andExpect(jsonPath("$.volume").value("1940"))
        .andExpect(jsonPath("$.number").value("701"))
        .andExpect(jsonPath("$.position").value("0701.0"))
        .andExpect(jsonPath("$.year").value("2010"))
        .andExpect(jsonPath("$.month").value("9"))
        .andExpect(jsonPath("$.title").value("R.I.P. The Missing Chapter, Part 1: The Hole In Things"))
        .andExpect(jsonPath("$.errors.length()").value(1))
        .andExpect(jsonPath("$.errors[0].message").value("Mock Error"));

    // Stores the information
    final Comic persistedComic = this.comicRepository.findById(comic.getId()).get();
    assertThat(persistedComic.getPublisher()).isEqualTo("DC Comics");
    assertThat(persistedComic.getSeries()).isEqualTo("Batman");
    assertThat(persistedComic.getVolume()).isEqualTo("1940");
    assertThat(persistedComic.getNumber()).isEqualTo("701");
    assertThat(persistedComic.getPosition()).isEqualTo("0701.0");
    assertThat(persistedComic.getTitle()).isEqualTo("R.I.P. The Missing Chapter, Part 1: The Hole In Things");
    assertThat(persistedComic.getErrors()).isEqualTo(Collections.singletonList(error));

    // Writes the XML to the file
    assertThat(TestUtil.zipContainsFile(comicPath, "ComicInfo.xml")).isTrue();
    final Document document = TestUtil.parseComicInfo(comicPath);
    assertThat(TestUtil.getText(document, "Series")).isEqualTo("Batman");
    assertThat(TestUtil.getText(document, "Publisher")).isEqualTo("DC Comics");
    assertThat(TestUtil.getText(document, "Volume")).isEqualTo("1940");
    assertThat(TestUtil.getText(document, "Number")).isEqualTo("701");
    assertThat(TestUtil.getText(document, "Title")).isEqualTo("R.I.P. The Missing Chapter, Part 1: The Hole In Things");
  }

  @Test
  public void scrapeWithError() throws Exception {
    // Given
    MockServerUtil.stop();
    this.helper.setComicsPath("src/test/resources/fixtures/incomplete", this.testBed);
    final String comicPath = this.testBed.getAbsolutePath() + "/DC Comics/Batman (1940)/Batman 701 (1940).cbz";
    final Comic comic = Comic.builder()
        .path(comicPath)
        .fileName("Batman 701 (1940).cbz")
        .number("")
        .publisher("")
        .series("")
        .volume("")
        .build();

    this.comicRepository.save(comic);

    comic.setPublisher("DC Comics");
    comic.setSeries("Batman");
    comic.setVolume("1940");
    comic.setNumber("701");
    comic.setErrors(Collections.singletonList(
            ScannerIssue.builder().severity(ScannerIssue.Severity.ERROR).message("Mock Error").build()));

    this.mockMvc.perform(put("/api/comics/scrape")
        .contentType(MediaType.APPLICATION_JSON_VALUE)
        .accept(MediaTypes.HAL_JSON_VALUE)
        .content(TestUtil.toJson(comic)))
        .andExpect(status().is(404));
  }

  @Test
  public void markAsRead() throws Exception {
    // Given
    final Comic comic = this.comicRepository.save(ComicFixtures.COMIC_V1_1);

    // When / Then
    this.mockMvc.perform(put("/api/comics/markAsRead")
        .contentType(MediaType.APPLICATION_JSON_VALUE)
        .accept(MediaTypes.HAL_JSON_VALUE)
        .content(TestUtil.toJson(comic)))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaTypes.HAL_JSON_VALUE))
        .andExpect(jsonPath("$.path").value("/a1.cbz"))
        .andExpect(jsonPath("$.read").value(true));

    final Comic persistedComic = this.comicRepository.findById(comic.getId()).get();
    assertThat(persistedComic.isRead()).isEqualTo(false);
  }

  @Test
  public void markAsUnread() throws Exception {
    // Given
    final Comic comic = this.comicRepository.save(ComicFixtures.COMIC_V1_1);
    this.progressRepository.save(ProgressFixtures.comicRead(ComicFixtures.COMIC_V1_1));

    // When / Then
    this.mockMvc.perform(put("/api/comics/markAsUnread")
        .contentType(MediaType.APPLICATION_JSON_VALUE)
        .accept(MediaTypes.HAL_JSON_VALUE)
        .content(TestUtil.toJson(comic)))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaTypes.HAL_JSON_VALUE))
        .andExpect(jsonPath("$.path").value("/a1.cbz"))
        .andExpect(jsonPath("$.read").value(false));

    final Comic persistedComic = this.comicRepository.findById(comic.getId()).get();
    assertThat(persistedComic.isRead()).isEqualTo(false);
  }

  @Test
  public void deleteComics() throws Exception {
    // Given
    this.comicRepository.saveAll(Arrays.asList(
        ComicFixtures.COMIC_V1_1,
        ComicFixtures.COMIC_V1_2));
    assertThat(this.comicRepository.findAll().size()).isEqualTo(2);

    // When / Then
    this.mockMvc.perform(delete("/api/comics")
        .contentType(MediaType.APPLICATION_JSON_VALUE)
        .accept(MediaTypes.HAL_JSON_VALUE))
        .andExpect(status().isOk());

    assertThat(this.comicRepository.findAll().size()).isEqualTo(0);
  }

  @Test
  public void bundle() throws Exception {
    // Given
    this.comicRepository.saveAll(Arrays.asList(
        ComicFixtures.COMIC_V1_1,
        ComicFixtures.COMIC_V1_2));

    // When
    this.mockMvc.perform(put("/api/comics/bundle")
        .contentType(MediaType.APPLICATION_JSON_VALUE)
        .accept(MediaTypes.HAL_JSON_VALUE))
        .andExpect(status().isOk());

    // Then
    final Comic comic1 = this.comicRepository.findByPath("/a1.cbz").get();
    final Comic comic2 = this.comicRepository.findByPath("/a2.cbz").get();
    assertThat(comic1.getPreviousId()).isEqualTo(null);
    assertThat(comic1.getNextId()).isEqualTo(comic2.getId());
    assertThat(comic2.getPreviousId()).isEqualTo(comic1.getId());
    assertThat(comic2.getNextId()).isEqualTo(null);
  }

  @Test
  public void findAllByOrderByPublisherAscSeriesAscVolumeAscPositionAsc() throws Exception {
    this.comicRepository.saveAll(Arrays.asList(
        ComicFixtures.COMIC_V3_2,
        ComicFixtures.COMIC_V1_1,
        ComicFixtures.COMIC_V3_1,
        ComicFixtures.COMIC_V3_3));

    this.mockMvc.perform(get("/api/comics/search/findAllByOrderByPublisherAscSeriesAscVolumeAscPositionAsc"))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaTypes.HAL_JSON_VALUE))
        .andExpect(jsonPath("$._embedded.comics.length()").value(4))
        .andExpect(jsonPath("$._embedded.comics[0].title").value(ComicFixtures.COMIC_V1_1.getTitle()))
        .andExpect(jsonPath("$._embedded.comics[1].title").value(ComicFixtures.COMIC_V3_1.getTitle()))
        .andExpect(jsonPath("$._embedded.comics[2].title").value(ComicFixtures.COMIC_V3_2.getTitle()))
        .andExpect(jsonPath("$._embedded.comics[3].title").value(ComicFixtures.COMIC_V3_3.getTitle()));
  }

  @Test
  public void findAllByPublisherAndSeriesAndVolumeOrderByPosition() throws Exception {
    this.comicRepository.saveAll(Arrays.asList(
            ComicFixtures.COMIC_V1_1,
            ComicFixtures.COMIC_V1_2,
            ComicFixtures.COMIC_V2_1,
            ComicFixtures.COMIC_V2_2,
            ComicFixtures.COMIC_V3_2,
            ComicFixtures.COMIC_V3_3));

    this.mockMvc.perform(get("/api/comics/search/findAllByPublisherAndSeriesAndVolumeOrderByPosition")
            .param("publisher", ComicFixtures.COMIC_V2_1.getPublisher())
            .param("series", ComicFixtures.COMIC_V2_1.getSeries())
            .param("volume", ComicFixtures.COMIC_V2_1.getVolume()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaTypes.HAL_JSON_VALUE))
            .andExpect(jsonPath("$._embedded.comics.length()").value(2))
            .andExpect(jsonPath("$._embedded.comics[0].title").value(ComicFixtures.COMIC_V2_1.getTitle()))
            .andExpect(jsonPath("$._embedded.comics[1].title").value(ComicFixtures.COMIC_V2_2.getTitle()));
  }
}
