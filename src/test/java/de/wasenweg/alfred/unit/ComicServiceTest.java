package de.wasenweg.alfred.unit;

import de.wasenweg.alfred.TestUtil;
import de.wasenweg.alfred.comics.Comic;
import de.wasenweg.alfred.comics.ComicQueryRepositoryImpl;
import de.wasenweg.alfred.comics.ComicRepository;
import de.wasenweg.alfred.comics.ComicService;
import de.wasenweg.alfred.progress.ProgressService;
import de.wasenweg.alfred.scanner.FileMetaDataService;
import de.wasenweg.alfred.scanner.NoImagesException;
import de.wasenweg.alfred.scanner.ScannerService;
import de.wasenweg.alfred.thumbnails.ThumbnailRepository;
import de.wasenweg.alfred.thumbnails.ThumbnailService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.File;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ComicServiceTest {

  @TempDir
  public transient File testBed;

  @Mock
  private transient ThumbnailService thumbnailService;

  @Mock
  private transient FileMetaDataService fileMetaDataService;

  @Mock
  private transient ComicRepository comicRepository;

  @Mock
  private transient ScannerService scannerService;

  @Mock
  private transient ThumbnailRepository thumbnailRepository;

  @Mock
  private transient ComicQueryRepositoryImpl queryRepository;

  @Mock
  private transient ProgressService progressService;

  @Spy
  @InjectMocks
  private transient ComicService comicService;

  @Test
  public void findById() throws Exception {
    final Comic comic = new Comic();
    when(this.queryRepository.findById("foo@bar.com", "239"))
        .thenReturn(Optional.of(comic));

    assertThat(this.comicService.findById("foo@bar.com", "239").get()).isEqualTo(comic);
    verify(this.queryRepository).findById("foo@bar.com", "239");
  }

  @Test
  public void updateProgress() throws Exception {
    final Comic comic = new Comic();
    when(this.progressService.updateComic("foo@bar.com", comic, false)).thenReturn(comic);
    doReturn(Optional.of(comic)).when(this.comicService).findById(eq("foo@bar.com"), any());

    assertThat(this.comicService.updateProgress(comic, "foo@bar.com")).isEqualTo(comic);
    verify(this.progressService).updateComic("foo@bar.com", comic, false);
  }

  @Test
  public void markAsRead() throws Exception {
    final Comic comic = new Comic();
    when(this.progressService.updateComic("foo@bar.com", comic, true)).thenReturn(comic);
    doReturn(Optional.of(comic)).when(this.comicService).findById(eq("foo@bar.com"), any());

    assertThat(this.comicService.markAsRead(comic, "foo@bar.com").get()).isEqualTo(comic);
    verify(this.progressService).updateComic("foo@bar.com", comic, true);
  }

  @Test
  public void markAsUnread() throws Exception {
    final Comic comic = new Comic();
    when(this.progressService.updateComic("foo@bar.com", comic, false)).thenReturn(comic);
    doReturn(Optional.of(comic)).when(this.comicService).findById(eq("foo@bar.com"), any());

    assertThat(this.comicService.markAsUnread(comic, "foo@bar.com").get()).isEqualTo(comic);
    verify(this.progressService).updateComic("foo@bar.com", comic, false);
  }

  @Test
  public void deleteComics() throws Exception {
    this.comicService.deleteComics();

    verify(this.comicRepository).deleteAll();
    verify(this.thumbnailRepository).deleteAll();
  }

  @Test
  public void bundle() throws Exception {
    this.comicService.bundle();

    verify(this.scannerService).associateVolumes();
  }

  @Test
  public void deletePageWithoutAComic() throws Exception {
    when(this.comicRepository.findById("5")).thenReturn(Optional.ofNullable(null));

    assertThat(this.comicService.deletePage("5", "/2.png").isPresent()).isFalse();
  }

  @Test
  public void deletePageWithInexistantPage() throws Exception {
    TestUtil.copyResources(this.testBed, "src/test/resources/fixtures/simple");
    final String comicPath = this.testBed.getAbsolutePath() + "/Batman 402 (1940).cbz";
    final Comic comic = new Comic();
    comic.setPath(comicPath);
    when(this.comicRepository.findById("5")).thenReturn(Optional.of(comic));

    assertThat(this.comicService.deletePage("5", "/4.png").get()).isEqualTo(comic);
    verify(this.thumbnailService).read(comic);
    verify(this.fileMetaDataService).parseFiles(comic);
    verify(this.comicRepository).save(comic);
  }

  @Test
  public void deletePageWithNoImagesLeft() throws Exception {
    TestUtil.copyResources(this.testBed, "src/test/resources/fixtures/full");
    final String comicPath = this.testBed.getAbsolutePath() + "/Batgirl 000 (2011).cbz";
    final Comic comic = new Comic();
    comic.setPath(comicPath);
    when(this.comicRepository.findById("5")).thenReturn(Optional.of(comic));
    doThrow(new NoImagesException()).when(this.thumbnailService).read(any());

    assertThat(this.comicService.deletePage("5", "/1.png").get()).isEqualTo(comic);
    verify(this.thumbnailService).read(comic);
    verify(this.fileMetaDataService, times(0)).parseFiles(comic);
    verify(this.comicRepository, times(0)).save(comic);
  }

  @Test
  public void deletePageInvalidTarget() throws Exception {
    TestUtil.copyResources(this.testBed, "src/test/resources/fixtures/simple");
    final String comicPath = this.testBed.getAbsolutePath() + "/Batman 402 (1940).cbz";
    final Comic comic = new Comic();
    comic.setPath(comicPath);
    when(this.comicRepository.findById("5")).thenReturn(Optional.of(comic));

    assertThat(this.comicService.deletePage("5", "/").get()).isEqualTo(comic);
    verify(this.thumbnailService, times(0)).read(comic);
    verify(this.fileMetaDataService, times(0)).parseFiles(comic);
    verify(this.comicRepository, times(0)).save(comic);
  }
}
