package de.wasenweg.alfred.integration;

import de.wasenweg.alfred.EnableEmbeddedMongo;
import de.wasenweg.alfred.comics.Comic;
import de.wasenweg.alfred.comics.ComicRepository;
import de.wasenweg.alfred.fixtures.ComicFixtures;
import de.wasenweg.alfred.progress.ProgressRepository;
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
public class VolumesIntegrationTest {

  private final MockMvc mockMvc;
  private final ComicRepository comicRepository;
  private final ProgressRepository progressRepository;

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
    this.progressRepository.deleteAll();
  }

  @Test
  public void findAllPublishers() throws Exception {
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
  public void findAllSeries() throws Exception {
    this.mockMvc.perform(get("/api/publishers/" + ComicFixtures.COMIC_V1_1.getPublisher() + "/series"))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaTypes.HAL_JSON_VALUE))
        .andExpect(jsonPath("$._embedded.series.length()").value(1))
        .andExpect(jsonPath("$._embedded.series[0].name")
            .value(ComicFixtures.COMIC_V1_1.getSeries()))
        .andExpect(jsonPath("$._embedded.series[0].publisher")
            .value(ComicFixtures.COMIC_V1_1.getPublisher()))
        .andExpect(jsonPath("$._embedded.series[0].volumesCount").value(3));
  }

  @Test
  public void findAllVolumes() throws Exception {
    this.mockMvc.perform(get("/api/publishers/"
            + ComicFixtures.COMIC_V1_1.getPublisher() + "/series/"
            + ComicFixtures.COMIC_V1_1.getSeries() + "/volumes"))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaTypes.HAL_JSON_VALUE))
        .andExpect(jsonPath("$._embedded.volumes.length()").value(3))

        // Volume 1
        .andExpect(jsonPath("$._embedded.volumes[0].name")
            .value(ComicFixtures.COMIC_V1_1.getVolume()))
        .andExpect(jsonPath("$._embedded.volumes[0].series")
            .value(ComicFixtures.COMIC_V1_1.getSeries()))
        .andExpect(jsonPath("$._embedded.volumes[0].publisher")
            .value(ComicFixtures.COMIC_V1_1.getPublisher()))
        .andExpect(jsonPath("$._embedded.volumes[0].issueCount").value(3))
        .andExpect(jsonPath("$._embedded.volumes[0].readCount").value(0))
        .andExpect(jsonPath("$._embedded.volumes[0].read").value(false))

        // Volume 2
        .andExpect(jsonPath("$._embedded.volumes[1].name")
            .value(ComicFixtures.COMIC_V2_1.getVolume()))
        .andExpect(jsonPath("$._embedded.volumes[1].series")
            .value(ComicFixtures.COMIC_V2_1.getSeries()))
        .andExpect(jsonPath("$._embedded.volumes[1].publisher")
            .value(ComicFixtures.COMIC_V2_1.getPublisher()))
        .andExpect(jsonPath("$._embedded.volumes[1].issueCount").value(3))
        .andExpect(jsonPath("$._embedded.volumes[1].readCount").value(0))
        .andExpect(jsonPath("$._embedded.volumes[1].read").value(false))

        // Volume 3
        .andExpect(jsonPath("$._embedded.volumes[2].name")
            .value(ComicFixtures.COMIC_V3_1.getVolume()))
        .andExpect(jsonPath("$._embedded.volumes[2].series")
            .value(ComicFixtures.COMIC_V3_1.getSeries()))
        .andExpect(jsonPath("$._embedded.volumes[2].publisher")
            .value(ComicFixtures.COMIC_V3_1.getPublisher()))
        .andExpect(jsonPath("$._embedded.volumes[2].issueCount").value(3))
        .andExpect(jsonPath("$._embedded.volumes[2].readCount").value(0))
        .andExpect(jsonPath("$._embedded.volumes[2].read").value(false));
  }

  @Test
  public void findAllPublishersWithMoreThanOne() throws Exception {
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
  public void findAllPublishersWithErrors() throws Exception {
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
