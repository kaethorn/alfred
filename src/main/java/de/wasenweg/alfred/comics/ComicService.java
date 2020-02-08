package de.wasenweg.alfred.comics;

import de.wasenweg.alfred.thumbnails.ThumbnailService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;
import java.util.zip.ZipFile;

import static java.lang.String.format;

@Service
@Slf4j
public class ComicService {

  @Autowired
  private ComicRepository comicRepository;

  @Autowired
  private ThumbnailService thumbnailService;

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
}
