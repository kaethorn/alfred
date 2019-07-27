package de.wasenweg.alfred.scanner;

import de.wasenweg.alfred.comics.Comic;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class ApiMetaDataReader {

  private List<ScannerIssue> scannerIssues = new ArrayList<ScannerIssue>();

  private Logger logger = LoggerFactory.getLogger(ApiMetaDataReader.class);

  private Pattern pattern;

  public ApiMetaDataReader() {
    final String publisherDirPattern = "^.*?(?<publisher>[^/]+)/";
    final String seriesDirPattern = "(?<series1>[^/]+) \\((?<volume1>\\d{4})\\)/";
    final String fileNamePattern = "(?<series2>[^/]+) (?<number>[\\d\\.a½/]+) \\((?<volume2>\\d{4})\\) [^/]+\\.cbz$";
    this.pattern = Pattern
        .compile(publisherDirPattern + seriesDirPattern + fileNamePattern);
  }

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

  private String mapPosition(final String number) {
    try {
      return MetaDataReaderUtil.mapPosition(number);
    } catch (final InvalidIssueNumberException exception) {
      this.logger.warn(exception.getMessage(), exception);
      this.scannerIssues.add(ScannerIssue.builder()
          .message(exception.getMessage())
          .type(ScannerIssue.Type.WARNING)
          .build());
      return new DecimalFormat("0000.0").format(new BigDecimal(0));
    }
  }

  /**
   * Expected format:
   *
   * `/{publisher}/{series}/{series} #{number} ({volume}).cbz`
   *
   * The publisher parent folder is optional.
   */
  // FIXME make private and use PowerMock to test it
  public void setPathParts(final Comic comic) {
    final Matcher matcher = this.pattern.matcher(comic.getPath());
    if (matcher.matches()
        && matcher.group("series1").equals(matcher.group("series2"))
        && matcher.group("volume1").equals(matcher.group("volume2"))) {
      comic.setPublisher(matcher.group("publisher"));
      comic.setSeries(matcher.group("series1"));
      comic.setVolume(matcher.group("volume1"));
      comic.setNumber(matcher.group("number"));
      comic.setPosition(this.mapPosition(comic.getNumber()));
    }
  }

  /**
   * Extract meta data from file path and match against API.
   *
   * @param comic The comic book entity.
   * @return
   */
  public List<ScannerIssue> set(final Comic comic) throws IncompleteMetaDataException {
    this.scannerIssues.clear();

    if (!this.isValid(comic)) {
      // Attempt to extract meta data from file path
      this.setPathParts(comic);
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

    return this.scannerIssues;
  }
}
