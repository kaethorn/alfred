package de.wasenweg.alfred.unit;

import de.wasenweg.alfred.TestUtil;
import de.wasenweg.alfred.comics.Comic;
import de.wasenweg.alfred.fixtures.ComicFixtures;
import de.wasenweg.alfred.thumbnails.NoThumbnailsException;
import de.wasenweg.alfred.thumbnails.ThumbnailRepository;
import de.wasenweg.alfred.thumbnails.ThumbnailService;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.File;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class ThumbnailServiceTest {

  @TempDir
  public transient File testBed;

  @Mock
  private transient ThumbnailRepository thumbnailRepository;

  @InjectMocks
  private transient ThumbnailService thumbnailService;

  @Test
  void read() throws Exception {
    TestUtil.copyResources(this.testBed, "src/test/resources/fixtures/simple");
    final String comicPath = this.testBed.getAbsolutePath() + "/Batman 402 (1940).cbz";
    final Comic comic = ComicFixtures.COMIC_V1_1.toBuilder().path(comicPath).id(ObjectId.get().toString()).build();
    this.thumbnailService.read(comic);

    verify(this.thumbnailRepository, times(2)).save(any());
  }

  @Test
  void readWithoutImages() throws Exception {
    TestUtil.copyResources(this.testBed, "src/test/resources/fixtures/special_cases/no_images");
    final String comicPath = this.testBed.getAbsolutePath() + "/Batman 402 (1940).cbz";
    final Comic comic = ComicFixtures.COMIC_V1_1.toBuilder().path(comicPath).id(ObjectId.get().toString()).build();
    this.thumbnailService.read(comic);

    verify(this.thumbnailRepository, times(0)).save(any());
  }

  @Test
  void readWithCorruptFiles() {
    assertThrows(NoThumbnailsException.class, () -> {
      this.thumbnailService.read(
          ComicFixtures.COMIC_V1_1.toBuilder()
              .path(this.testBed.getAbsolutePath() + "/none.cbz")
              .id(ObjectId.get().toString())
              .build()
      );
    });
  }
}
