package de.wasenweg.komix;

import de.wasenweg.komix.comics.ComicRepository;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.hateoas.MediaTypes;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.authentication;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = { KomixApplication.class }, webEnvironment = WebEnvironment.RANDOM_PORT)
@EnableAutoConfiguration
public class VolumesIntegrationTest {

    @LocalServerPort
    private int port;

    @Autowired
    private ComicRepository comicRepository;

    @Autowired
    private WebApplicationContext context;

    private MockMvc mvc;

    @Before
    public void setup() {
        mvc = MockMvcBuilders
          .webAppContextSetup(context)
          .apply(springSecurity())
          .build();
    }

    @After
    public void tearDown() {
        comicRepository.deleteAll();
    }

    @Test
    public void findVolumesBySeriesAndPublishers() throws Exception {
        comicRepository.save(ComicFixtures.COMIC_V1_1);
        comicRepository.save(ComicFixtures.COMIC_V1_2);
        comicRepository.save(ComicFixtures.COMIC_V1_3);
        comicRepository.save(ComicFixtures.COMIC_V2_1);
        comicRepository.save(ComicFixtures.COMIC_V2_2);
        comicRepository.save(ComicFixtures.COMIC_V2_3);
        comicRepository.save(ComicFixtures.COMIC_V3_1);
        comicRepository.save(ComicFixtures.COMIC_V3_2);
        comicRepository.save(ComicFixtures.COMIC_V3_3);

        mvc.perform(MockMvcRequestBuilders.get("/api/publishers")
                .with(authentication(OAuth2MockHelper.getOauthTestAuthentication()))
                .sessionAttr("scopedTarget.oauth2ClientContext", OAuth2MockHelper.getOauth2ClientContext()))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaTypes.HAL_JSON_UTF8_VALUE))
                .andExpect(jsonPath("$._embedded.publishers.length()").value(1))
                .andExpect(jsonPath("$._embedded.publishers[0].series.length()").value(1))
                .andExpect(jsonPath("$._embedded.publishers[0].series[0].volumes.length()").value(3))
                .andExpect(jsonPath("$._embedded.publishers[0].series[0].volumes[0].volume").value("1999"))
                .andExpect(jsonPath("$._embedded.publishers[0].series[0].volumes[1].volume").value("2005"))
                .andExpect(jsonPath("$._embedded.publishers[0].series[0].volumes[2].volume").value("2011"));
    }
}
