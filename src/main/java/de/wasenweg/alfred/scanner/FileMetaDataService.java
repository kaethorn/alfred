package de.wasenweg.alfred.scanner;

import de.wasenweg.alfred.comics.Comic;
import de.wasenweg.alfred.util.ZipReaderUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.io.Writer;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.ProviderNotFoundException;
import java.nio.file.StandardOpenOption;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static java.lang.String.format;

@Slf4j
@Service
public class FileMetaDataService {

  private final transient List<ScannerIssue> scannerIssues = new ArrayList<>();

  private DocumentBuilder getDocumentBuilder() throws ParserConfigurationException {
    final DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();
    return docBuilderFactory.newDocumentBuilder();
  }

  private void writeStringElement(final Document document, final String elementName, final String value) {
    final NodeList element = document.getDocumentElement().getElementsByTagName(elementName);
    if (element.getLength() > 0) {
      element.item(0).setTextContent(value);
    } else {
      final Element newElement = document.createElement(elementName);
      newElement.setTextContent(value);
      document.getDocumentElement().appendChild(newElement);
    }
  }

  private String readStringElement(final Document document, final String elementName) {
    final NodeList element = document.getElementsByTagName(elementName);
    if (element.getLength() > 0) {
      return element.item(0).getTextContent();
    } else {
      return "";
    }
  }

  private Integer readIntegerElement(final Document document, final String elementName) {
    final String value = this.readStringElement(document, elementName);
    try {
      return Integer.parseInt(value);
    } catch (final NumberFormatException exception) {
      final ScannerIssue parsingEvent = ScannerIssue.builder()
          .message("Couldn't read " + elementName + " value of '" + value + "'. Falling back to '0'")
          .severity(ScannerIssue.Severity.WARNING)
          .build();
      log.warn(parsingEvent.getMessage());
      this.scannerIssues.add(parsingEvent);
      return 0;
    }
  }

  private String mapPosition(final String number) {
    try {
      return Comic.mapPosition(number);
    } catch (final InvalidIssueNumberException exception) {
      log.warn(exception.getMessage(), exception);
      this.scannerIssues.add(ScannerIssue.builder()
          .message(exception.getMessage())
          .severity(ScannerIssue.Severity.WARNING)
          .build());
      return new DecimalFormat("0000.0").format(BigDecimal.ZERO);
    }
  }

  private String readIntegerValue(final Integer value) {
    if (value == null) {
      return null;
    }
    return value.toString();
  }

  private String marshal(final Comic comic)
      throws SAXException, IOException, TransformerException, ParserConfigurationException {

    final Document document = this.getOrCreateDocument(comic);

    this.writeStringElement(document, "Title", comic.getTitle());
    this.writeStringElement(document, "Series", comic.getSeries());
    this.writeStringElement(document, "Publisher", comic.getPublisher());
    this.writeStringElement(document, "Number", comic.getNumber());
    this.writeStringElement(document, "Volume", comic.getVolume());
    this.writeStringElement(document, "Summary", comic.getSummary());
    this.writeStringElement(document, "Notes", comic.getNotes());
    this.writeStringElement(document, "Year", this.readIntegerValue(comic.getYear()));
    this.writeStringElement(document, "Month", this.readIntegerValue(comic.getMonth()));
    this.writeStringElement(document, "Writer", comic.getWriter());
    this.writeStringElement(document, "Penciller", comic.getPenciller());
    this.writeStringElement(document, "Inker", comic.getInker());
    this.writeStringElement(document, "Colorist", comic.getColorist());
    this.writeStringElement(document, "Letterer", comic.getLetterer());
    this.writeStringElement(document, "CoverArtist", comic.getCoverArtist());
    this.writeStringElement(document, "Editor", comic.getEditor());
    this.writeStringElement(document, "Web", comic.getWeb());
    this.writeStringElement(document, "Manga", comic.isManga() ? "Yes" : "No");
    this.writeStringElement(document, "Characters", comic.getCharacters());
    this.writeStringElement(document, "Teams", comic.getTeams());
    this.writeStringElement(document, "Locations", comic.getLocations());

    final StringWriter stringWriter = new StringWriter();
    TransformerFactory.newInstance().newTransformer()
      .transform(new DOMSource(document), new StreamResult(stringWriter));
    return stringWriter.toString();
  }

