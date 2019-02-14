package de.wasenweg.comix;

import de.wasenweg.komix.KomixApplication;
import de.wasenweg.komix.comics.Comic;
import de.wasenweg.komix.comics.ComicRepository;

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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = KomixApplication.class, webEnvironment = WebEnvironment.RANDOM_PORT)
@EnableAutoConfiguration
public class ComicsIntegrationTest {

    @LocalServerPort
    private int port;

    @Autowired
    private ComicRepository comicRepository;

    @After
    public void tearDown() {
        comicRepository.deleteAll();
    }

    @Test
    public void getAllComics() throws Exception {
        comicRepository.save(ComicFixtures.COMIC_A1);
        comicRepository.save(ComicFixtures.COMIC_A2);

        final Traverson traverson = new Traverson(new URI("http://localhost:" + port + "/api/"), MediaTypes.HAL_JSON);
        final List<Comic> comics = traverson
                .follow("comics")
                .toObject(new ParameterizedTypeReference<Resources<Comic>>() { })
                .getContent()
                .stream().collect(Collectors.toList());

        assertThat(comics.size()).isEqualTo(2);
        assertThat(comics.get(0).getTitle()).isEqualTo("Title A1");
        assertThat(comics.get(1).getTitle()).isEqualTo("Title A2");
    }

    @Test
    public void findLastReadForVolume() throws Exception {
        comicRepository.save(ComicFixtures.COMIC_A1);
        comicRepository.save(ComicFixtures.COMIC_A2);
        comicRepository.save(ComicFixtures.COMIC_A3);

        final Traverson traverson = new Traverson(new URI("http://localhost:" + port + "/api/"), MediaTypes.HAL_JSON);

        final Map<String, Object> params = new HashMap<>();
        params.put("publisher", "Pub A");
        params.put("series", "Series A");
        params.put("volume", "1999");

        final Comic comic = traverson
                .follow("comics", "search", "findLastReadForVolume")
                .withTemplateParameters(params)
                .toObject(new ParameterizedTypeReference<Comic>() { });

        assertThat(comic.getTitle()).isEqualTo("Title A2");
    }

    @Test
    public void findAllLastReadPerVolume() throws Exception {
        comicRepository.save(ComicFixtures.COMIC_A1);
        comicRepository.save(ComicFixtures.COMIC_A2);
        comicRepository.save(ComicFixtures.COMIC_A3);
        comicRepository.save(ComicFixtures.COMIC_B1);
        comicRepository.save(ComicFixtures.COMIC_B2);
        comicRepository.save(ComicFixtures.COMIC_B3);
        comicRepository.save(ComicFixtures.COMIC_C1);
        comicRepository.save(ComicFixtures.COMIC_C2);
        comicRepository.save(ComicFixtures.COMIC_C3);

        final Traverson traverson = new Traverson(new URI("http://localhost:" + port + "/api/"), MediaTypes.HAL_JSON);

        final List<Comic> comics = traverson
                .follow("comics", "search", "findAllLastReadPerVolume")
                .toObject(new ParameterizedTypeReference<Resources<Comic>>() { })
                .getContent()
                .stream().collect(Collectors.toList());

        assertThat(comics.size()).isEqualTo(2);
        assertThat(comics.get(0).getTitle()).isEqualTo("Title A2");
        assertThat(comics.get(1).getTitle()).isEqualTo("Title C3");
    }
}
