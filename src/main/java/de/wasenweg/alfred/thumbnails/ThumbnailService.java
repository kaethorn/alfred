package de.wasenweg.alfred.thumbnails;

import de.wasenweg.alfred.comics.Comic;
import de.wasenweg.alfred.scanner.NoImagesException;
import de.wasenweg.alfred.thumbnails.Thumbnail.ThumbnailType;
import de.wasenweg.alfred.util.ZipReaderUtil;
import lombok.RequiredArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.FileSystem;
import java.nio.file.FileSystemNotFoundException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ThumbnailService {

  private final ThumbnailRepository thumbnailRepository;

  public Optional<Thumbnail> findFrontCoverByComicId(final String comicId) {
    return this.thumbnailRepository.findByComicIdAndType(new ObjectId(comicId), ThumbnailType.FRONT_COVER);
  }

  public Optional<Thumbnail> findBackCoverByComicId(final String comicId) {
    return this.thumbnailRepository.findByComicIdAndType(new ObjectId(comicId), ThumbnailType.BACK_COVER);
  }

  /*
   * Saves the front- and back cover thumbnails.
   */
  @Transactional
  public void read(final Comic comic) throws NoImagesException, IOException {
    try (FileSystem fs = FileSystems.newFileSystem(Paths.get(comic.getPath()), (ClassLoader) null)) {
      final List<Path> sortedEntries = ZipReaderUtil.getImages(fs);

      if (!sortedEntries.isEmpty()) {
        this.saveThumbnail(comic, sortedEntries.get(0), ThumbnailType.FRONT_COVER);
        this.saveThumbnail(comic, sortedEntries.get(sortedEntries.size() - 1), ThumbnailType.BACK_COVER);
      }
    } catch (final FileSystemNotFoundException | IOException exception) {
      throw new NoThumbnailsException(exception);
    }
  }

  private void saveThumbnail(final Comic comic, final Path file, final ThumbnailType type)
      throws IOException {

    final ObjectId comicId = new ObjectId(comic.getId());
    final Thumbnail thumbnail = this.thumbnailRepository
        .findByComicIdAndType(comicId, type)
        .orElse(Thumbnail.builder()
            .comicId(comicId)
            .type(type)
            .path(file.toString())
            .build());

    try (InputStream thumbnailStream = Files.newInputStream(file)) {
      thumbnail.setImage(ThumbnailUtil.get(thumbnailStream).toByteArray());
      this.thumbnailRepository.save(thumbnail);
    }
  }
}