  private void parseComicInfoXml(final Comic comic)
      throws SAXException, IOException, ParserConfigurationException, NoMetaDataException {

    final Document document = this.getDocument(comic);

    document.getDocumentElement().normalize();
    comic.setTitle(this.readStringElement(document, "Title"));
    comic.setSeries(this.readStringElement(document, "Series"));
    comic.setPublisher(this.readStringElement(document, "Publisher"));
    comic.setNumber(this.readStringElement(document, "Number"));
    comic.setPosition(this.mapPosition(comic.getNumber()));
    comic.setVolume(this.readStringElement(document, "Volume"));
    comic.setSummary(this.readStringElement(document, "Summary"));
    comic.setNotes(this.readStringElement(document, "Notes"));
    comic.setYear(this.readIntegerElement(document, "Year"));
    comic.setMonth(this.readIntegerElement(document, "Month"));
    comic.setWriter(this.readStringElement(document, "Writer"));
    comic.setPenciller(this.readStringElement(document, "Penciller"));
    comic.setInker(this.readStringElement(document, "Inker"));
    comic.setColorist(this.readStringElement(document, "Colorist"));
    comic.setLetterer(this.readStringElement(document, "Letterer"));
    comic.setCoverArtist(this.readStringElement(document, "CoverArtist"));
    comic.setEditor(this.readStringElement(document, "Editor"));
    comic.setWeb(this.readStringElement(document, "Web"));
    comic.setManga(this.readStringElement(document, "Manga").equals("Yes"));
    comic.setCharacters(this.readStringElement(document, "Characters"));
    comic.setTeams(this.readStringElement(document, "Teams"));
    comic.setLocations(this.readStringElement(document, "Locations"));
  }

  private Document getDocument(final Comic comic)
      throws IOException, SAXException, ParserConfigurationException, NoMetaDataException {

    try (FileSystem fs = FileSystems.newFileSystem(Paths.get(comic.getPath()), null)) {
      try (InputStream xmlStream = Files.newInputStream(fs.getPath("/ComicInfo.xml"))) {
        return this.getDocumentBuilder().parse(xmlStream);
      }
    } catch (final NoSuchFileException exception) {
      throw new NoMetaDataException(exception);
    }
  }

  private Document getOrCreateDocument(final Comic comic) throws ParserConfigurationException {
    try {
      return this.getDocument(comic);
    } catch (final ProviderNotFoundException | IOException | SAXException | ParserConfigurationException | NoMetaDataException exception) {
      final Document document = this.getDocumentBuilder().newDocument();
      document.appendChild(document.createElement("ComicInfo"));
      return document;
    }
  }

  public List<ScannerIssue> read(final Comic comic)
      throws SAXException, IOException, NoMetaDataException,
      ParserConfigurationException, NoImagesException, InvalidFileException {

    this.scannerIssues.clear();
    this.parseComicInfoXml(comic);
    this.parseFiles(comic);
    return this.scannerIssues;
  }

  /**
   * Retrieves and validates information regarding the archive file structure.
   *
   * 1. Determines the page count
   * 2. Checks if there are any directories in the archive
   * 3. Saves all file names
   * @param comic The comic entity
   */
  public void parseFiles(final Comic comic) throws IOException, NoImagesException, InvalidFileException {
    try (FileSystem fs = FileSystems.newFileSystem(Paths.get(comic.getPath()), null)) {
      final List<Path> files = ZipReaderUtil.getEntries(fs);
      comic.setPageCount(0);
      try {
        comic.setPageCount((int) files.stream()
            .filter(file -> Files.isRegularFile(file) && file.toString().matches(".*(png|jpg)$"))
            .count());
      } catch (final IllegalArgumentException exception) {
        throw new InvalidFileException(exception);
      }
      if (comic.getPageCount() <= 0) {
        throw new NoImagesException();
      }

      if (files.stream().anyMatch(Files::isDirectory)) {
        final ScannerIssue parsingEvent = ScannerIssue.builder()
            .message("Found directory entries in the archive.")
            .type(ScannerIssue.Type.NOT_FLAT)
            .fixable(true)
            .severity(ScannerIssue.Severity.WARNING).build();
        log.warn(parsingEvent.getMessage());
        this.scannerIssues.add(parsingEvent);
      }

      comic.setFiles(files.stream()
          .map(entry -> entry.toString())
          .sorted()
          .collect(Collectors.toList()));
    }
  }

  public void write(final Comic comic) {
    try (FileSystem fs = FileSystems.newFileSystem(Paths.get(comic.getPath()), null)) {
      final Path source = fs.getPath("/ComicInfo.xml");
      if (Files.exists(source)) {
        Files.delete(source);
      }
      try (Writer writer = Files.newBufferedWriter(source, StandardCharsets.UTF_8, StandardOpenOption.CREATE)) {
        writer.write(this.marshal(comic));
        log.info(format("Finished writing ComicInfo.xml to %s", comic.getPath()));
      }
    } catch (final IOException | SAXException | TransformerException | ParserConfigurationException exception) {
      log.error(format("Failed to write ComicInfo.xml to %s", comic.getPath()));
    }
  }
}
