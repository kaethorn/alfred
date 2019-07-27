package de.wasenweg.alfred.scanner;

import de.wasenweg.alfred.comics.Comic;

import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class ComicVineReader {

  private List<String> findMissingAttributes(final Comic comic) {
    final List<String> missingAttributes = new ArrayList<String>();
    if ("".equals(comic.getPublisher())) {
      missingAttributes.add("publisher");
    }
    if ("".equals(comic.getSeries())) {
      missingAttributes.add("publisher");
    }
    if ("".equals(comic.getVolume())) {
      missingAttributes.add("publisher");
    }
    if ("".equals(comic.getNumber())) {
      missingAttributes.add("publisher");
    }
    return missingAttributes;
  }

  private Boolean isValid(final Comic comic) {
    return this.findMissingAttributes(comic).isEmpty();
  }

  /**
   * Extract meta data from file path and match against API.
   *
   * Expected format:
   * `/{publisher}/{series}/{series} #{number} ({volume}).cbz`
   *
   * The publisher parent folder is optional.
   *
   * @param comic The comic book entity.
   * @return
   */
  public void read(final Comic comic) throws IncompleteMetaDataException {
    // Attempt to extract meta data from file path
    if (!this.isValid(comic)) {
      // TODO
      // Match file path
      final Pattern pattern = Pattern.compile("a*b");
      final Matcher matcher = pattern.matcher(comic.getPath());
      matcher.matches();
      comic.setSeries("TODO");
      // TODO
    }

    // If neither the XML nor the file path contain enough hints about which
    // comic book there is, we inform the user.
    final List<String> missingAttributes = this.findMissingAttributes(comic);
    if (missingAttributes.size() > 0) {
      throw new IncompleteMetaDataException(missingAttributes);
    }

    // Here we can assume to have enough meta data about the comic to make
    // a query to the Comic Vine API.
    // TODO
  }
}
