package de.wasenweg.alfred.thumbnails;

import de.wasenweg.alfred.comics.Comic;
import de.wasenweg.alfred.scanner.NoImagesException;
import de.wasenweg.alfred.util.ZipReaderUtil;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

@Service
public class ThumbnailService {

  @Autowired
  private ThumbnailRepository thumbnailRepository;

  /*
   * Finds the thumbnail in the given zip file.
   */
  public void setComic(final ZipFile file, final Comic comic) throws NoImagesException {

    try {
      final List<ZipEntry> sortedEntries = ZipReaderUtil.getImages(file);

      if (sortedEntries.size() > 0) {
        final ObjectId comicId = new ObjectId(comic.getId());
        final Thumbnail thumbnail = this.thumbnailRepository.findByComicId(comicId).orElse(
            Thumbnail.builder().comicId(comicId).build());

        thumbnail.setThumbnail(ThumbnailUtils.get(file.getInputStream(sortedEntries.get(0))).toByteArray());
        this.thumbnailRepository.save(thumbnail);
      } else {
        throw new NoImagesException();
      }
    } catch (final Exception exception) {
      throw new NoImagesException(exception);
    }
  }
}
