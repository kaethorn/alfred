package de.wasenweg.alfred.util;

import java.util.Comparator;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

public class ZipReader {

    /**
     * Returns a sorted list of images in the given zip file.
     * @param The zip file to search through.
     * @return List of image zip entries.
     */
    public static List<ZipEntry> getImages(final ZipFile file) {
        final Comparator<ZipEntry> byName =
                (ze1, ze2) -> ze1.getName().compareTo(ze2.getName());
        final Predicate<ZipEntry> isImage = ze -> ze.getName().matches(".*(png|jpg)$");

        return file.stream()
                .filter(isImage)
                .sorted(byName)
                .collect(Collectors.toList());
    }
}
