package de.wasenweg.alfred.scanner;

import de.wasenweg.alfred.comics.Comic;
import de.wasenweg.alfred.util.ZipReader;

import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

public class ThumbnailReader {

  public static void set(final ZipFile file, final Comic comic) throws NoImagesException {

    try {
      final List<ZipEntry> sortedEntries = ZipReader.getImages(file);

      if (sortedEntries.size() > 0) {
        comic.setThumbnail(Thumbnail.get(file.getInputStream(sortedEntries.get(0))).toByteArray());
      }
    } catch (final Exception exception) {
      throw new NoImagesException(exception);
    }
  }
}
