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

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@DirtiesContext
@EnableEmbeddedMongo
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@ActiveProfiles("test")
public class ThumbnailsIntegrationTest {

  @TempDir
  public File testBed;

  @LocalServerPort
  private int port;

  private final MockMvc mockMvc;
  private final ComicRepository comicRepository;
  private final IntegrationTestHelper helper;

  @BeforeEach
  public void setUp() {
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
  public void savesFrontCoverThumbnail() throws Exception {
    // Given
    final Comic comic = this.comicRepository.findAll().get(0);

    // When / Then
    this.mockMvc.perform(get("/api/thumbnails/front-cover/" + comic.getId()))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaTypes.HAL_JSON_VALUE))
        .andExpect(jsonPath("$._links.self.href").exists())
        .andExpect(jsonPath("$.type").value("FRONT_COVER"))
        .andExpect(jsonPath("$.path").value("/1.png"));
  }

  @Test
  public void savesBackCoverThumbnail() throws Exception {
    // Given
    final Comic comic = this.comicRepository.findAll().get(0);

    // When / Then
    this.mockMvc.perform(get("/api/thumbnails/back-cover/" + comic.getId()))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaTypes.HAL_JSON_VALUE))
        .andExpect(jsonPath("$._links.self.href").exists())
        .andExpect(jsonPath("$.type").value("BACK_COVER"))
        .andExpect(jsonPath("$.path").value("/3.png"));
  }
}