package de.wasenweg.alfred.integration;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import de.wasenweg.alfred.comics.Comic;
import de.wasenweg.alfred.settings.Setting;
import de.wasenweg.alfred.settings.SettingRepository;

import org.junit.rules.TemporaryFolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.w3c.dom.Document;

import reactor.core.publisher.Flux;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import static org.springframework.http.MediaType.TEXT_EVENT_STREAM;

@Component
public class IntegrationTestHelper {

  @Autowired
  private SettingRepository settingsRepository;

  public Flux<String> triggerScan(final int port) {
    return WebClient
        .create("http://localhost:" + port + "/api")
        .get()
        .uri("/scan-progress")
        .accept(TEXT_EVENT_STREAM)
        .retrieve()
        .bodyToFlux(new ParameterizedTypeReference<String>() { });
  }

  public Boolean zipContainsFile(final String zipPath, final String filePath) {
    try {
      final ZipFile zipFile = new ZipFile(zipPath);
      final Boolean found = zipFile.stream()
          .filter(entry -> entry.getName().equals(filePath))
          .collect(Collectors.toList()).size() > 0;
      zipFile.close();
      return found;
    } catch (final IOException exception) {
      exception.printStackTrace();
      return false;
    }
  }

  public Document parseComicInfo(final String zipPath) {
    try {
      final DocumentBuilder docBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
      final ZipFile zipFile = new ZipFile(zipPath);
      final ZipEntry comicInfo = zipFile.stream()
          .filter(entry -> entry.getName().equals("ComicInfo.xml"))
          .collect(Collectors.toList()).get(0);
      final Document document = docBuilder.parse(zipFile.getInputStream(comicInfo));
      zipFile.close();
      return document;
    } catch (final Exception exception) {
      exception.printStackTrace();
      return null;
    }
  }

  public String getText(final Document document, final String name) {
    return document.getDocumentElement().getElementsByTagName(name).item(0).getTextContent();
  }

  public void setComicsPath(final String comicsPath, final TemporaryFolder temp) {
    this.copyResources(temp, comicsPath);
    final Setting comicsPathSetting = this.settingsRepository.findByKey("comics.path").get();
    comicsPathSetting.setValue(temp.getRoot().getAbsolutePath());
    this.settingsRepository.save(comicsPathSetting);
  }

  public String comicToJson(final Comic comic) {
    try {
      return new ObjectMapper().writeValueAsString(comic);
    } catch (final JsonProcessingException exception) {
      exception.printStackTrace();
      return "";
    }
  }

  private void copyResources(final TemporaryFolder temp, final String resourcePath) {
    final Path source = Paths.get(resourcePath);
    try {
      Files.walk(source).forEach(file -> {
        try {
          Files.copy(file, temp.getRoot().toPath().resolve(source.relativize(file)), StandardCopyOption.REPLACE_EXISTING);
        } catch (final IOException exception) {
          exception.printStackTrace();
        }
      });
    } catch (final IOException exception) {
      exception.printStackTrace();
    }
  }
}
