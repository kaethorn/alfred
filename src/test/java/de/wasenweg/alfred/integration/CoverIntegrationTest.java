package de.wasenweg.alfred.integration;

import de.wasenweg.alfred.EnableEmbeddedMongo;
import de.wasenweg.alfred.TestUtil;
import de.wasenweg.alfred.comics.Comic;
import de.wasenweg.alfred.comics.ComicRepository;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.hateoas.MediaTypes;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import reactor.test.StepVerifier;

import java.io.File;
import java.time.Duration;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertIterableEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@DirtiesContext
@EnableEmbeddedMongo
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@ActiveProfiles("test")
class CoverIntegrationTest {

  @TempDir
  public File testBed;

  @LocalServerPort
  private int port;

  private final MockMvc mockMvc;
  private final ComicRepository comicRepository;
  private final IntegrationTestHelper helper;

  @BeforeEach
  void setUp() {
    this.helper.setComicsPath("src/test/resources/fixtures/simple", this.testBed);

    StepVerifier.create(TestUtil.triggerScan(this.port))
        .expectNext("start")
        .expectNext("1")
        .expectNext(this.testBed.getAbsolutePath() + "/Batman 402 (1940).cbz")
        .expectNext("cleanUp")
        .expectNext("association")
        .expectNext("done")
        .thenCancel()
        .verify(Duration.ofSeconds(9L));
  }

  @AfterEach
  public void tearDown() {
    this.comicRepository.deleteAll();
  }

  @Test
  void deletesFirstPage() throws Exception {
    // Given
    Comic comic = this.comicRepository.findAll().get(0);
    assertEquals(comic.getPageCount(), 3);
    assertIterableEquals(
        comic.getFiles(), Arrays.asList("/1.png", "/2.png", "/3.png", "/ComicInfo.xml"));

    // When
    this.mockMvc.perform(delete("/api/comics/" + comic.getId() + "/page")
        .param("path", "/1.png"))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaTypes.HAL_JSON_VALUE))
        .andExpect(jsonPath("$.files.length()").value("3"))
        .andExpect(jsonPath("$.files[0]").value("/2.png"))
        .andExpect(jsonPath("$.files[1]").value("/3.png"))
        .andExpect(jsonPath("$.files[2]").value("/ComicInfo.xml"));

    // Then
    comic = this.comicRepository.findAll().get(0);
    assertEquals(comic.getPageCount(), 2);
    assertIterableEquals(
        comic.getFiles(), Arrays.asList("/2.png", "/3.png", "/ComicInfo.xml"));
  }

  @Test
  void deletesLastPage() throws Exception {
    // Given
    Comic comic = this.comicRepository.findAll().get(0);
    assertEquals(comic.getPageCount(), 3);
    assertIterableEquals(
        comic.getFiles(), Arrays.asList("/1.png", "/2.png", "/3.png", "/ComicInfo.xml"));

    // When
    this.mockMvc.perform(delete("/api/comics/" + comic.getId() + "/page")
        .param("path", "/3.png"))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaTypes.HAL_JSON_VALUE))
        .andExpect(jsonPath("$.files.length()").value("3"))
        .andExpect(jsonPath("$.files[0]").value("/1.png"))
        .andExpect(jsonPath("$.files[1]").value("/2.png"))
        .andExpect(jsonPath("$.files[2]").value("/ComicInfo.xml"));

    // Then
    comic = this.comicRepository.findAll().get(0);
    assertEquals(comic.getPageCount(), 2);
    assertIterableEquals(
        comic.getFiles(), Arrays.asList("/1.png", "/2.png", "/ComicInfo.xml"));
  }
}
