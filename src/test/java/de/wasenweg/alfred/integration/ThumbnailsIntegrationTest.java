package de.wasenweg.alfred.integration;

import de.wasenweg.alfred.AlfredApplication;
import de.wasenweg.alfred.comics.Comic;
import de.wasenweg.alfred.comics.ComicRepository;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.io.TempDir;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.hateoas.MediaTypes;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import reactor.test.StepVerifier;

import java.io.File;
import java.time.Duration;

import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = { AlfredApplication.class }, webEnvironment = WebEnvironment.RANDOM_PORT)
@DirtiesContext
@EnableAutoConfiguration
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@ActiveProfiles("test")
public class ThumbnailsIntegrationTest {

  @TempDir
  public File testBed;

  @LocalServerPort
  private int port;

  private final ComicRepository comicRepository;
  private final WebApplicationContext context;
  private final IntegrationTestHelper helper;

  private MockMvc mockMvc;

  @BeforeEach
  public void setUp() {
    this.mockMvc = MockMvcBuilders
        .webAppContextSetup(this.context)
        .apply(springSecurity())
        .build();

    this.helper.setComicsPath("src/test/resources/fixtures/simple", this.testBed);

    StepVerifier.create(this.helper.triggerScan(this.port))
        .expectNext("start")
        .expectNext("1")
        .expectNext(this.testBed.getAbsolutePath() + "/Batman 402 (1940).cbz")
        .expectNext("cleanUp")
        .expectNext("association")
        .expectNext("done")
        .thenCancel()
        .verify(Duration.ofSeconds(2L));
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
    this.mockMvc.perform(MockMvcRequestBuilders.get("/api/thumbnails/front-cover/" + comic.getId()))
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
    this.mockMvc.perform(MockMvcRequestBuilders.get("/api/thumbnails/back-cover/" + comic.getId()))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaTypes.HAL_JSON_VALUE))
        .andExpect(jsonPath("$._links.self.href").exists())
        .andExpect(jsonPath("$.type").value("BACK_COVER"))
        .andExpect(jsonPath("$.path").value("/3.png"));
  }
}