package de.wasenweg.alfred.unit;

import de.wasenweg.alfred.TestUtil;
import de.wasenweg.alfred.comics.ComicRepository;
import de.wasenweg.alfred.fixtures.ComicFixtures;
import de.wasenweg.alfred.reader.ReaderService;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;

import java.io.File;
import java.util.ArrayList;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ReaderServiceTest {

  @TempDir
  public transient File testBed;

  @Mock
  private transient ComicRepository comicRepository;

  @InjectMocks
  private transient ReaderService readerService;

  @BeforeEach
  public void setUp() {
    TestUtil.copyResources(this.testBed, "src/test/resources/fixtures/simple");
  }

  @Test
  public void readWithError() throws Exception {
    this.prepareComicFile();

    assertThrows(ResourceNotFoundException.class, () -> this.readerService.read("1", 22, "f@b.com"));
  }

  @Test
  public void downloadWithError() throws Exception {
    when(this.comicRepository.findById(any())).thenReturn(Optional.of(ComicFixtures.COMIC_V1_1.toBuilder()
        .path("/foo/bar.cbz")
        .build()));

    assertThrows(ResourceNotFoundException.class, () -> {
      this.readerService.download("1").getBody().writeTo(null);
    });
  }

  private void prepareComicFile() {
    final String comicPath = this.testBed.getAbsolutePath() + "/Batman 402 (1940).cbz";
    when(this.comicRepository.findById(any())).thenReturn(Optional.of(ComicFixtures.COMIC_V1_1.toBuilder()
        .id(ObjectId.get().toString())
        .pageCount(3)
        .files(new ArrayList<>())
        .path(comicPath)
        .build()));
  }
}
