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
public class StatsIntegrationTest {

    @Autowired
    private ComicRepository comicRepository;

    @Autowired
    private ProgressRepository progressRepository;

    @Autowired
    private WebApplicationContext context;

    private MockMvc mvc;

    @Before
    public void setUp() {
        mvc = MockMvcBuilders
          .webAppContextSetup(context)
          .apply(springSecurity())
          .build();

        comicRepository.saveAll(Arrays.asList(
                ComicFixtures.COMIC_V1_1,
                ComicFixtures.COMIC_V1_2,
                ComicFixtures.COMIC_V1_3,
                ComicFixtures.COMIC_V2_1,
                ComicFixtures.COMIC_V2_2,
                ComicFixtures.COMIC_V2_3,
                ComicFixtures.COMIC_V3_1,
                ComicFixtures.COMIC_V3_2,
                ComicFixtures.COMIC_V3_3));

        progressRepository.save(ProgressFixtures.comicStarted(ComicFixtures.COMIC_V1_1));
    }

    @After
    public void tearDown() {
        comicRepository.deleteAll();
        progressRepository.deleteAll();
    }

    @Test
    public void getStats() throws Exception {
        mvc.perform(MockMvcRequestBuilders.get("/api/stats"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaTypes.HAL_JSON_UTF8_VALUE))
                .andExpect(jsonPath("$._links.self.href").value("http://localhost/api/stats"))
                .andExpect(jsonPath("$.issues").value(9))
                .andExpect(jsonPath("$.publishers").value(1))
                .andExpect(jsonPath("$.series").value(1))
                .andExpect(jsonPath("$.volumes").value(3))
                .andExpect(jsonPath("$.users").value(1));
    }
}
