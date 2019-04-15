package de.wasenweg.comix;

import de.wasenweg.komix.KomixApplication;
import de.wasenweg.komix.comics.ComicRepository;
import de.wasenweg.komix.preferences.Preference;
import de.wasenweg.komix.preferences.PreferenceRepository;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import java.time.Duration;

import static org.springframework.http.MediaType.TEXT_EVENT_STREAM;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = { KomixApplication.class }, webEnvironment = WebEnvironment.RANDOM_PORT)
@EnableAutoConfiguration
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

        final Flux<String> result = client.get()
            .uri("/scan-progress")
            .accept(TEXT_EVENT_STREAM)
            .retrieve()
            .bodyToFlux(new ParameterizedTypeReference<String>() { });

        StepVerifier.create(result)
            .expectNext("1")
            .expectNext("src/test/resources/fixtures/simple/Batman 402 (1940).cbz")
            .expectNext("")
            .thenCancel()
            .verify(Duration.ofSeconds(2L));
    }
}
