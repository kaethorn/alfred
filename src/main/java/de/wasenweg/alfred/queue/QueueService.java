package de.wasenweg.alfred.queue;

import de.wasenweg.alfred.comics.Comic;
import de.wasenweg.alfred.comics.ComicQueryRepositoryImpl;
import de.wasenweg.alfred.scanner.ScannerService;
import de.wasenweg.alfred.util.ZipReaderUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.DirectoryNotEmptyException;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Stream;

import static java.lang.String.format;

@Service
@Slf4j
@RequiredArgsConstructor
public class QueueService {

  private final ComicQueryRepositoryImpl comicQueryRepository;
  private final ScannerService scannerService;

  public List<Comic> get() {
    return this.comicQueryRepository.findAllWithErrors();
  }

  public List<Comic> getValid() {
    return this.comicQueryRepository.findAllWithoutErrors();
  }

  public Comic flatten(final Comic comic) throws IOException {
    try (final FileSystem fs = FileSystems.newFileSystem(Paths.get(comic.getPath()), null)) {
      final List<Path> files = ZipReaderUtil.getEntries(fs);
      files.stream()
          .filter(Files::isDirectory)
          .flatMap(directory -> {
            try {
              return Files.walk(directory).filter(Files::isRegularFile);
            } catch (final IOException exception) {
              return Stream.empty();
            }
          }).forEach(file -> {
            try {
              final String fileName = file.getFileName().toString();
              final String filePathInZip = file.toString();
              final Path source = fs.getPath(filePathInZip);
              final Path target = fs.getPath("/", fileName);
              log.debug(format("Moving %s to %s.", source, target));
              Files.move(source, target);
            } catch (final IOException exception) {
              exception.printStackTrace();
            }
          });

      // Remove empty directories
      files.stream()
          .filter(Files::isDirectory)
          .sorted(Comparator.comparing(directory -> -directory.getNameCount()))
          .forEach(directory -> {
            try {
              log.debug(format("Removing directory %s.", directory));
              Files.delete(directory);
            } catch (final DirectoryNotEmptyException exception) {
              log.debug(format("Directory %s not empty.", directory));
            } catch (final IOException exception) {
              log.debug(format("Directory %s could not be deleted.", directory));
            }
          });
    }

    log.info(format("Flattened %s.", comic.getPath()));
    return this.scannerService.processComic(comic);
  }
}