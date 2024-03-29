package de.wasenweg.alfred.unit;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import de.wasenweg.alfred.TestUtil;
import de.wasenweg.alfred.comics.Comic;
import de.wasenweg.alfred.comics.ComicRepository;
import de.wasenweg.alfred.scanner.ApiMetaDataService;
import de.wasenweg.alfred.scanner.FileMetaDataService;
import de.wasenweg.alfred.scanner.ScanProgressRepository;
import de.wasenweg.alfred.scanner.ScanProgressService;
import de.wasenweg.alfred.scanner.ScannerIssue;
import de.wasenweg.alfred.scanner.ScannerService;
import de.wasenweg.alfred.settings.SettingsService;
import de.wasenweg.alfred.thumbnails.ThumbnailService;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import reactor.test.StepVerifier;

import java.io.File;
import java.nio.file.Paths;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class ScannerServiceTest {

  @TempDir
  public transient File testBed;

  @Spy
  private transient FileMetaDataService fileMetaDataService;

  @Mock
  private transient ComicRepository comicRepository;

  @Mock
  private transient ThumbnailService thumbnailService;

  @Mock
  private transient ApiMetaDataService apiMetaDataService;

  @Spy
  private transient ObjectMapper objectMapper;

  @Mock
  private transient SettingsService settingsService;

  @Mock
  private transient ScanProgressRepository scanProgressRepository;

  @Captor
  private transient ArgumentCaptor<ArrayList<Comic>> comicsCaptor;

  @InjectMocks
  private transient ScanProgressService scanProgressService;

  private transient ScannerService scannerService;

  @BeforeEach
  public void setUp() {
    this.scannerService = new ScannerService(
        this.apiMetaDataService,
        this.fileMetaDataService,
        this.thumbnailService,
        this.comicRepository,
        this.settingsService,
        this.scanProgressService);
  }

  @Test
  void scanComics() throws Exception {
    TestUtil.copyResources(this.testBed, "src/test/resources/fixtures/simple");

    when(this.settingsService.get("comics.path")).thenReturn(this.testBed.getAbsolutePath());
    when(this.comicRepository.findByPath(any())).thenReturn(Optional.of(new Comic()));
    when(this.comicRepository.save(any())).thenReturn(new Comic());
    when(this.scanProgressRepository.save(any())).thenAnswer(a -> a.getArguments()[0]);
    when(this.comicRepository.findAllByOrderByPublisherAscSeriesAscVolumeAscPositionAsc())
        .thenReturn(new ArrayList<>());
    doReturn(new ArrayList<>()).when(this.fileMetaDataService).read(any());
    doNothing().when(this.thumbnailService).read(any());

    StepVerifier.create(this.scannerService.scan())
        .consumeNextWith(event -> {
          assertThat(event.event()).isEqualTo("START");
          assertThat(event.data()).isEqualTo("start");
        })
        .consumeNextWith(event -> {
          assertThat(event.event()).isEqualTo("TOTAL");
          assertThat(event.data()).isEqualTo("1");
        })
        .consumeNextWith(event -> {
          assertThat(event.event()).isEqualTo("CURRENT_FILE");
          assertThat(event.data()).endsWith("Batman 402 (1940).cbz");
        })
        .consumeNextWith(event -> {
          assertThat(event.event()).isEqualTo("CLEAN_UP");
          assertThat(event.data()).isEqualTo("cleanUp");
        })
        .consumeNextWith(event -> {
          assertThat(event.event()).isEqualTo("ASSOCIATION");
          assertThat(event.data()).isEqualTo("association");
        })
        .consumeNextWith(event -> {
          assertThat(event.event()).isEqualTo("DONE");
          assertThat(event.data()).isEqualTo("done");
        })
        .expectComplete()
        .verify(Duration.ofSeconds(8L));
  }

  @Test
  void scanComicsWithException() throws Exception {
    when(this.settingsService.get("comics.path")).thenReturn(this.testBed.getAbsolutePath() + "/invalid/");
    when(this.scanProgressRepository.save(any())).thenAnswer(a -> a.getArguments()[0]);

    StepVerifier.create(this.scannerService.scan())
        .consumeNextWith(event -> {
          assertThat(event.event()).isEqualTo("START");
          assertThat(event.data()).isEqualTo("start");
        })
        .consumeNextWith(event -> {
          assertThat(event.event()).isEqualTo("SCAN_ISSUE");
          assertThat(event.data()).contains("ERROR");
        })
        .consumeNextWith(event -> {
          assertThat(event.event()).isEqualTo("DONE");
          assertThat(event.data()).isEqualTo("done");
        })
        .expectComplete()
        .verify(Duration.ofSeconds(8L));

    verify(this.objectMapper).writeValueAsString(any());
  }

  @Test
  void scanWithFileReaderError() throws Exception {
    TestUtil.copyResources(this.testBed, "src/test/resources/fixtures/not_flat");

    when(this.settingsService.get("comics.path")).thenReturn(this.testBed.getAbsolutePath());
    when(this.comicRepository.save(any())).thenReturn(new Comic());
    when(this.scanProgressRepository.save(any())).thenAnswer(a -> a.getArguments()[0]);
    doNothing().when(this.thumbnailService).read(any());

    StepVerifier.create(this.scannerService.scan())
        .consumeNextWith(event -> {
          assertThat(event.event()).isEqualTo("START");
          assertThat(event.data()).isEqualTo("start");
        })
        .consumeNextWith(event -> {
          assertThat(event.event()).isEqualTo("TOTAL");
          assertThat(event.data()).isEqualTo("1");
        })
        .consumeNextWith(event -> {
          assertThat(event.event()).isEqualTo("CURRENT_FILE");
          assertThat(event.data()).endsWith("Batman 402 (1940).cbz");
        })
        .consumeNextWith(event -> {
          assertThat(event.event()).isEqualTo("SCAN_ISSUE");
          assertThat(event.data()).contains("Found directory entries");
        })
        .consumeNextWith(event -> {
          assertThat(event.event()).isEqualTo("CLEAN_UP");
          assertThat(event.data()).isEqualTo("cleanUp");
        })
        .consumeNextWith(event -> {
          assertThat(event.event()).isEqualTo("ASSOCIATION");
          assertThat(event.data()).isEqualTo("association");
        })
        .consumeNextWith(event -> {
          assertThat(event.event()).isEqualTo("DONE");
          assertThat(event.data()).isEqualTo("done");
        })
        .expectComplete()
        .verify(Duration.ofSeconds(8L));

    verify(this.objectMapper).writeValueAsString(any());
  }

  @Test
  void scanWithXmlError() throws Exception {
    TestUtil.copyResources(this.testBed, "src/test/resources/fixtures/special_cases/invalid_xml");

    when(this.settingsService.get("comics.path")).thenReturn(this.testBed.getAbsolutePath());
    when(this.comicRepository.save(any())).thenReturn(new Comic());
    when(this.scanProgressRepository.save(any())).thenAnswer(a -> a.getArguments()[0]);

    StepVerifier.create(this.scannerService.scan())
        .consumeNextWith(event -> {
          assertThat(event.event()).isEqualTo("START");
          assertThat(event.data()).isEqualTo("start");
        })
        .consumeNextWith(event -> {
          assertThat(event.event()).isEqualTo("TOTAL");
          assertThat(event.data()).isEqualTo("1");
        })
        .consumeNextWith(event -> {
          assertThat(event.event()).isEqualTo("CURRENT_FILE");
          assertThat(event.data()).endsWith("Batman 402 (1940).cbz");
        })
        .consumeNextWith(event -> {
          assertThat(event.event()).isEqualTo("SCAN_ISSUE");
          assertThat(event.data()).contains("The end-tag for element type \\\"ComicInfo\\\" must end with a '>'");
        })
        .consumeNextWith(event -> {
          assertThat(event.event()).isEqualTo("CLEAN_UP");
          assertThat(event.data()).isEqualTo("cleanUp");
        })
        .consumeNextWith(event -> {
          assertThat(event.event()).isEqualTo("ASSOCIATION");
          assertThat(event.data()).isEqualTo("association");
        })
        .consumeNextWith(event -> {
          assertThat(event.event()).isEqualTo("DONE");
          assertThat(event.data()).isEqualTo("done");
        })
        .expectComplete()
        .verify(Duration.ofSeconds(8L));

    verify(this.objectMapper).writeValueAsString(any());
  }

  @Test
  void scanWithoutImages() throws Exception {
    TestUtil.copyResources(this.testBed, "src/test/resources/fixtures/special_cases/no_images");

    when(this.settingsService.get("comics.path")).thenReturn(this.testBed.getAbsolutePath());
    when(this.comicRepository.save(any())).thenReturn(new Comic());
    when(this.scanProgressRepository.save(any())).thenAnswer(a -> a.getArguments()[0]);

    StepVerifier.create(this.scannerService.scan())
        .consumeNextWith(event -> {
          assertThat(event.event()).isEqualTo("START");
          assertThat(event.data()).isEqualTo("start");
        })
        .consumeNextWith(event -> {
          assertThat(event.event()).isEqualTo("TOTAL");
          assertThat(event.data()).isEqualTo("1");
        })
        .consumeNextWith(event -> {
          assertThat(event.event()).isEqualTo("CURRENT_FILE");
          assertThat(event.data()).endsWith("Batman 402 (1940).cbz");
        })
        .consumeNextWith(event -> {
          assertThat(event.event()).isEqualTo("SCAN_ISSUE");
          assertThat(event.data()).contains("No images found");
        })
        .consumeNextWith(event -> {
          assertThat(event.event()).isEqualTo("CLEAN_UP");
          assertThat(event.data()).isEqualTo("cleanUp");
        })
        .consumeNextWith(event -> {
          assertThat(event.event()).isEqualTo("ASSOCIATION");
          assertThat(event.data()).isEqualTo("association");
        })
        .consumeNextWith(event -> {
          assertThat(event.event()).isEqualTo("DONE");
          assertThat(event.data()).isEqualTo("done");
        })
        .expectComplete()
        .verify(Duration.ofSeconds(8L));

    verify(this.objectMapper).writeValueAsString(any());
  }

  @Test
  void scanWithApiReaderErrors() throws Exception {
    TestUtil.copyResources(this.testBed, "src/test/resources/fixtures/incomplete");

    when(this.settingsService.get("comics.path")).thenReturn(this.testBed.getAbsolutePath());
    when(this.comicRepository.save(any())).thenReturn(new Comic());
    when(this.apiMetaDataService.set(any()))
        .thenReturn(Arrays.asList(ScannerIssue.builder()
            .message("Could not reach ComicVine API")
            .type(ScannerIssue.Type.UNKNOWN)
            .severity(ScannerIssue.Severity.ERROR)
            .build()));
    when(this.scanProgressRepository.save(any())).thenAnswer(a -> a.getArguments()[0]);
    doNothing().when(this.fileMetaDataService).write(any());

    StepVerifier.create(this.scannerService.scan())
        .consumeNextWith(event -> {
          assertThat(event.event()).isEqualTo("START");
          assertThat(event.data()).isEqualTo("start");
        })
        .consumeNextWith(event -> {
          assertThat(event.event()).isEqualTo("TOTAL");
          assertThat(event.data()).isEqualTo("1");
        })
        .consumeNextWith(event -> {
          assertThat(event.event()).isEqualTo("CURRENT_FILE");
          assertThat(event.data()).endsWith("Batman 701 (1940).cbz");
        })
        .consumeNextWith(event -> {
          assertThat(event.event()).isEqualTo("SCAN_ISSUE");
          assertThat(event.data()).contains("Could not reach ComicVine API");
        })
        .consumeNextWith(event -> {
          assertThat(event.event()).isEqualTo("CLEAN_UP");
          assertThat(event.data()).isEqualTo("cleanUp");
        })
        .consumeNextWith(event -> {
          assertThat(event.event()).isEqualTo("ASSOCIATION");
          assertThat(event.data()).isEqualTo("association");
        })
        .consumeNextWith(event -> {
          assertThat(event.event()).isEqualTo("DONE");
          assertThat(event.data()).isEqualTo("done");
        })
        .expectComplete()
        .verify(Duration.ofSeconds(8L));

    verify(this.objectMapper).writeValueAsString(any());
    verify(this.fileMetaDataService).write(any());
  }

  @Test
  void scanWithJacksonError() throws Exception {
    doThrow(new JsonProcessingException("") {
      private static final long serialVersionUID = -4677056066803637172L; // NOPMD
    }).when(this.objectMapper).writeValueAsString(any());
    when(this.settingsService.get("comics.path")).thenReturn(this.testBed.getAbsolutePath() + "/invalid/");
    when(this.scanProgressRepository.save(any())).thenAnswer(a -> a.getArguments()[0]);

    StepVerifier.create(this.scannerService.scan())
        .consumeNextWith(event -> {
          assertThat(event.event()).isEqualTo("START");
          assertThat(event.data()).isEqualTo("start");
        })
        .consumeNextWith(event -> {
          assertThat(event.event()).isEqualTo("DONE");
          assertThat(event.data()).isEqualTo("done");
        })
        .expectComplete()
        .verify(Duration.ofSeconds(8L));

    verify(this.objectMapper).writeValueAsString(any());
  }

  @Test
  void resume() {
    StepVerifier.create(this.scannerService.resume())
        .expectComplete()
        .verify(Duration.ofSeconds(1L));
  }

  @Test
  void cleanOrphans() {
    final Comic comic1 = new Comic();
    comic1.setPath("/a");
    final Comic comic2 = new Comic();
    comic2.setPath("/b");

    doNothing().when(this.comicRepository).deleteAll(any());
    doReturn(Arrays.asList(comic1, comic2)).when(this.comicRepository).findAll();

    this.scannerService.cleanOrphans(Arrays.asList(Paths.get(comic1.getPath())));

    verify(this.comicRepository).deleteAll(this.comicsCaptor.capture());
    assertThat(this.comicsCaptor.getValue().size()).isEqualTo(1);
    assertThat(this.comicsCaptor.getValue().get(0).getPath()).isEqualTo("/b");
  }

  @Test
  void cleanOrphansWithoutFiles() throws Exception {
    this.scannerService.cleanOrphans(new ArrayList<>());
    verify(this.comicRepository, times(0)).deleteAll(any());
  }
}
