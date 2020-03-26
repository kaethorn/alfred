package de.wasenweg.alfred.unit;

import de.wasenweg.alfred.TestUtil;
import de.wasenweg.alfred.comics.Comic;
import de.wasenweg.alfred.fixtures.ComicFixtures;
import de.wasenweg.alfred.queue.QueueService;
import de.wasenweg.alfred.scanner.ScannerService;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.File;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class QueueServiceTest {

  @TempDir
  public transient File testBed;

  @Mock
  private transient ScannerService scannerService;

  @InjectMocks
  private transient QueueService queueService;

  @Test
  public void flattenWithError() throws Exception {
    TestUtil.copyResources(this.testBed, "src/test/resources/fixtures/special_cases/not_flat_with_duplicate");
    final String comicPath = this.testBed.getAbsolutePath() + "/Batman 402 (1940).cbz";
    final Comic comic = ComicFixtures.COMIC_V1_1.toBuilder().path(comicPath).build();
    this.queueService.flatten(comic);

    verify(this.scannerService).processComic(comic);
    final List<String> files = TestUtil.listFiles(comicPath);
    assertThat(files.size()).isEqualTo(7);
    assertThat(files.get(3)).isEqualTo("/2/ComicInfo.xml");
    assertThat(files.get(6)).isEqualTo("/ComicInfo.xml");
  }
}
