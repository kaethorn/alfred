package de.wasenweg.alfred.scanner;

import de.wasenweg.alfred.comics.Comic;
import de.wasenweg.alfred.util.ZipReaderUtil;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import java.io.IOException;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Optional;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

@Service
public class FileMetaDataReader {

  private List<ScannerIssue> scannerIssues = new ArrayList<ScannerIssue>();

  private Logger logger = LoggerFactory.getLogger(FileMetaDataReader.class);

  private DocumentBuilder docBuilder = null;

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

  private void parseComicInfoXml(final ZipFile file, final ZipEntry entry, final Comic comic)
      throws SAXException, IOException {

    final Optional<Document> documentOptional = this.getDocument(file, entry);

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

  public List<ScannerIssue> set(final ZipFile file, final Comic comic)
      throws SAXException, IOException, NoMetaDataException {

    this.scannerIssues.clear();

    this.setPageCountFromImages(file, comic);

    final DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();
    try {
      this.docBuilder = docBuilderFactory.newDocumentBuilder();
    } catch (final ParserConfigurationException exception) {
      exception.printStackTrace();
    }

    final ZipEntry metaDataFile = this.findMetaDataFile(file);
    this.parseComicInfoXml(file, metaDataFile, comic);

    return this.scannerIssues;
  }
}
