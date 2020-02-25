package de.wasenweg.alfred.integration;

import de.wasenweg.alfred.AlfredApplication;
import de.wasenweg.alfred.comics.Comic;
import de.wasenweg.alfred.comics.ComicRepository;
import de.wasenweg.alfred.scanner.ScannerIssue;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.hateoas.MediaTypes;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.Arrays;

import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = { AlfredApplication.class })
@DirtiesContext
@EnableAutoConfiguration
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@ActiveProfiles("test")
public class QueueIntegrationTest {

  private final ComicRepository comicRepository;
  private final WebApplicationContext context;

  private MockMvc mockMvc;

  @BeforeEach
  public void setUp() {
    this.mockMvc = MockMvcBuilders
        .webAppContextSetup(this.context)
        .apply(springSecurity())
        .build();

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
    this.mockMvc.perform(MockMvcRequestBuilders.get("/api/queue"))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaTypes.HAL_JSON_VALUE))
        .andExpect(jsonPath("$._embedded.comics.length()").value(1));
  }

  @Test
  public void getInvalidOmitsvalid() throws Exception {
    // Given / When / Then
    this.mockMvc.perform(MockMvcRequestBuilders.get("/api/queue"))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaTypes.HAL_JSON_VALUE))
        .andExpect(jsonPath("$._embedded.comics").doesNotExist());
  }

  @Test
  public void getValidReturnsValid() throws Exception {
    // Given / When / Then
    this.mockMvc.perform(MockMvcRequestBuilders.get("/api/queue/valid"))
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
    this.mockMvc.perform(MockMvcRequestBuilders.get("/api/queue/valid"))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaTypes.HAL_JSON_VALUE))
        .andExpect(jsonPath("$._embedded.comics").doesNotExist());
  }
}