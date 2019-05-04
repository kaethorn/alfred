package de.wasenweg.alfred;

import de.wasenweg.alfred.comics.ComicRepository;
import de.wasenweg.alfred.settings.Setting;
import de.wasenweg.alfred.settings.SettingRepository;

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
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import java.time.Duration;

import static org.springframework.http.MediaType.TEXT_EVENT_STREAM;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = { AlfredApplication.class }, webEnvironment = WebEnvironment.RANDOM_PORT)
@EnableAutoConfiguration
@ActiveProfiles(profiles = "test")
public class ScannerIngrationTest {

    @LocalServerPort
    private int port;

    @Autowired
    private SettingRepository preferenceRepository;

    @Autowired
    private ComicRepository comicRepository;

    @Before
    public void setUp() {
        final Setting comicsPath = preferenceRepository.findByKey("comics.path").get();
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
