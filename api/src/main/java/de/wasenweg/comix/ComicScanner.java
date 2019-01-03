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
	
	private static Comic readMetadata(Path path) throws FileNotFoundException, IOException {
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
        }
		return comic;
	}
	
	public static List<Comic> run() throws IOException {
		Path root = Paths.get(COMICS_PATH);
	    try (Stream<Path> files = Files.walk(root)) {
	        return files.filter(path -> Files.isRegularFile(path))
	                    .filter(path -> path.getFileName().toString().endsWith(".cbz"))
	                    .map(path -> {
							try {
								return readMetadata(path);
							} catch (FileNotFoundException e) {
								return new Comic();
							} catch (IOException e) {
								return new Comic();
							}
						})
	                    .filter(path -> path.getTitle() != null)
	                    .collect(Collectors.toList());
	    }
	}
}