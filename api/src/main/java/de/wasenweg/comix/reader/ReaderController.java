package de.wasenweg.comix.reader;

import org.springframework.web.bind.annotation.RestController;

import de.wasenweg.comix.Comic;
import de.wasenweg.comix.ComicRepository;

import java.io.IOException;
import java.util.Enumeration;
import java.util.List;
import java.util.Optional;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@RequestMapping("${spring.data.rest.base-path}")
@RestController
public class ReaderController {

	@Autowired
	private ComicRepository comicRepository;
	
	// TODO figure out a caching strategy:
	/// * in memory, self handled and capped
	//  * file based, self handled and capped
	//  * hybrid
	//  * 3rd party library
	//private imageCache;
	
	private List<String> extractComic(Comic comic, Short page) {
		List<String> pages = null;
		ZipFile file = null;
		try {
			file = new ZipFile(comic.getPath());
		} catch (IOException e) {
			e.printStackTrace();
		}
		try {
			final Enumeration<? extends ZipEntry> entries = file.entries();
			while (entries.hasMoreElements()) {
				final ZipEntry entry = entries.nextElement();
				if (!entry.getName().equals("ComicInfo.xml")) {
				}
			}
		} finally {
			try {
				file.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		return pages;
	}

    @RequestMapping("/read/{id}")
    @ResponseBody
    public Object readFromBeginning(@PathVariable("id") Long id) {
    	return read(id, (short) 0);
    }

	/**
	 * Returns the next three pages.
	 * 
	 * @param id The ID of the comic to open.
	 * @param offset The page number from which to start.
	 * @return The first extracted page and metadata.
	 */
    @RequestMapping("/read/{id}/{offset}")
    @ResponseBody
    public Object read(@PathVariable("id") Long id, @PathVariable("offset") Short offset) {
		Optional<Comic> comicQuery = comicRepository.findById(id);

		Comic comic = null;
		
		if (!comicQuery.isPresent()) {
			return comic;
		}
		
		comic = comicQuery.get();

		List<String> pages = extractComic(comic, offset);
		return pages;
    }
}