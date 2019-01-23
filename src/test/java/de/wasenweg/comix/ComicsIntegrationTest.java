package de.wasenweg.comix;

import de.wasenweg.komix.Comic;
import de.wasenweg.komix.ComicRepository;
import de.wasenweg.komix.KomixApplication;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.hateoas.MediaTypes;
import org.springframework.hateoas.Resources;
import org.springframework.hateoas.client.Traverson;
import org.springframework.test.context.junit4.SpringRunner;

import java.net.URI;
import java.util.Collection;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = KomixApplication.class, webEnvironment = WebEnvironment.RANDOM_PORT)
public class ComicsIntegrationTest {


    @LocalServerPort
    private int port;

    @Autowired
    private ComicRepository comicRepository;

    @Before
    public void setUp() {
        comicRepository.save(new Comic("/a.cbz", "Title A", "Series A", "1", "0001.0", (short) 2007, (short) 9, "Pub A"));
        comicRepository.save(new Comic("/b.cbz", "Title B", "Series A", "2", "0002.0", (short) 2007, (short) 10, "Pub A"));
    }

    @After
    public void tearDown() {
        comicRepository.deleteAll();
    }

    @Test
    public void getAllComics() throws Exception {
        final Traverson traverson = new Traverson(new URI("http://localhost:" + port + "/api/"), MediaTypes.HAL_JSON);
        final ParameterizedTypeReference<Resources<Comic>> typeRef = new ParameterizedTypeReference<Resources<Comic>>() { };
        final Collection<Comic> comics = traverson.follow("comics").toObject(typeRef).getContent();

        assertThat(comics.size()).isEqualTo(2);
        assertThat(comics.stream().map(Comic::getTitle).collect(Collectors.toList())).contains("Title A");
        assertThat(comics.stream().map(Comic::getTitle).collect(Collectors.toList())).contains("Title B");
    }
}
