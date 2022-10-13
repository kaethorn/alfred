package de.wasenweg.alfred.unit;

import de.wasenweg.alfred.TestUtil;
import de.wasenweg.alfred.util.ZipReaderUtil;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.File;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Paths;

import static org.assertj.core.api.Assertions.assertThat;

class ZipReaderUtilTest {

  @TempDir
  public transient File testBed;

  @Test
  void getEntries() throws Exception {
    TestUtil.copyResources(this.testBed, "src/test/resources/fixtures/simple");
    try (FileSystem fs = FileSystems.newFileSystem(
            Paths.get(this.testBed.getAbsolutePath() + "/Batman 402 (1940).cbz"),
            (ClassLoader) null)) {
      assertThat(ZipReaderUtil.getEntries(fs).size()).isEqualTo(4);
    }
  }

  @Test
  void getEntriesWithCorruptArchive() throws Exception {
    TestUtil.copyResources(this.testBed, "src/test/resources/fixtures/special_cases/corrupt_archive");
    try (FileSystem fs = FileSystems.newFileSystem(
            Paths.get(this.testBed.getAbsolutePath() + "/Batman 402 (1940).cbz"),
            (ClassLoader) null)) {
      assertThat(ZipReaderUtil.getEntries(fs).size()).isEqualTo(0);
    }
  }

  @Test
  void isImageWithoutImages() {
    TestUtil.copyResources(this.testBed, "src/test/resources/fixtures/simple");

    assertThat(ZipReaderUtil.isImage(Paths.get(this.testBed.getAbsolutePath()))).isFalse();
    assertThat(ZipReaderUtil.isImage(Paths.get(this.testBed.getAbsolutePath() + "/Batman 402 (1940).cbz"))).isFalse();
  }
}
