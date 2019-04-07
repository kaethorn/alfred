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
import java.util.Arrays;
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
        comicRepository.saveAll(Arrays.asList(
                ComicFixtures.COMIC_V1_1,
                ComicFixtures.COMIC_V1_2));

        final Traverson traverson = new Traverson(new URI("http://localhost:" + port + "/api/"), MediaTypes.HAL_JSON);
        final List<Comic> comics = traverson
                .follow("comics")
                .toObject(new ParameterizedTypeReference<Resources<Comic>>() { })
                .getContent()
                .stream().collect(Collectors.toList());

        assertThat(comics.size()).isEqualTo(2);
        assertThat(comics.get(0).getTitle()).isEqualTo(ComicFixtures.COMIC_V1_1.getTitle());
        assertThat(comics.get(1).getTitle()).isEqualTo(ComicFixtures.COMIC_V1_2.getTitle());
    }

    @Test
    public void findLastReadForVolume() throws Exception {
        comicRepository.saveAll(Arrays.asList(
                ComicFixtures.COMIC_V1_1_READ,
                ComicFixtures.COMIC_V1_2,
                ComicFixtures.COMIC_V1_3));

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
    public void findBookmarksMultipleVolumes() throws Exception {
        comicRepository.saveAll(Arrays.asList(
                // Partly read volume at second issue
                ComicFixtures.COMIC_V1_1_READ,
                ComicFixtures.COMIC_V1_2,
                ComicFixtures.COMIC_V1_3,

                // Unread volume
                ComicFixtures.COMIC_V2_1,
                ComicFixtures.COMIC_V2_2,
                ComicFixtures.COMIC_V2_3,

                // Partly read volume at third issue
                ComicFixtures.COMIC_V3_1_READ,
                ComicFixtures.COMIC_V3_2_READ,
                ComicFixtures.COMIC_V3_3));

        final Traverson traverson = new Traverson(new URI("http://localhost:" + port + "/api/"), MediaTypes.HAL_JSON);

        final List<Comic> comics = traverson
                .follow("comics", "search", "findAllLastReadPerVolume")
                .toObject(new ParameterizedTypeReference<Resources<Comic>>() { })
                .getContent()
                .stream().collect(Collectors.toList());

        assertThat(comics.size()).isEqualTo(2);
        assertThat(comics.get(0).getTitle()).isEqualTo(ComicFixtures.COMIC_V1_2.getTitle());
        assertThat(comics.get(1).getTitle()).isEqualTo(ComicFixtures.COMIC_V3_3.getTitle());
    }

    @Test
    public void findBookmarksFirstStarted() throws Exception {
        comicRepository.saveAll(Arrays.asList(
                // Partly read volume at first issue
                ComicFixtures.COMIC_V1_1_STARTED,
                ComicFixtures.COMIC_V1_2,
                ComicFixtures.COMIC_V1_3));

        final Traverson traverson = new Traverson(new URI("http://localhost:" + port + "/api/"), MediaTypes.HAL_JSON);

        final List<Comic> comics = traverson
                .follow("comics", "search", "findAllLastReadPerVolume")
                .toObject(new ParameterizedTypeReference<Resources<Comic>>() { })
                .getContent()
                .stream().collect(Collectors.toList());

        assertThat(comics.size()).isEqualTo(1);
        assertThat(comics.get(0).getTitle()).isEqualTo(ComicFixtures.COMIC_V1_1.getTitle());
    }

    @Test
    public void findBookmarksAllRead() throws Exception {
        comicRepository.saveAll(Arrays.asList(
                // Completely read volume
                ComicFixtures.COMIC_V3_1_READ,
                ComicFixtures.COMIC_V3_2_READ,
                ComicFixtures.COMIC_V3_3_READ));

        final Traverson traverson = new Traverson(new URI("http://localhost:" + port + "/api/"), MediaTypes.HAL_JSON);

        final List<Comic> comics = traverson
                .follow("comics", "search", "findAllLastReadPerVolume")
                .toObject(new ParameterizedTypeReference<Resources<Comic>>() { })
                .getContent()
                .stream().collect(Collectors.toList());

        assertThat(comics.size()).isEqualTo(0);
    }

    @Test
    public void findBookmarksNoneRead() throws Exception {
        comicRepository.saveAll(Arrays.asList(
                // Completely unread volume
                ComicFixtures.COMIC_V3_1,
                ComicFixtures.COMIC_V3_2,
                ComicFixtures.COMIC_V3_3));

        final Traverson traverson = new Traverson(new URI("http://localhost:" + port + "/api/"), MediaTypes.HAL_JSON);

        final List<Comic> comics = traverson
                .follow("comics", "search", "findAllLastReadPerVolume")
                .toObject(new ParameterizedTypeReference<Resources<Comic>>() { })
                .getContent()
                .stream().collect(Collectors.toList());

        assertThat(comics.size()).isEqualTo(0);
    }

    @Test
    public void findBookmarksLastStarted() throws Exception {
        comicRepository.saveAll(Arrays.asList(
                // Almost read volume
                ComicFixtures.COMIC_V3_1_READ,
                ComicFixtures.COMIC_V3_2_READ,
                ComicFixtures.COMIC_V3_3));

        final Traverson traverson = new Traverson(new URI("http://localhost:" + port + "/api/"), MediaTypes.HAL_JSON);

        final List<Comic> comics = traverson
                .follow("comics", "search", "findAllLastReadPerVolume")
                .toObject(new ParameterizedTypeReference<Resources<Comic>>() { })
                .getContent()
                .stream().collect(Collectors.toList());

        assertThat(comics.size()).isEqualTo(1);
        assertThat(comics.get(0).getTitle()).isEqualTo(ComicFixtures.COMIC_V3_3.getTitle());
    }

    @Test
    public void findBookmarksWithGaps() throws Exception {
        comicRepository.saveAll(Arrays.asList(
                // A volume with unread first issue
                ComicFixtures.COMIC_V3_1,
                ComicFixtures.COMIC_V3_2_READ,
                ComicFixtures.COMIC_V3_3));

        final Traverson traverson = new Traverson(new URI("http://localhost:" + port + "/api/"), MediaTypes.HAL_JSON);

        final List<Comic> comics = traverson
                .follow("comics", "search", "findAllLastReadPerVolume")
                .toObject(new ParameterizedTypeReference<Resources<Comic>>() { })
                .getContent()
                .stream().collect(Collectors.toList());

        assertThat(comics.size()).isEqualTo(1);
        assertThat(comics.get(0).getTitle()).isEqualTo(ComicFixtures.COMIC_V3_1.getTitle());
    }
}
