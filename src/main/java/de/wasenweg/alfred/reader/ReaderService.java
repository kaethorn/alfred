package de.wasenweg.alfred.reader;

import de.wasenweg.alfred.comics.Comic;
import de.wasenweg.alfred.comics.ComicRepository;
import de.wasenweg.alfred.progress.Progress;
import de.wasenweg.alfred.progress.ProgressRepository;
import de.wasenweg.alfred.util.ZipReaderUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.bson.types.ObjectId;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.InvalidMediaTypeException;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import java.io.IOException;
import java.io.InputStream;
import java.net.URLConnection;
import java.nio.channels.FileChannel;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Date;
import java.util.Optional;

import static java.lang.String.format;

@Service
@Slf4j
@RequiredArgsConstructor
public class ReaderService {

  private final ComicRepository comicRepository;
  private final ProgressRepository progressRepository;

  /**
   * Returns the page of the given comic.
   *
   * @param id         The ID of the comic to open.
   * @param page       The page number from which to start.
   * @param markAsRead Whether to marks the page as read.
   * @param userId     The current user's ID.
   * @return The extracted page.
   */
  public ResponseEntity<StreamingResponseBody> read(final String id, final Integer page, final boolean markAsRead,
      final String userId) {

    final Optional<Comic> comicQuery = this.comicRepository.findById(id);
    final Comic comic = comicQuery.orElseThrow(ResourceNotFoundException::new);

    log.debug(format("Reading page %s (page count %s) of %s, files: [%s]", page, comic.getPageCount(), comic.getFiles(),
        String.join(",", comic.getFiles())));

    if (markAsRead) {
      this.setReadState(comic, page, userId);
    }

    try {
      // Instantiate FileSystem here without try-with-resource as it's being
      // closed manually in the streaming response body handler.
      final FileSystem fs = FileSystems.newFileSystem(Paths.get(comic.getPath()), null); // NOPMD
      final Path path = ZipReaderUtil.getImages(fs).get(page);
      final String fileName = path.toString();
      final long fileSize = FileChannel.open(path).size();
      final String fileType = URLConnection.guessContentTypeFromName(fileName);
      log.debug(format("Extracting page %s of type %s with size %s.", fileName, fileType, fileSize));

      final MediaType mediaType = MediaType.parseMediaType(fileType);
      return ResponseEntity.ok()
          .header(HttpHeaders.CONTENT_DISPOSITION, "inline;filename=" + fileName)
          .contentLength(fileSize)
          .contentType(mediaType)
          .body(outputStream -> {
            try (InputStream fileStream = Files.newInputStream(path)) {
              fileStream.transferTo(outputStream);
            } finally {
              fs.close();
            }
          });

    } catch (final IOException | SecurityException | InvalidMediaTypeException exception) {
      log.error(exception.getLocalizedMessage());
      throw new ResourceNotFoundException(exception.getLocalizedMessage(), exception);
    }
  }

  /**
   * Downloads the CBZ archive designated by the given comic book ID.
   */
  public ResponseEntity<StreamingResponseBody> download(final String id) {
    final Optional<Comic> comicQuery = this.comicRepository.findById(id);
    final Comic comic = comicQuery.orElseThrow(ResourceNotFoundException::new);

    return ResponseEntity
        .ok()
        .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + comic.getFileName())
        .contentType(MediaType.APPLICATION_OCTET_STREAM)
        .body(outputStream -> {
          try (InputStream inputStream = Files.newInputStream(Paths.get(comic.getPath()))) {
            inputStream.transferTo(outputStream);
          } catch (final NoSuchFileException exception) {
            throw new ResourceNotFoundException(format("Error while downloading %s", comic.toString()), exception);
          }
        });
  }

  private void setReadState(final Comic comic, final Integer page, final String userId) {
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
