package de.wasenweg.alfred.unit;

import de.wasenweg.alfred.TestUtil;
import de.wasenweg.alfred.comics.Comic;
import de.wasenweg.alfred.scanner.FileMetaDataService;
import de.wasenweg.alfred.scanner.ScannerIssue;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.w3c.dom.Document;

import java.io.File;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
public class FileMetaDataServiceTest {

  @TempDir
  public transient File testBed;

  @InjectMocks
  private transient FileMetaDataService fileMetaDataService;

  @Test
  public void readWithInvalidMonth() throws Exception {
    TestUtil.copyResources(this.testBed, "src/test/resources/fixtures/special_cases/invalid_month");
    final String comicPath = this.testBed.getAbsolutePath() + "/Batman 402 (1940).cbz";
    final Comic comic = new Comic();
    comic.setPath(comicPath);

    final List<ScannerIssue> issues = this.fileMetaDataService.read(comic);
    assertThat(issues.size()).isEqualTo(1);
    assertThat(issues.get(0).getMessage()).isEqualTo("Couldn't read Month value of 'December'. Falling back to '0'");
  }

  @Test
  public void readWithInvalidNumber() throws Exception {
    TestUtil.copyResources(this.testBed, "src/test/resources/fixtures/special_cases/invalid_number");
    final String comicPath = this.testBed.getAbsolutePath() + "/Batman 402 (1940).cbz";
    final Comic comic = new Comic();
    comic.setPath(comicPath);

    final List<ScannerIssue> issues = this.fileMetaDataService.read(comic);
    assertThat(issues.size()).isEqualTo(1);
    assertThat(issues.get(0).getMessage()).isEqualTo("Couldn't read number 'zero'. Falling back to '0'.");
  }

  @Test
  public void writeWithInvalidNumber() throws Exception {
    TestUtil.copyResources(this.testBed, "src/test/resources/fixtures/simple");
    final String comicPath = this.testBed.getAbsolutePath() + "/Batman 402 (1940).cbz";
    final Comic comic = new Comic();
    comic.setPath(comicPath);

    assertThat(comic.getMonth()).isEqualTo(null);
    this.fileMetaDataService.write(comic);
    final Document document = TestUtil.parseComicInfo(comicPath);
    assertThat(TestUtil.getText(document, "Month")).isEqualTo("");
  }
}
