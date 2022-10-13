package de.wasenweg.alfred.integration;

import de.wasenweg.alfred.EnableEmbeddedMongo;
import de.wasenweg.alfred.comics.Comic;
import de.wasenweg.alfred.comics.ComicRepository;
import de.wasenweg.alfred.fixtures.ComicFixtures;
import de.wasenweg.alfred.scanner.ScannerIssue;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.hateoas.MediaTypes;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@DirtiesContext
@EnableEmbeddedMongo
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@ActiveProfiles("test")
class PublishersIntegrationTest {

  private final MockMvc mockMvc;
  private final ComicRepository comicRepository;

  @BeforeEach
  public void setUp() {
    this.comicRepository.saveAll(Arrays.asList(
        ComicFixtures.COMIC_V1_1,
        ComicFixtures.COMIC_V1_2,
        ComicFixtures.COMIC_V1_3,
        ComicFixtures.COMIC_V2_1,
        ComicFixtures.COMIC_V2_2,
        ComicFixtures.COMIC_V2_3,
        ComicFixtures.COMIC_V3_1,
        ComicFixtures.COMIC_V3_2,
        ComicFixtures.COMIC_V3_3));
  }

  @AfterEach
  public void tearDown() {
    this.comicRepository.deleteAll();
  }

  @Test
  void findAllPublishers() throws Exception {
    this.mockMvc.perform(get("/api/publishers"))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaTypes.HAL_JSON_VALUE))
        .andExpect(jsonPath("$._embedded.publishers.length()").value(1))
        .andExpect(jsonPath("$._embedded.publishers[0].name")
            .value(ComicFixtures.COMIC_V1_1.getPublisher()))
        .andExpect(jsonPath("$._embedded.publishers[0].seriesCount").value(1))
        .andExpect(jsonPath("$._embedded.publishers[0].series.length()").value(1))
        .andExpect(jsonPath("$._embedded.publishers[0].series[0].volumesCount").value(3));
  }

  @Test
  void findAllPublishersWithMoreThanOne() throws Exception {
    // When there are two publishers, each of which have the exact same
    // series name and volume name.
    final Comic pivotal = this.comicRepository.findByPath("/a1.cbz").get();
    pivotal.setPublisher("Publisher B");
    this.comicRepository.save(pivotal);

    this.mockMvc.perform(get("/api/publishers"))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaTypes.HAL_JSON_VALUE))
        .andExpect(jsonPath("$._embedded.publishers.length()").value(2))
        .andExpect(jsonPath("$._embedded.publishers[0].name")
            .value(ComicFixtures.COMIC_V1_1.getPublisher()))
        .andExpect(jsonPath("$._embedded.publishers[0].seriesCount").value(1))
        .andExpect(jsonPath("$._embedded.publishers[0].series.length()").value(1))
        .andExpect(jsonPath("$._embedded.publishers[0].series[0].name")
            .value(ComicFixtures.COMIC_V1_1.getSeries()))
        .andExpect(jsonPath("$._embedded.publishers[0].series[0].volumesCount").value(3))
        .andExpect(jsonPath("$._embedded.publishers[1].name")
            .value("Publisher B"))
        .andExpect(jsonPath("$._embedded.publishers[1].seriesCount").value(1))
        .andExpect(jsonPath("$._embedded.publishers[1].series.length()").value(1))
        .andExpect(jsonPath("$._embedded.publishers[1].series[0].volumesCount").value(1))
        .andExpect(jsonPath("$._embedded.publishers[1].series[0].name")
            .value(ComicFixtures.COMIC_V1_1.getSeries()));
  }

  @Test
  void findAllPublishersWithErrors() throws Exception {
    final Comic pivotal = this.comicRepository.findByPath("/a1.cbz").get();
    pivotal.setPublisher("Pub B");
    pivotal.setErrors(Arrays.asList(
        ScannerIssue.builder().severity(ScannerIssue.Severity.ERROR).message("Mock Error").build()));
    this.comicRepository.save(pivotal);

    this.mockMvc.perform(get("/api/publishers"))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaTypes.HAL_JSON_VALUE))
        .andExpect(jsonPath("$._embedded.publishers.length()").value(1))
        .andExpect(jsonPath("$._embedded.publishers[0].name")
            .value(ComicFixtures.COMIC_V1_1.getPublisher()))
        .andExpect(jsonPath("$._embedded.publishers[0].seriesCount").value(1))
        .andExpect(jsonPath("$._embedded.publishers[0].series.length()").value(1))
        .andExpect(jsonPath("$._embedded.publishers[0].series[0].volumesCount").value(3));
  }
}
