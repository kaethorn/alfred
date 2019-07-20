package de.wasenweg.alfred.thumbnails;

import de.wasenweg.alfred.comics.Comic;
import de.wasenweg.alfred.scanner.NoImagesException;
import de.wasenweg.alfred.util.ZipReader;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

public class ThumbnailSetter {

  @Autowired
  private static ThumbnailRepository thumbnailRepository;

  /*
   * Finds the thumbnail in the given zip file.
   */
  public static void set(final ZipFile file, final Comic comic) throws NoImagesException {

    try {
      final List<ZipEntry> sortedEntries = ZipReader.getImages(file);

      if (sortedEntries.size() > 0) {
        final ObjectId comicId = new ObjectId(comic.getId());
        final Thumbnail thumbnail = thumbnailRepository.findByComicId(comicId).orElse(
            Thumbnail.builder().comicId(comicId).build());

        thumbnail.setThumbnail(ThumbnailReader.get(file.getInputStream(sortedEntries.get(0))).toByteArray());
        thumbnailRepository.save(thumbnail);
      } else {
        throw new NoImagesException(new Exception());
      }
    } catch (final Exception exception) {
      throw new NoImagesException(exception);
    }
  }
}
