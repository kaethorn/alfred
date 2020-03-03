package de.wasenweg.alfred.unit;

import de.wasenweg.alfred.TestUtil;
import de.wasenweg.alfred.comics.Comic;
import de.wasenweg.alfred.scanner.FileMetaDataService;
import de.wasenweg.alfred.scanner.InvalidFileException;
import de.wasenweg.alfred.scanner.NoImagesException;
import de.wasenweg.alfred.scanner.ScannerIssue;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.w3c.dom.Document;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

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

  @Test
  public void writeWithManga() throws Exception {
    TestUtil.copyResources(this.testBed, "src/test/resources/fixtures/simple");
    final String comicPath = this.testBed.getAbsolutePath() + "/Batman 402 (1940).cbz";
    final Comic comic = new Comic();
    comic.setPath(comicPath);
    comic.setManga(true);

    assertThat(comic.isManga()).isEqualTo(true);
    this.fileMetaDataService.write(comic);
    final Document document = TestUtil.parseComicInfo(comicPath);
    assertThat(TestUtil.getText(document, "Manga")).isEqualTo("Yes");
  }

  @Test
  public void writeWithInvalidXml() throws Exception {
    TestUtil.copyResources(this.testBed, "src/test/resources/fixtures/special_cases/invalid_xml");
    final String comicPath = this.testBed.getAbsolutePath() + "/Batman 402 (1940).cbz";
    final Comic comic = new Comic();
    comic.setPath(comicPath);
    comic.setSeries("Green Arrow");

    this.fileMetaDataService.write(comic);
    final Document document = TestUtil.parseComicInfo(comicPath);
    assertThat(TestUtil.getText(document, "Publisher")).isEqualTo("");
    assertThat(TestUtil.getText(document, "Series")).isEqualTo("Green Arrow");
  }

  @Test
  public void writeWithInvalidContent() throws Exception {
    final String comicPath = this.testBed.getAbsolutePath() + "/invalid-path/invalid.cbz";
    final Comic comic = new Comic();
    comic.setPath(comicPath);

    this.fileMetaDataService.write(comic);
    assertThat(Files.exists(Paths.get(comicPath))).isFalse();
  }

  @Test
  public void parseFilesWithDirectories() throws Exception {
    TestUtil.copyResources(this.testBed, "src/test/resources/fixtures/special_cases/containing_directory");
    final String comicPath = this.testBed.getAbsolutePath() + "/Batman 402 (1940).cbz";
    final Comic comic = new Comic();
    comic.setPath(comicPath);

    this.fileMetaDataService.parseFiles(comic);
    assertThat(comic.getFiles().size()).isEqualTo(4);
    assertThat(comic.getFiles().get(0)).isEqualTo("/1.png");
    assertThat(comic.getFiles().get(1)).isEqualTo("/2");
    assertThat(comic.getFiles().get(2)).isEqualTo("/2/2.png");
    assertThat(comic.getFiles().get(3)).isEqualTo("/ComicInfo.xml");
  }

  @Test
  public void parseFilesWithInvalidFile() throws Exception {
    TestUtil.copyResources(this.testBed, "src/test/resources/fixtures/special_cases/containing_invalid_file");
    final String comicPath = this.testBed.getAbsolutePath() + "/Batman 402 (1940).cbz";
    final Comic comic = new Comic();
    comic.setPath(comicPath);

    assertThrows(InvalidFileException.class, () -> this.fileMetaDataService.parseFiles(comic));
  }

  @Test
  public void parseFilesWithoutImages() throws Exception {
    TestUtil.copyResources(this.testBed, "src/test/resources/fixtures/special_cases/no_images");
    final String comicPath = this.testBed.getAbsolutePath() + "/Batman 402 (1940).cbz";
    final Comic comic = new Comic();
    comic.setPath(comicPath);

    assertThrows(NoImagesException.class, () -> this.fileMetaDataService.parseFiles(comic));
  }
}
