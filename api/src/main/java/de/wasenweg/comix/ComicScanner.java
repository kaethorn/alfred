package de.wasenweg.comix;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ComicScanner {

	private final static String COMICS_PATH = "../sample";
	
	public static List<String> run() throws IOException {
		Path root = Paths.get(COMICS_PATH);
	    try (Stream<Path> files = Files.walk(root)) {
	        return files.filter(p -> Files.isRegularFile(p))
	                    .filter(p -> p.getFileName().toString().endsWith(".cbz"))
	                    .map(p -> root.relativize(p).toString())
	                    .collect(Collectors.toList());
	    }
	}
}