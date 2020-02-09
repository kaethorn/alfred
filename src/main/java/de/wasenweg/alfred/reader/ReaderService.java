package de.wasenweg.alfred.reader;

import de.wasenweg.alfred.comics.Comic;
import de.wasenweg.alfred.comics.ComicRepository;
import de.wasenweg.alfred.progress.Progress;
import de.wasenweg.alfred.progress.ProgressRepository;
import de.wasenweg.alfred.util.ZipReaderUtil;
import lombok.extern.slf4j.Slf4j;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.net.URLConnection;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import static java.lang.String.format;

@Slf4j
@Service
public class ReaderService {

  @Autowired
  private ComicRepository comicRepository;

  @Autowired
  private ProgressRepository progressRepository;

  /**
   * Returns the page of the given comic.
   *
   * @param id         The ID of the comic to open.
   * @param page       The page number from which to start.
   * @param markAsRead Whether to marks the page as read.
   * @param userId     The current user's ID.
   * @return The extracted page.
   */
  public ResponseEntity<StreamingResponseBody> read(final String id, final Short page, final boolean markAsRead,
      final String userId) {

    final Optional<Comic> comicQuery = this.comicRepository.findById(id);
    final Comic comic = comicQuery.orElseThrow(ResourceNotFoundException::new);

    log.debug(format("Reading page %s (page count %s) of %s, files: [%s]", page, comic.getPageCount(), comic.toString(),
        String.join(",", comic.getFiles())));

    if (markAsRead) {
      this.setReadState(comic, page, userId);
    }

    try {
      final ZipFile file = new ZipFile(comic.getPath());
      final ComicPage comicPage = this.extractPage(file, page);
      final StreamingResponseBody responseBody = outputStream -> {
        int numberOfBytesToWrite;
        final byte[] data = new byte[1024];
        while ((numberOfBytesToWrite = comicPage.getStream().read(data, 0, data.length)) != -1) {
          outputStream.write(data, 0, numberOfBytesToWrite);
        }
        comicPage.getStream().close();
        file.close();
      };

      final MediaType mediaType = MediaType.parseMediaType(comicPage.getType());

      return ResponseEntity.ok().header(HttpHeaders.CONTENT_DISPOSITION, "inline;filename=" + comicPage.getName())
          .contentLength(comicPage.getSize()).contentType(mediaType).body(responseBody);
    } catch (final Exception exception) {
      log.error(exception.getLocalizedMessage());
      throw new ResourceNotFoundException(exception.getLocalizedMessage());
    }
  }

  /**
   * Downloads the CBZ archive designated by the given comic book ID.
   */
  public ResponseEntity<StreamingResponseBody> download(final String id) {

    final Optional<Comic> comicQuery = this.comicRepository.findById(id);
    final Comic comic = comicQuery.orElseThrow(ResourceNotFoundException::new);
    final File file = new File(comic.getPath());
    final InputStream inputStream;
    try {
      inputStream = new FileInputStream(file);
    } catch (final FileNotFoundException exception) {
      throw new ResourceNotFoundException();
    }

    final StreamingResponseBody responseBody = outputStream -> {
      int numberOfBytesToWrite;
      final byte[] data = new byte[1024];
      while ((numberOfBytesToWrite = inputStream.read(data, 0, data.length)) != -1) {
        outputStream.write(data, 0, numberOfBytesToWrite);
      }

      inputStream.close();
    };

    return ResponseEntity
        .ok()
        .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + file.getName())
        .contentType(MediaType.APPLICATION_OCTET_STREAM)
        .body(responseBody);
  }

  private ComicPage extractPage(final ZipFile file, final Short page) {
    final ComicPage result = new ComicPage();
    try {
      final List<ZipEntry> sortedEntries = ZipReaderUtil.getImages(file);
      final ZipEntry entry = sortedEntries.get(page);
      final String fileName = entry.getName();
      result.setStream(file.getInputStream(entry));
      result.setSize(entry.getSize());
      result.setType(URLConnection.guessContentTypeFromName(fileName));
      result.setName(fileName);
    } catch (final Exception e) {
      e.printStackTrace();
    }

    log.debug(format("Extracted page number %s: %s", page, result.toString()));

    return result;
  }

  private void setReadState(final Comic comic, final Short page, final String userId) {
    final ObjectId comicId = new ObjectId(comic.getId());
    final Progress progress = this.progressRepository
        .findByUserIdAndComicId(userId, comicId)
        .orElse(Progress.builder().comicId(comicId).userId(userId).build());

    progress.setCurrentPage(page);
    progress.setLastRead(new Date());

    if (page == comic.getPageCount() - 1) {
      progress.setRead(true);
    }

    this.progressRepository.save(progress);
  }
}
