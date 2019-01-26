package de.wasenweg.comix;

import de.wasenweg.komix.KomixApplication;
import de.wasenweg.komix.comics.Comic;
import de.wasenweg.komix.comics.ComicRepository;
import de.wasenweg.komix.preferences.Preference;
import de.wasenweg.komix.preferences.PreferenceRepository;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertEquals;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = KomixApplication.class, webEnvironment = WebEnvironment.RANDOM_PORT)
public class ScannerIngrationTest {

    @LocalServerPort
    private int port;

    @Autowired
    private PreferenceRepository preferenceRepository;

    @Autowired
    private ComicRepository comicRepository;

    @Before
    public void setUp() {
        final Preference comicsPath = preferenceRepository.findByKey("comics.path").get();
        comicsPath.setValue("src/test/resources/fixtures/simple");
        preferenceRepository.save(comicsPath);
    }

    @After
    public void tearDown() {
        comicRepository.deleteAll();
    }

    @Test
    public void emittsScanProgressEvents() throws Exception {
        final WebClient client = WebClient
                .create("http://localhost:" + port + "/api");

        final ParameterizedTypeReference<ServerSentEvent<String>> type =
                new ParameterizedTypeReference<ServerSentEvent<String>>() { };

        final Flux<ServerSentEvent<String>> eventStream = client.get()
                .uri("/scan-progress")
                .retrieve()
                .bodyToFlux(type);

        final List<ServerSentEvent<String>> events = new ArrayList<ServerSentEvent<String>>();

        final CompletableFuture<String> future = new CompletableFuture<>();
        eventStream.subscribe(
                content -> {
                    events.add(content);
                },
                error -> {
                    assertThat(true).isEqualTo(false);
                    future.complete("Error");
                },
                () -> {
                    future.complete("Complete");
                }
        );
        assertEquals("Complete", future.get());
        final List<Comic> comics = StreamSupport
                .stream(this.comicRepository.findAll().spliterator(), false)
                .collect(Collectors.toList());
        assertThat(comics.size()).isEqualTo(1);

        assertThat(events.get(0).event()).isEqualTo("total");
        assertThat(events.get(0).data()).isEqualTo("1");
        assertThat(events.get(1).event()).isEqualTo("current-file");
        assertThat(events.get(1).data()).isEqualTo("src/test/resources/fixtures/simple/Batman 402 (1940).cbz");
        assertThat(events.get(2).event()).isEqualTo("done");
        assertThat(events.get(2).data()).isEqualTo("");
    }
}
