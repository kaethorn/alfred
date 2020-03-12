package de.wasenweg.alfred.integration;

import de.wasenweg.alfred.EnableEmbeddedMongo;
import de.wasenweg.alfred.comics.ComicRepository;
import de.wasenweg.alfred.fixtures.ComicFixtures;
import de.wasenweg.alfred.fixtures.ProgressFixtures;
import de.wasenweg.alfred.progress.ProgressRepository;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.hateoas.MediaTypes;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@DirtiesContext
@EnableEmbeddedMongo
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@ActiveProfiles("test")
public class ProgressIntegrationTest {

  private final MockMvc mockMvc;
  private final ComicRepository comicRepository;
  private final ProgressRepository progressRepository;

  @AfterEach
  public void tearDown() {
    this.progressRepository.deleteAll();
    this.comicRepository.deleteAll();
  }

  @Test
  public void deleteProgress() throws Exception {
    // Given
    this.comicRepository.saveAll(Arrays.asList(
        ComicFixtures.COMIC_V1_1,
        ComicFixtures.COMIC_V1_2));
    this.progressRepository.saveAll(Arrays.asList(
        ProgressFixtures.comicStarted(ComicFixtures.COMIC_V1_1),
        ProgressFixtures.comicStarted(ComicFixtures.COMIC_V1_2)));

    // When / Then
    this.mockMvc.perform(delete("/api/progress")
        .contentType(MediaType.APPLICATION_JSON_VALUE)
        .accept(MediaTypes.HAL_JSON_VALUE))
        .andExpect(status().isOk());

    assertThat(this.progressRepository.findAll().size()).isEqualTo(0);
  }

  @Test
  public void deleteProgressForCurrentUser() throws Exception {
    // Given
    this.comicRepository.saveAll(Arrays.asList(
        ComicFixtures.COMIC_V1_1,
        ComicFixtures.COMIC_V1_2,
        ComicFixtures.COMIC_V1_3));
    this.progressRepository.saveAll(Arrays.asList(
        ProgressFixtures.comicStarted(ComicFixtures.COMIC_V1_1),
        ProgressFixtures.comicStarted(ComicFixtures.COMIC_V1_2),
        ProgressFixtures.comicStarted(ComicFixtures.COMIC_V1_3, "another-user")));

    // When / Then
    this.mockMvc.perform(delete("/api/progress/me")
        .contentType(MediaType.APPLICATION_JSON_VALUE)
        .accept(MediaTypes.HAL_JSON_VALUE))
        .andExpect(status().isOk());

    assertThat(this.progressRepository.findAll().size()).isEqualTo(1);
    assertThat(this.progressRepository.findAll().get(0).getComicId().toString())
        .isEqualTo(ComicFixtures.COMIC_V1_3.getId());
  }
}