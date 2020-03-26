package de.wasenweg.alfred;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import de.wasenweg.alfred.util.ZipReaderUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.web.reactive.function.client.WebClient;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;
import reactor.core.publisher.Flux;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static java.lang.String.format;
import static org.springframework.http.MediaType.TEXT_EVENT_STREAM;

@Slf4j
public final class TestUtil {

  private TestUtil() {
  }

  public static Flux<String> triggerScan(final int port) {
    return WebClient.create("http://localhost:" + port + "/api")
        .get().uri("/scan-progress").accept(TEXT_EVENT_STREAM)
        .retrieve().bodyToFlux(new ParameterizedTypeReference<String>() {
        });
  }

  public static void copyResources(final File temp, final String resourcePath) {
    final Path source = Paths.get(resourcePath);
    try {
      Files.walk(source).forEach(file -> {
        try {
          Files.copy(file, temp.toPath().resolve(source.relativize(file)), StandardCopyOption.REPLACE_EXISTING);
        } catch (final IOException exception) {
          log.error(format("Failed to copy file %s.", file), exception);
        }
      });
    } catch (final IOException exception) {
      log.error(format("Failed to copy directory %s.", resourcePath), exception);
    }
  }

  public static Boolean zipContainsFile(final String zipPath, final String filePath) {
    try (FileSystem fs = FileSystems.newFileSystem(Paths.get(zipPath), null)) {
      return Files.exists(fs.getPath(filePath)) ? true : false;
    } catch (final IOException | SecurityException exception) {
      log.error("Failed to check zip file.", exception);
      return false;
    }
  }

  public static List<String> listFiles(final String zipPath) {
    try (FileSystem fs = FileSystems.newFileSystem(Paths.get(zipPath), null)) {
      return ZipReaderUtil.getEntries(fs).stream().map(Path::toString).collect(Collectors.toList());
    } catch (final IOException | SecurityException exception) {
      log.error("Failed to read zip file.", exception);
      return new ArrayList<>();
    }
  }

  public static Document parseComicInfo(final String zipPath) {
    try (FileSystem fs = FileSystems.newFileSystem(Paths.get(zipPath), null)) {
      final DocumentBuilder docBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
      try (InputStream xmlStream = Files.newInputStream(fs.getPath("/ComicInfo.xml"))) {
        return docBuilder.parse(xmlStream);
      }
    } catch (final IOException | InvalidPathException | ParserConfigurationException | SAXException exception) {
      log.error("Failed to parse meta data.", exception);
      return null;
    }
  }

  public static String getText(final Document document, final String name) {
    return document.getDocumentElement().getElementsByTagName(name).item(0).getTextContent();
  }

  public static String toJson(final Object object) {
    try {
      return new ObjectMapper().writeValueAsString(object);
    } catch (final JsonProcessingException exception) {
      log.error("Failed to convert to JSON.", exception);
      return "";
    }
  }
}