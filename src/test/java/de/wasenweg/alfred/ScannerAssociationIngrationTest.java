package de.wasenweg.alfred;

import de.wasenweg.alfred.comics.Comic;
import de.wasenweg.alfred.comics.ComicRepository;

import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.web.server.LocalServerPort;
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
  private IntegrationTestHelper integrationTestHelper;

  @After
  public void tearDown() {
    comicRepository.deleteAll();
  }

  @Test
  public void associatesComics() throws Exception {
    // Given
    integrationTestHelper.setComicsPath("src/test/resources/fixtures/full");

    // When
    StepVerifier.create(integrationTestHelper.triggerScan(port))
        .expectNext("305")
        .expectNextCount(305)
        .expectNext("")
        .thenCancel()
        .verify();

    // Then
    final List<Comic> comics = comicRepository.findAll();
    assertThat(comics.size()).isEqualTo(305);

    final List<Comic> batgirlVol2008 = comicRepository
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
