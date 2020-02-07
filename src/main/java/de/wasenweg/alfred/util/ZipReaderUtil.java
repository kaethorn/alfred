package de.wasenweg.alfred.util;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

public class ZipReaderUtil {

  /**
   * Returns a sorted list of entries in the given zip file.
   * @param file The zip file to search through.
   * @return List of zip entries.
   */
  public static List<ZipEntry> getEntries(final ZipFile file) throws Exception {
    final Comparator<ZipEntry> byName =
        (ze1, ze2) -> ze1.getName().compareTo(ze2.getName());

    return file.stream()
        .sorted(byName)
        .collect(Collectors.toList());
  }

  /**
   * Returns a sorted list of images in the given zip file.
   * @param file The zip file to search through.
   * @return List of image zip entries.
   */
  public static List<ZipEntry> getImages(final ZipFile file) throws Exception {
    return getEntries(file).stream()
        .filter(ZipReaderUtil::isImage)
        .collect(Collectors.toList());
  }

  public static Boolean isImage(final ZipEntry zipEntry) {
    return zipEntry.getName().matches(".*(png|jpg)$");
  }
}
