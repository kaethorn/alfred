package de.wasenweg.comix;

import de.wasenweg.komix.KomixApplication;
import de.wasenweg.komix.comics.ComicRepository;
import de.wasenweg.komix.publisher.Publisher;

import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.hateoas.MediaTypes;
import org.springframework.hateoas.Resources;
import org.springframework.hateoas.client.Traverson;
import org.springframework.test.context.junit4.SpringRunner;

import java.net.URI;
import java.util.List;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = KomixApplication.class, webEnvironment = WebEnvironment.RANDOM_PORT)
@EnableAutoConfiguration
public class VolumesIntegrationTest {

    @LocalServerPort
    private int port;

    @Autowired
    private ComicRepository comicRepository;

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

        final Traverson traverson = new Traverson(new URI("http://localhost:" + port + "/api/"), MediaTypes.HAL_JSON);

        final List<Publisher> publishers = traverson
                .follow("comics", "search", "findVolumesBySeriesAndPublishers")
                .toObject(new ParameterizedTypeReference<Resources<Publisher>>() { })
                .getContent()
                .stream().collect(Collectors.toList());

        assertThat(publishers.size()).isEqualTo(1);
        assertThat(publishers.get(0).getSeries().size()).isEqualTo(1);
        assertThat(publishers.get(0).getSeries().get(0).getVolumes().size()).isEqualTo(3);
        assertThat(publishers.get(0).getSeries().get(0).getVolumes().get(0).getVolume()).isEqualTo("1999");
        assertThat(publishers.get(0).getSeries().get(0).getVolumes().get(1).getVolume()).isEqualTo("2005");
        assertThat(publishers.get(0).getSeries().get(0).getVolumes().get(2).getVolume()).isEqualTo("2011");
    }
}
