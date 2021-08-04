package de.wasenweg.alfred.util;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.FileSystem;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public final class ZipReaderUtil {

  private ZipReaderUtil() {
  }

  /**
   * Returns a sorted list of entries in the given zip file.
   *
   * @param fs An already opened file system.
   * @return List of paths to zip entries.
   */
  public static List<Path> getEntries(final FileSystem fs) {
    final Path rootDirectory = fs.getRootDirectories().iterator().next();
    try {
      return Files.walk(rootDirectory)
          .sorted()
          .filter(entry -> !entry.equals(rootDirectory))
          .collect(Collectors.toList());
    } catch (final UncheckedIOException | IOException exception) {
      return new ArrayList<>();
    }
  }

  /**
   * Returns a sorted list of images in the given zip file.
   *
   * @param fs The file system of the zip file to search through.
   * @return List of image zip entries.
   */
  public static List<Path> getImages(final FileSystem fs) {
    return getEntries(fs).stream().filter(ZipReaderUtil::isImage).collect(Collectors.toList());
  }

  public static Boolean isImage(final Path path) {
    return Files.isRegularFile(path) && path.toString().matches("(?i).*(png|jpg|jpeg)$");
  }
}
