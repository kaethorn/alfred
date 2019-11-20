package de.wasenweg.alfred.integration;

import de.wasenweg.alfred.AlfredApplication;
import de.wasenweg.alfred.comics.Comic;
import de.wasenweg.alfred.comics.ComicRepository;
import de.wasenweg.alfred.mockserver.MockServer;
import de.wasenweg.alfred.progress.ProgressRepository;
import de.wasenweg.alfred.scanner.ScannerIssue;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.hateoas.MediaTypes;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.w3c.dom.Document;

import java.io.IOException;
import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = { AlfredApplication.class })
@EnableAutoConfiguration
@ActiveProfiles("test")
public class ComicsIntegrationTest {

  @Autowired
  private ComicRepository comicRepository;

  @Autowired
  private ProgressRepository progressRepository;

  @Autowired
  private WebApplicationContext context;

  @Autowired
  private IntegrationTestHelper helper;

  @Rule
  public TemporaryFolder testBed = new TemporaryFolder();

  private MockMvc mockMvc;

  @BeforeClass
  public static void startServer() throws IOException {
    MockServer.startServer();
  }

  @AfterClass
  public static void stopServer() {
    MockServer.stop();
  }

  @Before
  public void setUp() {
    this.mockMvc = MockMvcBuilders
        .webAppContextSetup(this.context)
        .apply(springSecurity())
        .build();
  }

  @After
  public void tearDown() {
    this.comicRepository.deleteAll();
    this.progressRepository.deleteAll();
  }

