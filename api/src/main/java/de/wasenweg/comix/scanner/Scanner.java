package de.wasenweg.comix.scanner;

import de.wasenweg.comix.Comic;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter.SseEventBuilder;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Enumeration;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

public class Scanner {

    private final SseEmitter emitter;

    public Scanner(final SseEmitter emitter) {
        this.emitter = emitter;
    }

    private final String comicsPath = "../sample";

    private void sendEvent(final String data, final String name) {
        final SseEventBuilder event = SseEmitter.event().data(data).id(String.valueOf(this.hashCode())).name(name);
        try {
            emitter.send(event);
        } catch (final IOException e) {
            emitter.completeWithError(e);
        }
    }

    public void reportProgress(final String path) {
        this.sendEvent(path, "current-file");
    }

    public void reportTotal(final int total) {
        this.sendEvent(String.valueOf(total), "total");
    }

    public void reportFinish() {
        this.sendEvent("", "done");
    }

    private String readElement(final Document document, final String elementName) {
        final NodeList element = document.getElementsByTagName(elementName);
        if (element.getLength() > 0) {
            return element.item(0).getTextContent();
        } else {
            return "";
        }
    }

    private Comic readMetadata(final Path path) {
        reportProgress(path.toString());

        final DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder docBuilder = null;
        try {
            docBuilder = docBuilderFactory.newDocumentBuilder();
        } catch (final ParserConfigurationException e) {
            e.printStackTrace();
        }

        final Comic comic = new Comic(path.toAbsolutePath().toString(), "", "", "", (short) 0, (short) 0, "");

        ZipFile file = null;
        try {
            file = new ZipFile(path.toString());
        } catch (final IOException e) {
            e.printStackTrace();
        }
        try {
            final Enumeration<? extends ZipEntry> entries = file.entries();
            while (entries.hasMoreElements()) {
                final ZipEntry entry = entries.nextElement();
                if (entry.getName().equals("ComicInfo.xml")) {
                    final Document document = docBuilder.parse(file.getInputStream(entry));
                    document.getDocumentElement().normalize();
                    comic.setTitle(readElement(document, "Title"));
                    comic.setSeries(readElement(document, "Series"));
                    comic.setPublisher(readElement(document, "Publisher"));
                    comic.setNumber(readElement(document, "Number"));
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
                    comic.setPageCount(Short.parseShort(readElement(document, "PageCount")));
                    comic.setManga(readElement(document, "Manga").equals("Yes"));
                    comic.setCharacters(readElement(document, "Characters"));
                    comic.setTeams(readElement(document, "Teams"));
                }
            }
        } catch (final SAXException e) {
            e.printStackTrace();
        } catch (final IOException e) {
            e.printStackTrace();
        } finally {
            try {
                file.close();
            } catch (final IOException e) {
                e.printStackTrace();
            }
        }

        return comic;
    }

    public List<Comic> run() {
        List<Comic> list = null;
        final Path root = Paths.get(comicsPath);

        List<Path> comicFiles = null;

        try (Stream<Path> files = Files.walk(root)) {
            comicFiles = files.filter(path -> Files.isRegularFile(path))
                    .filter(path -> path.getFileName().toString().endsWith(".cbz")).collect(Collectors.toList());

            reportTotal(comicFiles.size());

            list = comicFiles.stream().map(path -> readMetadata(path)).filter(path -> !path.getTitle().isEmpty())
                    .collect(Collectors.toList());
        } catch (final IOException e) {
            e.printStackTrace();
        }

        return list;
    }
}
