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

  private static String readStringElement(final Document document, final String elementName) {
    final NodeList element = document.getElementsByTagName(elementName);
    if (element.getLength() > 0) {
      return element.item(0).getTextContent();
    } else {
      return "";
    }
  }

  private static Short readShortElement(final Document document, final String elementName) {
    try {
      return Short.parseShort(readStringElement(document, "Year"));
    } catch (final Exception e) {
      return (short)0;
    }
  }

  private static Short getPageCount(final Document document) {
    final String pageCount = readStringElement(document, "PageCount");
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
    comic.setTitle(readStringElement(document, "Title"));
    comic.setSeries(readStringElement(document, "Series"));
    comic.setPublisher(readStringElement(document, "Publisher"));
    comic.setNumber(readStringElement(document, "Number"));
    comic.setPosition(mapPosition(comic.getNumber()));
    comic.setVolume(readStringElement(document, "Volume"));
    comic.setSummary(readStringElement(document, "Summary"));
    comic.setNotes(readStringElement(document, "Notes"));
    comic.setYear(readShortElement(document, "Year"));
    comic.setMonth(readShortElement(document, "Month"));
    comic.setWriter(readStringElement(document, "Writer"));
    comic.setPenciller(readStringElement(document, "Penciller"));
    comic.setInker(readStringElement(document, "Inker"));
    comic.setColorist(readStringElement(document, "Colorist"));
    comic.setLetterer(readStringElement(document, "Letterer"));
    comic.setEditor(readStringElement(document, "Editor"));
    comic.setWeb(readStringElement(document, "Web"));
    comic.setPageCount(getPageCount(document));
    comic.setManga(readStringElement(document, "Manga").equals("Yes"));
    comic.setCharacters(readStringElement(document, "Characters"));
    comic.setTeams(readStringElement(document, "Teams"));
    comic.setLocations(readStringElement(document, "Locations"));
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
