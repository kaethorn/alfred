package de.wasenweg.alfred.integration;

import de.wasenweg.alfred.EnableEmbeddedMongo;
import de.wasenweg.alfred.comics.ComicRepository;
import de.wasenweg.alfred.fixtures.ComicFixtures;
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
public class SeriesIntegrationTest {

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
  public void findAllSeries() throws Exception {
    this.mockMvc.perform(get("/api/series")
        .param("publisher", ComicFixtures.COMIC_V1_1.getPublisher()))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaTypes.HAL_JSON_VALUE))
        .andExpect(jsonPath("$._embedded.series.length()").value(1))
        .andExpect(jsonPath("$._embedded.series[0].name")
            .value(ComicFixtures.COMIC_V1_1.getSeries()))
        .andExpect(jsonPath("$._embedded.series[0].publisher")
            .value(ComicFixtures.COMIC_V1_1.getPublisher()))
        .andExpect(jsonPath("$._embedded.series[0].volumesCount").value(3));
  }
}
