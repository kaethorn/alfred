package de.wasenweg.comix.scanner;

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

import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import de.wasenweg.comix.Comic;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

public class Scanner {

	private SimpMessageSendingOperations messagingTemplate;

	public Scanner(SimpMessageSendingOperations messagingTemplate) {
		this.messagingTemplate = messagingTemplate;
	}
	
	private final String COMICS_PATH = "../sample";
	
	private void reportProgress(final String path) {
	    messagingTemplate.convertAndSend("/progress/scanner", path);
	}

	private String readElement(Document document, String elementName) {
		NodeList element = document.getElementsByTagName(elementName);
		if (element.getLength() > 0) {
			return element.item(0).getTextContent();
		} else {
			return "";
		}
	}

	private Comic readMetadata(Path path) {
		reportProgress(path.toString());

		// FIXME simulated delay
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder docBuilder = null;
		try {
			docBuilder = docBuilderFactory.newDocumentBuilder();
		} catch (ParserConfigurationException e1) {
			e1.printStackTrace();
		}

		Comic comic = new Comic(path.toAbsolutePath().toString(), "", "", "", (short) 0, (short) 0, "");
		
		ZipFile file = null;
		try {
			file = new ZipFile(path.toString());
		} catch (IOException e) {
			e.printStackTrace();
		}
        try {
            final Enumeration<? extends ZipEntry> entries = file.entries();
            while (entries.hasMoreElements()) {
                final ZipEntry entry = entries.nextElement();
                if (entry.getName().equals("ComicInfo.xml")) {
            		Document document = docBuilder.parse(file.getInputStream(entry));
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
        } catch (SAXException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
            try {
				file.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
        }

		return comic;
	}

	public List<Comic> run() throws IOException {
		Path root = Paths.get(COMICS_PATH);
    	// TODO send a message with the file total amount.
	    try (Stream<Path> files = Files.walk(root)) {
	        return files.filter(path -> Files.isRegularFile(path))
	                    .filter(path -> path.getFileName().toString().endsWith(".cbz"))
	                    .map(path -> readMetadata(path))
	                    .filter(path -> !path.getTitle().isEmpty())
	                    .collect(Collectors.toList());
	    }
	}
}