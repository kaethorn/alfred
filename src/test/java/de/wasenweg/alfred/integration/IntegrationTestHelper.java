package de.wasenweg.alfred.integration;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import de.wasenweg.alfred.comics.Comic;
import de.wasenweg.alfred.settings.Setting;
import de.wasenweg.alfred.settings.SettingRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;
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

import static java.lang.String.format;
import static org.springframework.http.MediaType.TEXT_EVENT_STREAM;

@Component
@Slf4j
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class IntegrationTestHelper {

  private final SettingRepository settingsRepository;

  public Flux<String> triggerScan(final int port) {
    return WebClient.create("http://localhost:" + port + "/api")
        .get().uri("/scan-progress").accept(TEXT_EVENT_STREAM)
        .retrieve().bodyToFlux(new ParameterizedTypeReference<String>() {
        });
  }

  public Boolean zipContainsFile(final String zipPath, final String filePath) {
    try (final FileSystem fs = FileSystems.newFileSystem(Paths.get(zipPath), null)) {
      return Files.exists(fs.getPath(filePath)) ? true : false;
    } catch (final IOException | SecurityException exception) {
      log.error("Failed to check zip file.", exception);
      return false;
    }
  }

  public Document parseComicInfo(final String zipPath) {
    try (final FileSystem fs = FileSystems.newFileSystem(Paths.get(zipPath), null)) {
      final DocumentBuilder docBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
      try (final InputStream xmlStream = Files.newInputStream(fs.getPath("/ComicInfo.xml"))) {
        final Document document = docBuilder.parse(xmlStream);
        return document;
      }
    } catch (final IOException | InvalidPathException | ParserConfigurationException | SAXException exception) {
      log.error("Failed to parse meta data.", exception);
      return null;
    }
  }

  public String getText(final Document document, final String name) {
    return document.getDocumentElement().getElementsByTagName(name).item(0).getTextContent();
  }

  public void setComicsPath(final String comicsPath, final File temp) {
    this.copyResources(temp, comicsPath);
    final Setting comicsPathSetting = this.settingsRepository.findByKey("comics.path").get();
    comicsPathSetting.setValue(temp.getAbsolutePath());
    this.settingsRepository.save(comicsPathSetting);
  }

  public String comicToJson(final Comic comic) {
    try {
      return new ObjectMapper().writeValueAsString(comic);
    } catch (final JsonProcessingException exception) {
      log.error("Failed to convert to JSON.", exception);
      return "";
    }
  }

  private void copyResources(final File temp, final String resourcePath) {
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
}
