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

import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import de.wasenweg.comix.Comic;
import reactor.core.publisher.FluxSink;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

public class Scanner {

	private FluxSink<String> sink;
	
	public Scanner(FluxSink<String> fluxSink) {
		this.sink = fluxSink;
	}

	private final String COMICS_PATH = "../sample";
	
	private void sendEvent(String data, String name) {
		sink.next(name + ":" + data);
	}

	public void reportProgress(final String path) {
		this.sendEvent(path, "current-file");
	}

	public void reportTotal(final int total) {
		this.sendEvent(String.format("%i", total), "total");
	}

	public void reportFinish() {
		this.sendEvent("", "done");
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
			Thread.sleep(300);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder docBuilder = null;
		try {
			docBuilder = docBuilderFactory.newDocumentBuilder();
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
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

	public List<Comic> run() {
		List<Comic> list = null;
		Path root = Paths.get(COMICS_PATH);
		
		List<Path> comicFiles = null;

		try (Stream<Path> files = Files.walk(root)) {
			comicFiles = files.filter(path -> Files.isRegularFile(path))
					.filter(path -> path.getFileName().toString().endsWith(".cbz"))
					.collect(Collectors.toList());
			
			reportTotal(comicFiles.size());
			
			list = comicFiles.stream()
					.map(path -> readMetadata(path))
					.filter(path -> !path.getTitle().isEmpty())
					.collect(Collectors.toList());
		} catch (IOException e) {
			e.printStackTrace();
		}

		return list;
	}
}