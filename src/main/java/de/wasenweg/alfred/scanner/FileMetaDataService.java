package de.wasenweg.alfred.scanner;

import de.wasenweg.alfred.comics.Comic;
import de.wasenweg.alfred.util.ZipReaderUtil;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
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
import java.io.StringWriter;
import java.io.Writer;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Optional;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

@Service
public class FileMetaDataService {

  private List<ScannerIssue> scannerIssues = new ArrayList<ScannerIssue>();

  private Logger logger = LoggerFactory.getLogger(FileMetaDataService.class);

  private DocumentBuilder docBuilder = null;

  @Autowired
  public FileMetaDataService() {
    final DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();
    try {
      this.docBuilder = docBuilderFactory.newDocumentBuilder();
    } catch (final ParserConfigurationException exception) {
      exception.printStackTrace();
    }
  }

  private void writeStringElement(final Document document, final String elementName, final String value) {
    final NodeList element = document.getDocumentElement().getElementsByTagName(elementName);
    if (element.getLength() > 0) {
      element.item(0).setTextContent(value);
    } else {
      final Element newElement = document.createElement(elementName);
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

  private Short readShortElement(final Document document, final String elementName) {
    final String value = this.readStringElement(document, elementName);
    try {
      return Short.parseShort(value);
    } catch (final Exception exception) {
      final ScannerIssue parsingEvent = ScannerIssue.builder()
          .message("Couldn't read " + elementName + " value of '" + value + "'. Falling back to '0'")
          .type(ScannerIssue.Type.WARNING)
          .build();
      this.logger.warn(parsingEvent.getMessage());
      this.scannerIssues.add(parsingEvent);
      return (short)0;
    }
  }

  private Short getPageCount(final Document document) {
    final String pageCount = this.readStringElement(document, "PageCount");
    if (pageCount.isEmpty()) {
      return (short) document.getElementsByTagName("Page").getLength();
    }
    return Short.parseShort(pageCount);
  }

  private String mapPosition(final String number) {
    try {
      return Comic.mapPosition(number);
    } catch (final InvalidIssueNumberException exception) {
      this.logger.warn(exception.getMessage(), exception);
      this.scannerIssues.add(ScannerIssue.builder()
          .message(exception.getMessage())
          .type(ScannerIssue.Type.WARNING)
          .build());
      return new DecimalFormat("0000.0").format(new BigDecimal(0));
    }
  }

  private Optional<Document> getDocument(final ZipFile file, final ZipEntry entry)
      throws SAXException, IOException {

    return Optional.ofNullable(
        this.docBuilder.parse(file.getInputStream(entry)));
  }

  private String readShortValue(final Short value) {
    if (value == null) {
      return null;
    }
    return value.toString();
  }

  private String marshal(final Comic comic) throws SAXException, IOException, TransformerException {
    Document document;
    final ZipFile file = this.getZipFile(comic);

    try {
      document = this.getDocument(file, this.findMetaDataFile(file)).get();
    } catch (final NoMetaDataException exception) {
      document = this.docBuilder.newDocument();
      document.appendChild(document.createElement("ComicInfo"));
    }
    file.close();

    this.writeStringElement(document, "Title", comic.getTitle());
    this.writeStringElement(document, "Series", comic.getSeries());
    this.writeStringElement(document, "Publisher", comic.getPublisher());
    this.writeStringElement(document, "Number", comic.getNumber());
    this.writeStringElement(document, "Volume", comic.getVolume());
    this.writeStringElement(document, "Summary", comic.getSummary());
    this.writeStringElement(document, "Notes", comic.getNotes());
    this.writeStringElement(document, "Year", this.readShortValue(comic.getYear()));
    this.writeStringElement(document, "Month", this.readShortValue(comic.getMonth()));
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

  private void parseComicInfoXml(final ZipFile file, final Comic comic)
      throws SAXException, IOException, NoMetaDataException {

    final Optional<Document> documentOptional = this.getDocument(file, this.findMetaDataFile(file));

    if (!documentOptional.isPresent()) {
      return;
    }

    final Document document = documentOptional.get();

    document.getDocumentElement().normalize();
    comic.setTitle(this.readStringElement(document, "Title"));
    comic.setSeries(this.readStringElement(document, "Series"));
    comic.setPublisher(this.readStringElement(document, "Publisher"));
    comic.setNumber(this.readStringElement(document, "Number"));
    comic.setPosition(this.mapPosition(comic.getNumber()));
    comic.setVolume(this.readStringElement(document, "Volume"));
    comic.setSummary(this.readStringElement(document, "Summary"));
    comic.setNotes(this.readStringElement(document, "Notes"));
    comic.setYear(this.readShortElement(document, "Year"));
    comic.setMonth(this.readShortElement(document, "Month"));
    comic.setWriter(this.readStringElement(document, "Writer"));
    comic.setPenciller(this.readStringElement(document, "Penciller"));
    comic.setInker(this.readStringElement(document, "Inker"));
    comic.setColorist(this.readStringElement(document, "Colorist"));
    comic.setLetterer(this.readStringElement(document, "Letterer"));
    comic.setCoverArtist(this.readStringElement(document, "CoverArtist"));
    comic.setEditor(this.readStringElement(document, "Editor"));
    comic.setWeb(this.readStringElement(document, "Web"));
    comic.setPageCount(this.getPageCount(document));
    comic.setManga(this.readStringElement(document, "Manga").equals("Yes"));
    comic.setCharacters(this.readStringElement(document, "Characters"));
    comic.setTeams(this.readStringElement(document, "Teams"));
    comic.setLocations(this.readStringElement(document, "Locations"));
  }

  private ZipEntry findMetaDataFile(final ZipFile file) throws NoMetaDataException {
    final Enumeration<? extends ZipEntry> entries = file.entries();
    while (entries.hasMoreElements()) {
      final ZipEntry entry = entries.nextElement();
      if (entry.getName().equals("ComicInfo.xml")) {
        return entry;
      }
    }
    throw new NoMetaDataException();
  }

  private void setPageCountFromImages(final ZipFile file, final Comic comic) {
    short pageCount;
    try {
      final List<ZipEntry> sortedEntries = ZipReaderUtil.getImages(file);
      pageCount = (short) sortedEntries.size();
    } catch (final Exception exception) {
      pageCount = (short) 0;
    }
    comic.setPageCount(pageCount);
  }

  public ZipFile getZipFile(final Comic comic) throws IOException {
    return new ZipFile(comic.getPath());
  }

  public List<ScannerIssue> read(final ZipFile file, final Comic comic)
      throws SAXException, IOException, NoMetaDataException {
    this.scannerIssues.clear();
    this.setPageCountFromImages(file, comic);
    this.parseComicInfoXml(file, comic);
    return this.scannerIssues;
  }

  public void write(final Comic comic) {
    final Path zipFilePath = Paths.get(comic.getPath());
    try (final FileSystem fs = FileSystems.newFileSystem(zipFilePath, null)) {
      final Path source = fs.getPath("/ComicInfo.xml");
      if (Files.exists(source)) {
        Files.delete(source);
      }
      final Writer writer = Files.newBufferedWriter(source, StandardCharsets.UTF_8, StandardOpenOption.CREATE);
      writer.write(this.marshal(comic));
      writer.close();
      fs.close();
      this.logger.info("Finished writing ComicInfo.XML for " + comic.getPath());
    } catch (final IOException | SAXException | TransformerException exception) {
      exception.printStackTrace();
    }
  }
}
