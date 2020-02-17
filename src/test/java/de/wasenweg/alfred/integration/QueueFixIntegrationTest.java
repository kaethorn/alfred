package de.wasenweg.alfred.integration;

import de.wasenweg.alfred.AlfredApplication;
import de.wasenweg.alfred.comics.Comic;
import de.wasenweg.alfred.comics.ComicRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.io.TempDir;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.hateoas.MediaTypes;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.io.File;

import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = { AlfredApplication.class })
@EnableAutoConfiguration
@ActiveProfiles("test")
public class QueueFixIntegrationTest {

  @TempDir
  public File testBed;

  @Autowired
  private ComicRepository comicRepository;

  @Autowired
  private WebApplicationContext context;

  @Autowired
  private IntegrationTestHelper helper;

  private MockMvc mockMvc;

  @BeforeEach
  public void setUp() {
    this.mockMvc = MockMvcBuilders
        .webAppContextSetup(this.context)
        .apply(springSecurity())
        .build();
  }

  @AfterEach
  public void tearDown() {
    this.comicRepository.deleteAll();
  }

  @Test
  @DirtiesContext
  public void flatten() throws Exception {
    // Given
    this.helper.setComicsPath("src/test/resources/fixtures/not_flat", this.testBed);
    final String comicPath = this.testBed.getAbsolutePath() + "/Batman 402 (1940).cbz";
    Comic comic = Comic.builder()
        .path(comicPath)
        .fileName("Batman 402 (1940).cbz")
        .number("402")
        .publisher("DC Comics")
        .series("Batman")
        .volume("1940")
        .build();

    comic = this.comicRepository.save(comic);

    // When / Then
    this.mockMvc.perform(MockMvcRequestBuilders.put("/api/queue/fix/NOT_FLAT")
        .contentType(MediaType.APPLICATION_JSON_VALUE)
        .accept(MediaTypes.HAL_JSON_VALUE)
        .content(this.helper.comicToJson(comic)))
        .andDo(print())
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaTypes.HAL_JSON_VALUE))
        .andExpect(jsonPath("$.number").value("402"))
        .andExpect(jsonPath("$.files.length()").value(5))
        .andExpect(jsonPath("$.files[0]").value("/1.png"))
        .andExpect(jsonPath("$.files[1]").value("/2.png"))
        .andExpect(jsonPath("$.files[2]").value("/3.png"))
        .andExpect(jsonPath("$.files[3]").value("/4.png"))
        .andExpect(jsonPath("$.files[4]").value("/ComicInfo.xml"));
  }
}