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

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = { AlfredApplication.class }, webEnvironment = WebEnvironment.RANDOM_PORT)
@EnableAutoConfiguration
@ActiveProfiles(profiles = "test")
public class ScannerAssociationIngrationTest {

  @LocalServerPort
  private int port;

  @Autowired
  private ComicRepository comicRepository;

  @Autowired
  private ProgressRepository progressRepository;

  @Autowired
  private IntegrationTestHelper helper;

  @Rule
  public TemporaryFolder testBed = new TemporaryFolder();

  @After
  public void tearDown() {
    this.comicRepository.deleteAll();
    this.progressRepository.deleteAll();
  }

  @Test
  @DirtiesContext
  public void associatesComics() throws Exception {
    // Given
    this.helper.setComicsPath("src/test/resources/fixtures/full", this.testBed);

    // When
    StepVerifier.create(this.helper.triggerScan(this.port))
        .expectNext("start")
        .expectNext("305")
        .expectNextCount(305)
        .expectNext("cleanUp")
        .expectNext("association")
        .expectNext("done")
        .thenCancel()
        .verify();

    // Then
    final List<Comic> comics = this.comicRepository.findAll();
    assertThat(comics.size()).isEqualTo(305);

    final List<Comic> batgirlVol2008 = this.comicRepository
        .findAllByPublisherAndSeriesAndVolumeOrderByPosition("", "DC Comics", "Batgirl", "2008");
    assertThat(batgirlVol2008.size()).isEqualTo(6);

    assertThat(batgirlVol2008.get(0).getNextId()).isEqualTo(batgirlVol2008.get(1).getId());
    assertThat(batgirlVol2008.get(0).getPreviousId()).isNull();
    assertThat(batgirlVol2008.get(1).getNextId()).isEqualTo(batgirlVol2008.get(2).getId());
    assertThat(batgirlVol2008.get(1).getPreviousId()).isEqualTo(batgirlVol2008.get(0).getId());
    assertThat(batgirlVol2008.get(5).getNextId()).isNull();
    assertThat(batgirlVol2008.get(5).getPreviousId()).isEqualTo(batgirlVol2008.get(4).getId());
  }
}
