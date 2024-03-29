package de.wasenweg.alfred.queue;

import de.wasenweg.alfred.comics.Comic;
import de.wasenweg.alfred.comics.ComicQueryRepositoryImpl;
import de.wasenweg.alfred.scanner.ScannerService;
import de.wasenweg.alfred.util.ZipReaderUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Comparator;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class QueueService {

  private final ComicQueryRepositoryImpl comicQueryRepository;
  private final ScannerService scannerService;

  public List<Comic> get() {
    return this.comicQueryRepository.findAllWithErrors();
  }

  public List<Comic> getValid(final String publisher, final String series, final String volume) {
    return this.comicQueryRepository.findAllWithoutErrors(publisher, series, volume);
  }

  public Comic flatten(final Comic comic) throws IOException {
    try (FileSystem fs = FileSystems.newFileSystem(Paths.get(comic.getPath()), (ClassLoader) null)) {
      final List<Path> files = ZipReaderUtil.getEntries(fs);
      files.stream()
          .filter(Files::isDirectory)
          .flatMap(directory -> files.stream()
              .filter(Files::isRegularFile)
              .filter(file -> file.startsWith(directory)))
          .forEach(file -> {
            try {
              final String fileName = file.getFileName().toString();
              final String filePathInZip = file.toString();
              final Path source = fs.getPath(filePathInZip);
              final Path target = fs.getPath("/", fileName);
              log.debug("Moving {} to {}.", source, target);
              Files.move(source, target);
            } catch (final IOException exception) {
              log.error("Error while flattening {}", comic, exception);
            }
          });

      // Remove empty directories
      files.stream()
          .filter(Files::isDirectory)
          .sorted(Comparator.comparing(directory -> -directory.getNameCount()))
          .forEach(directory -> {
            try {
              log.debug("Removing directory {}.", directory);
              Files.delete(directory);
            } catch (final IOException exception) {
              log.error("Directory {} could not be deleted.", directory, exception);
            }
          });
    }

    log.info("Flattened {}.", comic.getPath());
    return this.scannerService.processComic(comic);
  }
}
