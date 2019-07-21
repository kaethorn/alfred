package de.wasenweg.alfred;

import de.wasenweg.alfred.comics.ComicRepository;
import de.wasenweg.alfred.progress.ProgressRepository;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.hateoas.MediaTypes;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.Arrays;

import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = { AlfredApplication.class })
@EnableAutoConfiguration
@ActiveProfiles("test")
public class VolumesIntegrationTest {

  @Autowired
  private ComicRepository comicRepository;

  @Autowired
  private ProgressRepository progressRepository;

  @Autowired
  private WebApplicationContext context;

  private MockMvc mockMvc;

  @Before
  public void setUp() {
    this.mockMvc = MockMvcBuilders
        .webAppContextSetup(this.context)
        .apply(springSecurity())
        .build();

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

  @After
  public void tearDown() {
    this.comicRepository.deleteAll();
    this.progressRepository.deleteAll();
  }

  @Test
  public void findAllPublishers() throws Exception {
    this.mockMvc.perform(MockMvcRequestBuilders.get("/api/publishers"))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaTypes.HAL_JSON_UTF8_VALUE))
        .andExpect(jsonPath("$._embedded.publishers.length()").value(1))
        .andExpect(jsonPath("$._embedded.publishers[0].publisher")
            .value(ComicFixtures.COMIC_V1_1.getPublisher()))
        .andExpect(jsonPath("$._embedded.publishers[0].seriesCount").value(1));
  }

  @Test
  public void findAllSeries() throws Exception {
    this.mockMvc.perform(MockMvcRequestBuilders
        .get("/api/publishers/" + ComicFixtures.COMIC_V1_1.getPublisher() + "/series"))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaTypes.HAL_JSON_UTF8_VALUE))
        .andExpect(jsonPath("$._embedded.series.length()").value(1))
        .andExpect(jsonPath("$._embedded.series[0].series")
            .value(ComicFixtures.COMIC_V1_1.getSeries()))
        .andExpect(jsonPath("$._embedded.series[0].publisher")
            .value(ComicFixtures.COMIC_V1_1.getPublisher()))
        .andExpect(jsonPath("$._embedded.series[0].volumesCount").value(3));
  }

  @Test
  public void findAllVolumes() throws Exception {
    this.mockMvc.perform(MockMvcRequestBuilders
        .get("/api/publishers/"
            + ComicFixtures.COMIC_V1_1.getPublisher() + "/series/"
            + ComicFixtures.COMIC_V1_1.getSeries() + "/volumes"))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaTypes.HAL_JSON_UTF8_VALUE))
        .andExpect(jsonPath("$._embedded.volumes.length()").value(3))

        // Volume 1
        .andExpect(jsonPath("$._embedded.volumes[0].volume")
            .value(ComicFixtures.COMIC_V1_1.getVolume()))
        .andExpect(jsonPath("$._embedded.volumes[0].series")
            .value(ComicFixtures.COMIC_V1_1.getSeries()))
        .andExpect(jsonPath("$._embedded.volumes[0].publisher")
            .value(ComicFixtures.COMIC_V1_1.getPublisher()))
        .andExpect(jsonPath("$._embedded.volumes[0].issueCount").value(3))
        .andExpect(jsonPath("$._embedded.volumes[0].readCount").value(0))
        .andExpect(jsonPath("$._embedded.volumes[0].read").value(false))

        // Volume 2
        .andExpect(jsonPath("$._embedded.volumes[1].volume")
            .value(ComicFixtures.COMIC_V2_1.getVolume()))
        .andExpect(jsonPath("$._embedded.volumes[1].series")
            .value(ComicFixtures.COMIC_V2_1.getSeries()))
        .andExpect(jsonPath("$._embedded.volumes[1].publisher")
            .value(ComicFixtures.COMIC_V2_1.getPublisher()))
        .andExpect(jsonPath("$._embedded.volumes[1].issueCount").value(3))
        .andExpect(jsonPath("$._embedded.volumes[1].readCount").value(0))
        .andExpect(jsonPath("$._embedded.volumes[1].read").value(false))

        // Volume 3
        .andExpect(jsonPath("$._embedded.volumes[2].volume")
            .value(ComicFixtures.COMIC_V3_1.getVolume()))
        .andExpect(jsonPath("$._embedded.volumes[2].series")
            .value(ComicFixtures.COMIC_V3_1.getSeries()))
        .andExpect(jsonPath("$._embedded.volumes[2].publisher")
            .value(ComicFixtures.COMIC_V3_1.getPublisher()))
        .andExpect(jsonPath("$._embedded.volumes[2].issueCount").value(3))
        .andExpect(jsonPath("$._embedded.volumes[2].readCount").value(0))
        .andExpect(jsonPath("$._embedded.volumes[2].read").value(false));
  }
}
