package de.wasenweg.alfred.util;

import java.io.IOException;
import java.nio.file.FileSystem;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public class ZipReaderUtil {

  /**
   * Returns a sorted list of entries in the given zip file.
   *
   * @param fs An already opened file system.
   * @return List of paths to zip entries.
   */
  public static List<Path> getEntries(final FileSystem fs) {
    return StreamSupport.stream(fs.getRootDirectories().spliterator(), true)
        .flatMap(rootDirectory -> {
          try {
            return Files.walk(rootDirectory);
          } catch (final IOException e) {
            return Stream.empty();
          }
        })
        .sorted()
        .collect(Collectors.toList());
  }

  /**
   * Returns a sorted list of images in the given zip file.
   *
   * @param path The path to the zip file to search through.
   * @return List of image zip entries.
   */
  public static List<Path> getImages(final FileSystem fs) {
    return getEntries(fs).stream().filter(ZipReaderUtil::isImage).collect(Collectors.toList());
  }

  public static Boolean isImage(final Path path) {
    return Files.isRegularFile(path) && path.toString().matches(".*(png|jpg)$");
  }
}
