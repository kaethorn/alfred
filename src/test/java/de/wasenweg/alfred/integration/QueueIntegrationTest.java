package de.wasenweg.alfred.integration;

import de.wasenweg.alfred.EnableEmbeddedMongo;
import de.wasenweg.alfred.comics.Comic;
import de.wasenweg.alfred.comics.ComicRepository;
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
public class QueueIntegrationTest {

  private final MockMvc mockMvc;
  private final ComicRepository comicRepository;

  @BeforeEach
  public void setUp() {
    final Comic comic = Comic.builder()
        .path("/none.cbz")
        .fileName("Batman 701 (1940).cbz")
        .number("701")
        .publisher("DC Comics")
        .series("Batman")
        .volume("1940")
        .build();

    this.comicRepository.save(comic);
  }

  @AfterEach
  public void tearDown() {
    this.comicRepository.deleteAll();
  }

  @Test
  public void getInvalidReturnsInvalid() throws Exception {
    // Given
    final Comic comic = this.comicRepository.findAll().get(0);
    comic.setErrors(Arrays.asList(
        ScannerIssue.builder().severity(ScannerIssue.Severity.ERROR).message("Mock Error").build()));
    this.comicRepository.save(comic);

    // When / Then
    this.mockMvc.perform(get("/api/queue"))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaTypes.HAL_JSON_VALUE))
        .andExpect(jsonPath("$._embedded.comics.length()").value(1));
  }

  @Test
  public void getInvalidOmitsValid() throws Exception {
    // Given / When / Then
    this.mockMvc.perform(get("/api/queue"))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaTypes.HAL_JSON_VALUE))
        .andExpect(jsonPath("$._embedded.comics").doesNotExist());
  }

  @Test
  public void getValidReturnsValid() throws Exception {
    // Given / When / Then
    this.mockMvc.perform(get("/api/queue/valid"))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaTypes.HAL_JSON_VALUE))
        .andExpect(jsonPath("$._embedded.comics.length()").value(1));
  }

  @Test
  public void getValidOmitsInvalid() throws Exception {
    // Given
    final Comic comic = this.comicRepository.findAll().get(0);
    comic.setErrors(Arrays.asList(
        ScannerIssue.builder().severity(ScannerIssue.Severity.ERROR).message("Mock Error").build()));
    this.comicRepository.save(comic);

    // When / Then
    this.mockMvc.perform(get("/api/queue/valid"))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaTypes.HAL_JSON_VALUE))
        .andExpect(jsonPath("$._embedded.comics").doesNotExist());
  }
}