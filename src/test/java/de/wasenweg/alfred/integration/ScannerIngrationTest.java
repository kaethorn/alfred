package de.wasenweg.alfred.integration;

import de.wasenweg.alfred.AlfredApplication;
import de.wasenweg.alfred.comics.Comic;
import de.wasenweg.alfred.comics.ComicRepository;
import de.wasenweg.alfred.progress.ProgressRepository;

import org.junit.After;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import reactor.test.StepVerifier;

import java.time.Duration;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = { AlfredApplication.class }, webEnvironment = WebEnvironment.RANDOM_PORT)
@EnableAutoConfiguration
@ActiveProfiles(profiles = "test")
public class ScannerIngrationTest {

  @LocalServerPort
  private int port;

  @Autowired
  private ComicRepository comicRepository;

  @Autowired
  private ProgressRepository progressRepository;

  @Autowired
  private IntegrationTestHelper integrationTestHelper;

  @Rule
  public TemporaryFolder testBed = new TemporaryFolder();

  @After
  public void tearDown() {
    this.comicRepository.deleteAll();
    this.progressRepository.deleteAll();
  }

  @Test
  @DirtiesContext
  public void emittsScanProgressEvents() throws Exception {
    // Given
    this.integrationTestHelper.setComicsPath("src/test/resources/fixtures/simple", this.testBed);

    // When
    StepVerifier.create(this.integrationTestHelper.triggerScan(this.port))
        .expectNext("start")
        .expectNext("1")
        .expectNext(this.testBed.getRoot().getAbsolutePath() + "/Batman 402 (1940).cbz")
        .expectNext("cleanUp")
        .expectNext("association")
        .expectNext("done")
        .thenCancel()
        .verify(Duration.ofSeconds(2L));

    // Then
    final List<Comic> comics = this.comicRepository.findAll();
    assertThat(comics.size()).isEqualTo(1);
  }
}