  @Test
  public void getAllComics() throws Exception {
    this.comicRepository.saveAll(Arrays.asList(
        ComicFixtures.COMIC_V1_1,
        ComicFixtures.COMIC_V1_2));

    this.mockMvc.perform(MockMvcRequestBuilders.get("/api/comics"))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaTypes.HAL_JSON_UTF8_VALUE))
        .andExpect(jsonPath("$._embedded.comics.length()").value(2))
        .andExpect(jsonPath("$._embedded.comics[0].title").value(ComicFixtures.COMIC_V1_1.getTitle()))
        .andExpect(jsonPath("$._embedded.comics[1].title").value(ComicFixtures.COMIC_V1_2.getTitle()));
  }

  @Test
  public void findLastReadForVolumeWithReadIssue() throws Exception {
    this.comicRepository.saveAll(Arrays.asList(
        ComicFixtures.COMIC_V1_1, // read
        ComicFixtures.COMIC_V1_2,
        ComicFixtures.COMIC_V1_3));

    this.progressRepository.save(ProgressFixtures.comicRead(ComicFixtures.COMIC_V1_1));

    this.mockMvc.perform(MockMvcRequestBuilders.get("/api/comics/search/findLastReadForVolume")
        .param("publisher", ComicFixtures.COMIC_V1_1.getPublisher())
        .param("series", ComicFixtures.COMIC_V1_1.getSeries())
        .param("volume", ComicFixtures.COMIC_V1_1.getVolume()))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaTypes.HAL_JSON_UTF8_VALUE))
        .andExpect(jsonPath("$.title").value(ComicFixtures.COMIC_V1_2.getTitle()));
  }

  @Test
  public void findLastReadForVolumeWithStartedIssue() throws Exception {
    this.comicRepository.saveAll(Arrays.asList(
        ComicFixtures.COMIC_V1_1, // started
        ComicFixtures.COMIC_V1_2,
        ComicFixtures.COMIC_V1_3));

    this.progressRepository.save(ProgressFixtures.comicStarted(ComicFixtures.COMIC_V1_1));

    this.mockMvc.perform(MockMvcRequestBuilders.get("/api/comics/search/findLastReadForVolume")
        .param("publisher", ComicFixtures.COMIC_V1_1.getPublisher())
        .param("series", ComicFixtures.COMIC_V1_1.getSeries())
        .param("volume", ComicFixtures.COMIC_V1_1.getVolume()))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaTypes.HAL_JSON_UTF8_VALUE))
        .andExpect(jsonPath("$.title").value(ComicFixtures.COMIC_V1_1.getTitle()));
  }

  @Test
  public void findLastReadForUnstartedVolume() throws Exception {
    this.comicRepository.saveAll(Arrays.asList(
        ComicFixtures.COMIC_V1_1,
        ComicFixtures.COMIC_V1_2,
        ComicFixtures.COMIC_V1_3));

    this.mockMvc.perform(MockMvcRequestBuilders.get("/api/comics/search/findLastReadForVolume")
        .param("publisher", ComicFixtures.COMIC_V1_1.getPublisher())
        .param("series", ComicFixtures.COMIC_V1_1.getSeries())
        .param("volume", ComicFixtures.COMIC_V1_1.getVolume()))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaTypes.HAL_JSON_UTF8_VALUE))
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

    this.mockMvc.perform(MockMvcRequestBuilders.get("/api/comics/search/findLastReadForVolume")
        .param("publisher", ComicFixtures.COMIC_V1_1.getPublisher())
        .param("series", ComicFixtures.COMIC_V1_1.getSeries())
        .param("volume", ComicFixtures.COMIC_V1_1.getVolume()))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaTypes.HAL_JSON_UTF8_VALUE))
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

    this.mockMvc.perform(MockMvcRequestBuilders.get("/api/comics/search/findLastReadForVolume")
        .param("publisher", ComicFixtures.COMIC_V1_1.getPublisher())
        .param("series", ComicFixtures.COMIC_V1_1.getSeries())
        .param("volume", ComicFixtures.COMIC_V1_1.getVolume()))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaTypes.HAL_JSON_UTF8_VALUE))
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

    this.mockMvc.perform(MockMvcRequestBuilders.get("/api/comics/search/findAllLastReadPerVolume"))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaTypes.HAL_JSON_UTF8_VALUE))
        .andExpect(jsonPath("$._embedded.comics.length()").value(2))
        .andExpect(jsonPath("$._embedded.comics[0].title").value(ComicFixtures.COMIC_V1_2.getTitle()))
        .andExpect(jsonPath("$._embedded.comics[1].title").value(ComicFixtures.COMIC_V3_3.getTitle()));
  }

  @Test
  public void findBookmarksFirstStarted() throws Exception {
    this.comicRepository.saveAll(Arrays.asList(
        // Partly read volume at first issue
        ComicFixtures.COMIC_V1_1, // started
        ComicFixtures.COMIC_V1_2,
        ComicFixtures.COMIC_V1_3));

    this.progressRepository.save(ProgressFixtures.comicStarted(ComicFixtures.COMIC_V1_1));

    this.mockMvc.perform(MockMvcRequestBuilders.get("/api/comics/search/findAllLastReadPerVolume"))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaTypes.HAL_JSON_UTF8_VALUE))
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

    this.mockMvc.perform(MockMvcRequestBuilders.get("/api/comics/search/findAllLastReadPerVolume"))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaTypes.HAL_JSON_UTF8_VALUE))
        .andExpect(jsonPath("$._embedded.comics").doesNotExist());
  }

  @Test
  public void findBookmarksNoneRead() throws Exception {
    this.comicRepository.saveAll(Arrays.asList(
        // Completely unread volume
        ComicFixtures.COMIC_V3_1,
        ComicFixtures.COMIC_V3_2,
        ComicFixtures.COMIC_V3_3));

    this.mockMvc.perform(MockMvcRequestBuilders.get("/api/comics/search/findAllLastReadPerVolume"))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaTypes.HAL_JSON_UTF8_VALUE))
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

    this.mockMvc.perform(MockMvcRequestBuilders.get("/api/comics/search/findAllLastReadPerVolume"))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaTypes.HAL_JSON_UTF8_VALUE))
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

    this.mockMvc.perform(MockMvcRequestBuilders.get("/api/comics/search/findAllLastReadPerVolume"))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaTypes.HAL_JSON_UTF8_VALUE))
        .andExpect(jsonPath("$._embedded.comics.length()").value(1))
        .andExpect(jsonPath("$._embedded.comics[0].title").value(ComicFixtures.COMIC_V3_1.getTitle()));
  }

  @Test
  @DirtiesContext
  public void updateIncompleteComic() throws Exception {
    // Given
    this.helper.setComicsPath("src/test/resources/fixtures/incomplete", this.testBed);
    final String comicPath = this.testBed.getRoot().getAbsolutePath() + "/DC Comics/Batman (1940)/Batman 701 (1940).cbz";
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
    comic.setYear(Short.parseShort("2010"));
    comic.setMonth(Short.parseShort("10"));
    comic.setErrors(Arrays.asList(
        ScannerIssue.builder().type(ScannerIssue.Type.ERROR).message("Mock Error").build()));

    // Returns the comic with new values and without errors
    this.mockMvc.perform(MockMvcRequestBuilders.put("/api/comics")
        .contentType(MediaType.APPLICATION_JSON_VALUE)
        .accept(MediaTypes.HAL_JSON_UTF8_VALUE)
        .content(this.helper.comicToJson(comic)))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaTypes.HAL_JSON_UTF8_VALUE))
        .andExpect(jsonPath("$.series").value("Batman"))
        .andExpect(jsonPath("$.publisher").value("DC Comics"))
        .andExpect(jsonPath("$.volume").value("1940"))
        .andExpect(jsonPath("$.number").value("701"))
        .andExpect(jsonPath("$.position").value("0701.0"))
        .andExpect(jsonPath("$.year").value("2010"))
        .andExpect(jsonPath("$.month").value("10"))
        .andExpect(jsonPath("$.title").value(""))
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
    assertThat(this.helper.zipContainsFile(comicPath, "ComicInfo.xml")).isTrue();
    final Document document = this.helper.parseComicInfo(comicPath);
    assertThat(this.helper.getText(document, "Series")).isEqualTo("Batman");
    assertThat(this.helper.getText(document, "Publisher")).isEqualTo("DC Comics");
    assertThat(this.helper.getText(document, "Volume")).isEqualTo("1940");
    assertThat(this.helper.getText(document, "Number")).isEqualTo("701");
    assertThat(this.helper.getText(document, "Title")).isEqualTo("");
  }

  @Test
  @DirtiesContext
  public void updateComic() throws Exception {
    // Given
    this.helper.setComicsPath("src/test/resources/fixtures/simple", this.testBed);
    final String comicPath = this.testBed.getRoot().getAbsolutePath() + "/Batman 402 (1940).cbz";
    final Comic comic = Comic.builder()
        .path(comicPath)
        .fileName("Batman 402 (1940).cbz")
        .number("402")
        .publisher("DC Comics")
        .series("Batman")
        .volume("1940")
        .year(Short.parseShort("1986"))
        .month(Short.parseShort("12"))
        .build();

    this.comicRepository.save(comic);

    comic.setNumber("502");
    comic.setYear(Short.parseShort("1993"));

    // Returns the comic with new values
    this.mockMvc.perform(MockMvcRequestBuilders.put("/api/comics")
        .contentType(MediaType.APPLICATION_JSON_VALUE)
        .accept(MediaTypes.HAL_JSON_UTF8_VALUE)
        .content(this.helper.comicToJson(comic)))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaTypes.HAL_JSON_UTF8_VALUE))
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
    assertThat(this.helper.zipContainsFile(comicPath, "ComicInfo.xml")).isTrue();
    final Document document = this.helper.parseComicInfo(comicPath);
    assertThat(this.helper.getText(document, "Series")).isEqualTo("Batman");
    assertThat(this.helper.getText(document, "Publisher")).isEqualTo("DC Comics");
    assertThat(this.helper.getText(document, "Volume")).isEqualTo("1940");
    assertThat(this.helper.getText(document, "Number")).isEqualTo("502");
    assertThat(this.helper.getText(document, "Title")).isEqualTo("");
  }

  @Test
  @DirtiesContext
  public void scrape() throws Exception {
    // Given
    this.helper.setComicsPath("src/test/resources/fixtures/incomplete", this.testBed);
    final String comicPath = this.testBed.getRoot().getAbsolutePath() + "/DC Comics/Batman (1940)/Batman 701 (1940).cbz";
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
        .type(ScannerIssue.Type.ERROR)
        .message("Mock Error")
        .build();

    comic.setPublisher("DC Comics");
    comic.setSeries("Batman");
    comic.setVolume("1940");
    comic.setNumber("701");
    comic.setErrors(Arrays.asList(error));

    // Returns the comic with scraped values but keeps errors
    this.mockMvc.perform(MockMvcRequestBuilders.put("/api/comics/scrape")
        .contentType(MediaType.APPLICATION_JSON_VALUE)
        .accept(MediaTypes.HAL_JSON_UTF8_VALUE)
        .content(this.helper.comicToJson(comic)))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaTypes.HAL_JSON_UTF8_VALUE))
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
    assertThat(persistedComic.getErrors()).isEqualTo(Arrays.asList(error));

    // Writes the XML to the file
    assertThat(this.helper.zipContainsFile(comicPath, "ComicInfo.xml")).isTrue();
    final Document document = this.helper.parseComicInfo(comicPath);
    assertThat(this.helper.getText(document, "Series")).isEqualTo("Batman");
    assertThat(this.helper.getText(document, "Publisher")).isEqualTo("DC Comics");
    assertThat(this.helper.getText(document, "Volume")).isEqualTo("1940");
    assertThat(this.helper.getText(document, "Number")).isEqualTo("701");
    assertThat(this.helper.getText(document, "Title")).isEqualTo("R.I.P. The Missing Chapter, Part 1: The Hole In Things");
  }

  @Test
  @DirtiesContext
  public void scrapeWithError() throws Exception {
    // Given
    MockServer.stop();
    this.helper.setComicsPath("src/test/resources/fixtures/incomplete", this.testBed);
    final String comicPath = this.testBed.getRoot().getAbsolutePath() + "/DC Comics/Batman (1940)/Batman 701 (1940).cbz";
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
    comic.setErrors(Arrays.asList(
        ScannerIssue.builder().type(ScannerIssue.Type.ERROR).message("Mock Error").build()));

    this.mockMvc.perform(MockMvcRequestBuilders.put("/api/comics/scrape")
        .contentType(MediaType.APPLICATION_JSON_VALUE)
        .accept(MediaTypes.HAL_JSON_UTF8_VALUE)
        .content(this.helper.comicToJson(comic)))
        .andExpect(status().is(404));
  }
}
