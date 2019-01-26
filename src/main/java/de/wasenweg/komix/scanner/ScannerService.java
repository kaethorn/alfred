package de.wasenweg.komix.scanner;

import de.wasenweg.komix.Comic;
import de.wasenweg.komix.ComicRepository;
import de.wasenweg.komix.preferences.PreferenceRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter.SseEventBuilder;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Enumeration;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

@Service
public class ScannerService {

    private List<SseEmitter> emitters = new ArrayList<>();

    private final PreferenceRepository preferenceRepository;

    private final ComicRepository comicRepository;

    private DocumentBuilder docBuilder = null;

    @Autowired
    public ScannerService(final ComicRepository comicRepository, final PreferenceRepository preferenceRepository) {
        this.comicRepository = comicRepository;
        this.preferenceRepository = preferenceRepository;

        final DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();
        try {
            docBuilder = docBuilderFactory.newDocumentBuilder();
        } catch (final ParserConfigurationException e) {
            e.printStackTrace();
        }
    }

    private void sendEvent(final String data, final String name) {
        final SseEventBuilder event = SseEmitter.event().data(data).id(String.valueOf(this.hashCode())).name(name);
        emitters.forEach(emitter -> {
            try {
                emitter.send(event);
            } catch (final IOException e) {
                reportError(e.getMessage());
                emitter.completeWithError(e);
            }
        });
    }

    private void reportProgress(final String path) {
        this.sendEvent(path, "current-file");
    }

    private void reportTotal(final int total) {
        this.sendEvent(String.valueOf(total), "total");
    }

    public void reportFinish() {
        this.sendEvent("", "done");
    }

    public void reportError(final String error) {
        this.sendEvent(error, "error");
    }

    private String readElement(final Document document, final String elementName) {
        final NodeList element = document.getElementsByTagName(elementName);
        if (element.getLength() > 0) {
            return element.item(0).getTextContent();
        } else {
            return "";
        }
    }

    private Short getPageCount(final Document document) {
        final String pageCount = readElement(document, "PageCount");
        if (pageCount.isEmpty()) {
            return (short) document.getElementsByTagName("Page").getLength();
        }
        return Short.parseShort(pageCount);
    }

    private String mapPosition(final String number) {
        final BigDecimal position = new BigDecimal(number.equals("½") ? "0.5" : number);
        final String result = new DecimalFormat("0000.0").format(position);
        return result;
    }

    private Optional<Document> getDocument(final ZipFile file, final ZipEntry entry) {
        Optional<Document> document = Optional.empty();

        try {
            document = Optional.ofNullable(
                    docBuilder.parse(file.getInputStream(entry)));
        } catch (final SAXException e) {
            e.printStackTrace();
            reportError(e.getMessage());
        } catch (final IOException e) {
            e.printStackTrace();
            reportError(e.getMessage());
        }

        return document;
    }

    private void parseComicInfoXml(final ZipFile file, final ZipEntry entry, final Comic comic) {
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
    }

    private Comic createComic(final Path path) {
        reportProgress(path.toString());

        final Comic comic = new Comic(path.toAbsolutePath().toString(), "", "", "", "0.0", (short) 0, (short) 0, "");

        ZipFile file = null;
        try {
            file = new ZipFile(path.toString());
            readMetadata(file, comic);
            readThumbNail(file, comic);
        } catch (final Exception e) {
            e.printStackTrace();
            reportError(e.getMessage());
        } finally {
            try {
                file.close();
            } catch (final IOException e) {
                e.printStackTrace();
                reportError(e.getMessage());
            }
        }

        return comic;
    }

    private void readMetadata(final ZipFile file, final Comic comic) {
        final Enumeration<? extends ZipEntry> entries = file.entries();
        while (entries.hasMoreElements()) {
            final ZipEntry entry = entries.nextElement();
            if (entry.getName().equals("ComicInfo.xml")) {
                parseComicInfoXml(file, entry, comic);
                break;
            }
        }
    }

    private void readThumbNail(final ZipFile file, final Comic comic) {
        final Comparator<ZipEntry> byName =
                (ze1, ze2) -> ze1.getName().compareTo(ze2.getName());
        final Predicate<ZipEntry> isImage = ze -> ze.getName().matches(".*(png|jpg)$");
        final List<ZipEntry> sortedEntries = file.stream()
                .filter(isImage)
                .sorted(byName)
                .collect(Collectors.toList());

        try {
            comic.setThumbnail(Thumbnail.get(file.getInputStream(sortedEntries.get(0))).toByteArray());
        } catch (final IOException e) {
            e.printStackTrace();
        }
    }

    public void scanComics(final List<SseEmitter> emitters) {
        this.emitters = emitters;

        List<Comic> comics = null;

        final String comicsPath = preferenceRepository.findByKey("comics.path").getValue();
        final Path root = Paths.get(comicsPath);

        List<Path> comicFiles = null;

        try (Stream<Path> files = Files.walk(root)) {
            comicFiles = files.filter(path -> Files.isRegularFile(path))
                    .filter(path -> path.getFileName().toString().endsWith(".cbz")).collect(Collectors.toList());

            reportTotal(comicFiles.size());

            comics = comicFiles.stream()
                    .map(path -> createComic(path))
                    .filter(path -> !path.getTitle().isEmpty())
                    .collect(Collectors.toList());
        } catch (final IOException e) {
            e.printStackTrace();
            reportError(e.getMessage());
        }

        comicRepository.deleteAll();
        comicRepository.saveAll(comics);
        reportFinish();
        emitters.forEach(emitter -> {
            emitter.complete();
        });
    }
}
