package de.wasenweg.comix;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class ComicScanner {

	private final static String COMICS_PATH = "../sample";
	
	private static Comic readMetadata(Path path) {
		Comic comic = new Comic(path.toAbsolutePath().toString(), "", "", (short) 0, (short) 0, (short) 0, "");
		try (FileInputStream fis = new FileInputStream(path.toString());
             BufferedInputStream bis = new BufferedInputStream(fis);
             ZipInputStream zis = new ZipInputStream(bis)) {
            
			ZipEntry ze;

			String files = "";
            while ((ze = zis.getNextEntry()) != null) {
                files += ze.getName() + "(" + ze.getSize() + ");";
            }
            comic.setSummary(files);
        } catch (FileNotFoundException e) {
		} catch (IOException e) {
		}
		return comic;
	}
	
	public static List<Comic> run() throws IOException {
		Path root = Paths.get(COMICS_PATH);
	    try (Stream<Path> files = Files.walk(root)) {
	        return files.filter(path -> Files.isRegularFile(path))
	                    .filter(path -> path.getFileName().toString().endsWith(".cbz"))
	                    .map(path -> readMetadata(path))
	                    .filter(path -> !path.getTitle().isEmpty())
	                    .collect(Collectors.toList());
	    }
	}
}