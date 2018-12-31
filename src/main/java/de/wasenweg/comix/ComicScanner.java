package de.wasenweg.comix;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ComicScanner {

	public static String run() throws IOException {
        return "Found comics: " + collectFiles("sample");
	}
	
	private static List<String> collectFiles(String path) throws IOException {
		Path root = Paths.get(path);
	    try (Stream<Path> files = Files.walk(root)) {
	        return files.filter(p -> Files.isRegularFile(p))
	                    .filter(p -> p.getFileName().toString().endsWith(".cbz"))
	                    .map(p -> root.relativize(p).toString())
	                    .collect(Collectors.toList());
	    }
	}
}