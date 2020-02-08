package de.wasenweg.alfred.comics;

import de.wasenweg.alfred.progress.ProgressService;
import de.wasenweg.alfred.scanner.ApiMetaDataService;
import de.wasenweg.alfred.scanner.FileMetaDataService;
import de.wasenweg.alfred.scanner.ScannerIssue;
import de.wasenweg.alfred.scanner.ScannerService;
import de.wasenweg.alfred.thumbnails.ThumbnailRepository;
import de.wasenweg.alfred.thumbnails.ThumbnailService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;
import java.util.zip.ZipFile;

import static java.lang.String.format;

@Service
@Slf4j
public class ComicService {

  @Autowired
  private ThumbnailRepository thumbnailRepository;

  @Autowired
  private ComicQueryRepositoryImpl queryRepository;

  @Autowired
  private ComicRepository comicRepository;

  @Autowired
  private ThumbnailService thumbnailService;

  @Autowired
  private ScannerService scannerService;

  @Autowired
  private FileMetaDataService fileMetaDataService;

  @Autowired
  private ApiMetaDataService apiMetaDataService;

  @Autowired
  private ProgressService progressService;

  public List<Comic> findAll() {
    return this.comicRepository.findAll();
  }

  public Optional<Comic> findById(final String userId, final String comicId) {
    return this.queryRepository.findById(userId, comicId);
  }

  public Comic update(final Comic comic) {
    this.comicRepository.save(comic);
    this.fileMetaDataService.write(comic);
    this.scannerService.processComic(comic);
    return comic;
  }

  public Comic updateProgress(final Comic comic) {
    this.comicRepository.save(comic);
    return comic;
  }

  public Comic scrape(final Comic comic) throws ResourceNotFoundException {
    final List<ScannerIssue> issues = this.apiMetaDataService.set(comic);
    if (issues.size() > 0) {
      throw new ResourceNotFoundException("Error while querying ComicVine.");
    }
    this.comicRepository.save(comic);
    this.fileMetaDataService.write(comic);
    return comic;
  }

  public List<Comic> findAllLastReadPerVolume(final String userId) {
    return this.queryRepository.findAllLastReadPerVolume(userId);
  }

  public List<Comic> findAllByOrderByPublisherAscSeriesAscVolumeAscPositionAsc() {
    return this.comicRepository.findAllByOrderByPublisherAscSeriesAscVolumeAscPositionAsc();
  }

  public Optional<Comic> findLastReadForVolume(
      final String userId, final String publisher, final String series, final String volume) {
    return this.queryRepository.findLastReadForVolume(userId, publisher, series, volume);
  }

  public List<Comic> findAllByPublisherAndSeriesAndVolumeOrderByPosition(
      final String userId, final String publisher, final String series, final String volume) {
    return this.queryRepository.findAllByPublisherAndSeriesAndVolumeOrderByPosition(
        userId, publisher, series, volume);
  }

  public Optional<Comic> markAsRead(final Comic comic, final String userId) {
    return Optional.ofNullable(this.progressService.updateComic(userId, comic, true));
  }

  public Optional<Comic> markAsUnread(final Comic comic, final String userId) {
    return Optional.ofNullable(this.progressService.updateComic(userId, comic, false));
  }

  public void deleteComics() {
    this.comicRepository.deleteAll();
    this.thumbnailRepository.deleteAll();
  }

  public Optional<Comic> deletePage(final String comicId, final String filePath) {
    final Optional<Comic> maybeComic = this.comicRepository.findById(comicId);
    if (maybeComic.isPresent()) {
      final Comic comic = maybeComic.get();
      final Path zipFilePath = Paths.get(comic.getPath());
      try (final FileSystem fs = FileSystems.newFileSystem(zipFilePath, null)) {
        final Path source = fs.getPath(filePath);
        if (Files.exists(source)) {
          Files.delete(source);
        }
        fs.close();
        log.info(format("Deleted file %s in comic %s", filePath, comic.getPath()));
        final ZipFile zipFile = new ZipFile(comic.getPath());
        this.thumbnailService.read(zipFile, comic);
        zipFile.close();
      } catch (final IOException exception) {
        exception.printStackTrace();
      }
    }
    return maybeComic;
  }

  public void bundle() {
    this.scannerService.associateVolumes();
  }
}
