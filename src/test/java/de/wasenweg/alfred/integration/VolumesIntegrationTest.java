package de.wasenweg.alfred.integration;

import de.wasenweg.alfred.EnableEmbeddedMongo;
import de.wasenweg.alfred.TestUtil;
import de.wasenweg.alfred.comics.ComicRepository;
import de.wasenweg.alfred.fixtures.ComicFixtures;
import de.wasenweg.alfred.fixtures.ProgressFixtures;
import de.wasenweg.alfred.fixtures.VolumeFixtures;
import de.wasenweg.alfred.progress.Progress;
import de.wasenweg.alfred.progress.ProgressRepository;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
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
class VolumesIntegrationTest {

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
  void findAllVolumes() throws Exception {
    this.mockMvc.perform(get("/api/volumes")
        .param("publisher", ComicFixtures.COMIC_V1_1.getPublisher())
        .param("series", ComicFixtures.COMIC_V1_1.getSeries()))
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
  void markAsRead() throws Exception {
    assertThat(this.progressRepository.findAll().size()).isEqualTo(0);

    this.mockMvc.perform(put("/api/volumes/markAsRead")
        .contentType(MediaType.APPLICATION_JSON_VALUE)
        .accept(MediaTypes.HAL_JSON_VALUE)
        .content(TestUtil.toJson(VolumeFixtures.VOLUME_1)))
        .andExpect(status().isOk());

    assertThat(this.progressRepository.findAll().size()).isEqualTo(3);
    assertThat(this.progressRepository.findAll().get(0).getComicId().toString()).isEqualTo(ComicFixtures.COMIC_V1_1.getId());
    assertThat(this.progressRepository.findAll().get(0).isRead()).isTrue();
    assertThat(this.progressRepository.findAll().get(1).getComicId().toString()).isEqualTo(ComicFixtures.COMIC_V1_2.getId());
    assertThat(this.progressRepository.findAll().get(1).isRead()).isTrue();
    assertThat(this.progressRepository.findAll().get(2).getComicId().toString()).isEqualTo(ComicFixtures.COMIC_V1_3.getId());
    assertThat(this.progressRepository.findAll().get(2).isRead()).isTrue();
  }

  @Test
  void markAsUnread() throws Exception {
    this.progressRepository.saveAll(Arrays.asList(
        ProgressFixtures.comicRead(ComicFixtures.COMIC_V1_1),
        ProgressFixtures.comicRead(ComicFixtures.COMIC_V1_2),
        ProgressFixtures.comicRead(ComicFixtures.COMIC_V1_3)));
    assertThat(this.progressRepository.findAll().size()).isEqualTo(3);
    assertThat(this.progressRepository.findAll().stream().map(Progress::isRead)).containsOnly(true);

    this.mockMvc.perform(put("/api/volumes/markAsUnread")
        .contentType(MediaType.APPLICATION_JSON_VALUE)
        .accept(MediaTypes.HAL_JSON_VALUE)
        .content(TestUtil.toJson(VolumeFixtures.VOLUME_1)))
        .andExpect(status().isOk());

    assertThat(this.progressRepository.findAll().size()).isEqualTo(3);
    assertThat(this.progressRepository.findAll().stream().map(Progress::isRead)).containsOnly(false);
    assertThat(this.progressRepository.findAll().stream().map(progress -> progress.getComicId().toString()))
        .containsExactly(ComicFixtures.COMIC_V1_1.getId(), ComicFixtures.COMIC_V1_2.getId(), ComicFixtures.COMIC_V1_3.getId());
  }

  @Test
  void markAllAsReadUntil() throws Exception {
    assertThat(this.progressRepository.findAll().size()).isEqualTo(0);

    this.mockMvc.perform(put("/api/volumes/markAllAsReadUntil")
        .contentType(MediaType.APPLICATION_JSON_VALUE)
        .accept(MediaTypes.HAL_JSON_VALUE)
        .content(TestUtil.toJson(ComicFixtures.COMIC_V1_2)))
        .andExpect(status().isOk());

    assertThat(this.progressRepository.findAll().size()).isEqualTo(2);
    assertThat(this.progressRepository.findAll().stream().map(Progress::isRead)).containsOnly(true);
    assertThat(this.progressRepository.findAll().stream().map(progress -> progress.getComicId().toString()))
        .containsExactly(ComicFixtures.COMIC_V1_1.getId(), ComicFixtures.COMIC_V1_2.getId());
  }
}
