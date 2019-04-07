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
import java.util.GregorianCalendar;
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
        comicRepository.save(ComicFixtures.COMIC_V1_1.build());
        comicRepository.save(ComicFixtures.COMIC_V1_2.build());

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
        comicRepository.save(ComicFixtures.COMIC_V1_1
                .read(true)
                .lastRead(new GregorianCalendar(2019, 1, 20).getTime())
                .build());
        comicRepository.save(ComicFixtures.COMIC_V1_2.build());
        comicRepository.save(ComicFixtures.COMIC_V1_3.build());

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
    public void findBookmarksMixed() throws Exception {
        // Partly read volume at second issue
        comicRepository.save(ComicFixtures.COMIC_V1_1
                .read(true)
                .lastRead(new GregorianCalendar(2019, 1, 20).getTime())
                .build());
        comicRepository.save(ComicFixtures.COMIC_V1_2.build());
        comicRepository.save(ComicFixtures.COMIC_V1_3.build());

        // Unread volume
        comicRepository.save(ComicFixtures.COMIC_V2_1.build());
        comicRepository.save(ComicFixtures.COMIC_V2_2.build());
        comicRepository.save(ComicFixtures.COMIC_V2_3.build());

        // Partly read volume at third issue
        comicRepository.save(ComicFixtures.COMIC_V3_1
                .read(true)
                .lastRead(new GregorianCalendar(2018, 10, 8).getTime())
                .build());
        comicRepository.save(ComicFixtures.COMIC_V3_2
                .read(true)
                .lastRead(new GregorianCalendar(2018, 10, 13).getTime())
                .build());
        comicRepository.save(ComicFixtures.COMIC_V3_3.build());

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

    @Test
    public void findBookmarksFirstStarted() throws Exception {
        // Partly read volume at first issue
        comicRepository.save(ComicFixtures.COMIC_V1_1
                .currentPage((short) 4)
                .lastRead(new GregorianCalendar(2019, 1, 20).getTime())
                .build());
        comicRepository.save(ComicFixtures.COMIC_V1_2.build());
        comicRepository.save(ComicFixtures.COMIC_V1_3.build());

        final Traverson traverson = new Traverson(new URI("http://localhost:" + port + "/api/"), MediaTypes.HAL_JSON);

        final List<Comic> comics = traverson
                .follow("comics", "search", "findAllLastReadPerVolume")
                .toObject(new ParameterizedTypeReference<Resources<Comic>>() { })
                .getContent()
                .stream().collect(Collectors.toList());

        assertThat(comics.size()).isEqualTo(1);
        assertThat(comics.get(0).getTitle()).isEqualTo("Title A1");
    }

    @Test
    public void findBookmarksAllRead() throws Exception {
        // Completely read volume
        comicRepository.save(ComicFixtures.COMIC_V1_1
                .read(true)
                .lastRead(new GregorianCalendar(2019, 1, 20).getTime())
                .build());
        comicRepository.save(ComicFixtures.COMIC_V1_2
                .read(true)
                .lastRead(new GregorianCalendar(2019, 1, 21).getTime())
                .build());
        comicRepository.save(ComicFixtures.COMIC_V1_3
                .read(true)
                .lastRead(new GregorianCalendar(2019, 1, 22).getTime())
                .build());

        final Traverson traverson = new Traverson(new URI("http://localhost:" + port + "/api/"), MediaTypes.HAL_JSON);

        final List<Comic> comics = traverson
                .follow("comics", "search", "findAllLastReadPerVolume")
                .toObject(new ParameterizedTypeReference<Resources<Comic>>() { })
                .getContent()
                .stream().collect(Collectors.toList());

        assertThat(comics.size()).isEqualTo(0);
    }
}
