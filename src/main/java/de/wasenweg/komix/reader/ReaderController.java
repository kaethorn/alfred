package de.wasenweg.komix.reader;

import de.wasenweg.komix.comics.Comic;
import de.wasenweg.komix.comics.ComicRepository;
import de.wasenweg.komix.progress.Progress;
import de.wasenweg.komix.progress.ProgressRepository;
import de.wasenweg.komix.util.ZipReader;

import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLConnection;
import java.security.Principal;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

@RequestMapping("/api")
@RestController
public class ReaderController {

    @Autowired
    private ComicRepository comicRepository;

    @Autowired
    private ProgressRepository progressRepository;

    private ComicPage extractPage(final Comic comic, final Short page) {
        final ComicPage result = new ComicPage();
        ZipFile file = null;
        try {
            file = new ZipFile(comic.getPath());
            final List<ZipEntry> sortedEntries = ZipReader.getImages(file);
            final ZipEntry entry = sortedEntries.get(page);
            final String fileName = entry.getName();
            result.stream = file.getInputStream(entry);
            result.size = entry.getSize();
            result.type = URLConnection.guessContentTypeFromName(fileName);
            result.name = fileName;
        } catch (final IOException e) {
            e.printStackTrace();
        }

        return result;
    }

    @Transactional
    private void setReadState(final Comic comic, final Short page, final String userId) {
        final ObjectId comicId = new ObjectId(comic.getId());
        final Progress progress = progressRepository
                .findByUserIdAndComicId(userId, comicId)
                .orElse(Progress.builder().comicId(comicId).userId(userId).build());

        progress.setCurrentPage(page);
        progress.setLastRead(new Date());

        if (page == comic.getPageCount() - 1) {
            progress.setRead(true);
        }

        progressRepository.save(progress);
    }

    @GetMapping("/read/{id}")
    @ResponseBody
    public ResponseEntity<StreamingResponseBody> readFromBeginning(
            @PathVariable("id") final String id,
            final Principal principal) {
        return read(id, (short) 0, principal);
    }

    /**
     * Returns the page of the given comic.
     *
     * @param id   The ID of the comic to open.
     * @param page The page number from which to start.
     * @return The extracted page.
     */
    @GetMapping("/read/{id}/{page}")
    @ResponseBody
    public ResponseEntity<StreamingResponseBody> read(
            @PathVariable("id") final String id,
            @PathVariable("page") final Short page,
            final Principal principal) {
        final Optional<Comic> comicQuery = comicRepository.findById(id);

        if (!comicQuery.isPresent() || id == null || page == null) {
            return null;
        }

        final Comic comic = comicQuery.get();

        setReadState(comic, page, principal.getName());

        final ComicPage comicPage = extractPage(comic, page);

        final StreamingResponseBody responseBody = outputStream -> {
            int numberOfBytesToWrite;
            final byte[] data = new byte[1024];
            while ((numberOfBytesToWrite = comicPage.stream.read(data, 0, data.length)) != -1) {
                outputStream.write(data, 0, numberOfBytesToWrite);
            }
            comicPage.stream.close();
        };

        return ResponseEntity
                .ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline;filename=" + comicPage.name)
                .contentLength(comicPage.size)
                .contentType(MediaType.parseMediaType(comicPage.type))
                .body(responseBody);
    }

    @GetMapping("/download/{id}")
    @ResponseBody
    public ResponseEntity<StreamingResponseBody> download(@PathVariable("id") final String id) throws FileNotFoundException {
        final Optional<Comic> comicQuery = comicRepository.findById(id);

        if (!comicQuery.isPresent() || id == null) {
            return null;
        }

        final Comic comic = comicQuery.get();

        final File file = new File(comic.getPath());

        final InputStream inputStream = new FileInputStream(file);

        final StreamingResponseBody responseBody = outputStream -> {
            int numberOfBytesToWrite;
            final byte[] data = new byte[1024];
            while ((numberOfBytesToWrite = inputStream.read(data, 0, data.length)) != -1) {
                outputStream.write(data, 0, numberOfBytesToWrite);
            }

            inputStream.close();
        };

        return ResponseEntity
                .ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + file.getName())
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(responseBody);
    }
}
