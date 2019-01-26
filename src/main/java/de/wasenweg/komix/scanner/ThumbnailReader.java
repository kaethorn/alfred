package de.wasenweg.komix.scanner;

import de.wasenweg.komix.Comic;

import java.io.IOException;
import java.util.Comparator;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

public class ThumbnailReader {

    public static void set(final ZipFile file, final Comic comic)
            throws IOException {

        final Comparator<ZipEntry> byName =
                (ze1, ze2) -> ze1.getName().compareTo(ze2.getName());
        final Predicate<ZipEntry> isImage = ze -> ze.getName().matches(".*(png|jpg)$");
        final List<ZipEntry> sortedEntries = file.stream()
                .filter(isImage)
                .sorted(byName)
                .collect(Collectors.toList());

        comic.setThumbnail(Thumbnail.get(file.getInputStream(sortedEntries.get(0))).toByteArray());
    }
}
