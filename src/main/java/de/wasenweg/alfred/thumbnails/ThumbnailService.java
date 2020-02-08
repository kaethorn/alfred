package de.wasenweg.alfred.thumbnails;

import de.wasenweg.alfred.comics.Comic;
import de.wasenweg.alfred.scanner.NoImagesException;
import de.wasenweg.alfred.thumbnails.Thumbnail.ThumbnailType;
import de.wasenweg.alfred.util.ZipReaderUtil;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

@Service
public class ThumbnailService {

  @Autowired
  private ThumbnailRepository thumbnailRepository;

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
  public void read(final ZipFile file, final Comic comic) throws NoImagesException {

    try {
      final List<ZipEntry> sortedEntries = ZipReaderUtil.getImages(file);

      if (sortedEntries.size() > 0) {
        final ObjectId comicId = new ObjectId(comic.getId());
        this.setThumbnail(comicId, file, sortedEntries.get(0), ThumbnailType.FRONT_COVER);
        this.setThumbnail(comicId, file, sortedEntries.get(sortedEntries.size() - 1), ThumbnailType.BACK_COVER);
      } else {
        throw new NoImagesException();
      }
    } catch (final Exception exception) {
      throw new NoImagesException(exception);
    }
  }

  private void setThumbnail(final ObjectId comicId, final ZipFile file, final ZipEntry entry, final ThumbnailType type)
      throws IOException {

    final Thumbnail thumbnail = this.thumbnailRepository
        .findByComicIdAndType(comicId, type)
        .orElse(Thumbnail.builder()
            .comicId(comicId)
            .type(type)
            .path(entry.getName())
            .build());

    thumbnail.setThumbnail(ThumbnailUtils.get(file.getInputStream(entry)).toByteArray());
    this.thumbnailRepository.save(thumbnail);
  }
}
