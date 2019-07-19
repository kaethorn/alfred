package de.wasenweg.alfred.scanner;

import de.wasenweg.alfred.comics.Comic;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import java.io.IOException;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.Enumeration;
import java.util.Optional;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

public class MetaDataReader {

  private static Logger logger = LoggerFactory.getLogger(MetaDataReader.class);

  private static DocumentBuilder docBuilder = null;

  private static String readElement(final Document document, final String elementName) {
    final NodeList element = document.getElementsByTagName(elementName);
    if (element.getLength() > 0) {
      return element.item(0).getTextContent();
    } else {
      return "";
    }
  }

  private static Short getPageCount(final Document document) {
    final String pageCount = readElement(document, "PageCount");
    if (pageCount.isEmpty()) {
      return (short) document.getElementsByTagName("Page").getLength();
    }
    return Short.parseShort(pageCount);
  }

  private static String mapPosition(final String number) {
    String convertableNumber = number;
    if (number.equals("½") || number.equals("1/2")) {
      convertableNumber = "0.5";
    }
    if (number.endsWith("a")) {
      convertableNumber = convertableNumber.replace("a", ".5");
    }
    BigDecimal position = new BigDecimal(0);
    try {
      position = new BigDecimal(convertableNumber);
    } catch (final Exception exception) {
      logger.warn("Couldn't read number '" + number + "'. Falling back to '0'", exception);
    }
    final String result = new DecimalFormat("0000.0").format(position);
    return result;
  }

  private static Optional<Document> getDocument(final ZipFile file, final ZipEntry entry)
      throws SAXException, IOException {

    return Optional.ofNullable(
        docBuilder.parse(file.getInputStream(entry)));
  }

  private static void parseComicInfoXml(final ZipFile file, final ZipEntry entry, final Comic comic)
      throws SAXException, IOException {

    final Optional<Document> documentOptional = getDocument(file, entry);

    if (!documentOptional.isPresent()) {
      return;
    }

    final Document document = documentOptional.get();

    document.getDocumentElement().normalize();
    comic.setTitle(readElement(document, "Title"));
    comic.setSeries(readElement(document, "Series"));
    comic.setPublisher(readElement(document, "Publisher"));
    comic.setNumber(readElement(document, "Number"));
    comic.setPosition(mapPosition(comic.getNumber()));
    comic.setVolume(readElement(document, "Volume"));
    comic.setSummary(readElement(document, "Summary"));
    comic.setNotes(readElement(document, "Notes"));
    comic.setYear(Short.parseShort(readElement(document, "Year")));
    comic.setMonth(Short.parseShort(readElement(document, "Month")));
    comic.setWriter(readElement(document, "Writer"));
    comic.setPenciller(readElement(document, "Penciller"));
    comic.setInker(readElement(document, "Inker"));
    comic.setColorist(readElement(document, "Colorist"));
    comic.setLetterer(readElement(document, "Letterer"));
    comic.setEditor(readElement(document, "Editor"));
    comic.setWeb(readElement(document, "Web"));
    comic.setPageCount(getPageCount(document));
    comic.setManga(readElement(document, "Manga").equals("Yes"));
    comic.setCharacters(readElement(document, "Characters"));
    comic.setTeams(readElement(document, "Teams"));
    comic.setLocations(readElement(document, "Locations"));
  }

  public static void set(final ZipFile file, final Comic comic)
      throws SAXException, IOException {

    final DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();
    try {
      docBuilder = docBuilderFactory.newDocumentBuilder();
    } catch (final ParserConfigurationException e) {
      e.printStackTrace();
    }

    final Enumeration<? extends ZipEntry> entries = file.entries();
    while (entries.hasMoreElements()) {
      final ZipEntry entry = entries.nextElement();
      if (entry.getName().equals("ComicInfo.xml")) {
        parseComicInfoXml(file, entry, comic);
        break;
      }
    }
  }
}
