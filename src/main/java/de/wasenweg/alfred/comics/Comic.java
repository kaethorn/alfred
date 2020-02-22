package de.wasenweg.alfred.comics;

import de.wasenweg.alfred.scanner.InvalidIssueNumberException;
import de.wasenweg.alfred.scanner.ScannerIssue;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static java.lang.String.format;

@Data
@Builder
@AllArgsConstructor
@RequiredArgsConstructor
@Document
public class Comic {

  private static final List<String> HALF_ISSUE_NUMBERS = Arrays.asList("½", "1/2");
  private static final String PUBLISHER_DIR_PATTERN = "^.*?(?<publisher>[^/]+)/";
  private static final String SERIES_DIR_PATTERN = "(?<series1>[^/]+) \\((?<volume1>\\d{4})\\)/";
  private static final String FILE_NAME_PATTERN = "(?<series2>[^/]+) (?<number>[\\d\\.a½/]+) \\((?<volume2>\\d{4})\\)( [^/]+)?\\.cbz$";
  private static Pattern pattern = Pattern
        .compile(PUBLISHER_DIR_PATTERN + SERIES_DIR_PATTERN + FILE_NAME_PATTERN);

  @Id
  private String id;

  @NonNull
  private String path;

  @NonNull
  private String fileName;

  @NonNull
  @Indexed
  private String publisher;

  @NonNull
  @Indexed
  private String series;

  @NonNull
  @Indexed
  private String volume;

  @NonNull
  private String number;

  @Indexed
  private String position;

  private Integer year;

  private Integer month;

  private String title;

  private String summary;

  private String notes;

  private String writer;

  private String penciller;

  private String inker;

  private String colorist;

  private String letterer;

  private String coverArtist;

  private String editor;

  private String web;

  private boolean manga;

  private String characters;

  private String teams;

  private String locations;

  private Integer pageCount;
  private String nextId;
  private String previousId;

  private boolean read;

  private List<ScannerIssue> errors;
  private List<String> files;

  @Builder.Default
  private Integer currentPage = 0;

  private Date lastRead;

  public Comic() {
    this.read = false;
    this.currentPage = 0;
  }

  public void setNumber(final String number) {
    this.number = number;
    try {
      this.position = Comic.mapPosition(number);
    } catch (final InvalidIssueNumberException exception) {
      this.position = new DecimalFormat("0000.0").format(BigDecimal.ZERO);
    }
  }

  public Boolean isValid() {
    return this.findMissingAttributes().isEmpty();
  }

  /**
   * Expected format:
   * `/{publisher}/{series} ({volume})/{series} #{number} ({volume}).cbz`
   */
  public void setPathParts() throws InvalidIssueNumberException {
    final Matcher matcher = pattern.matcher(this.getPath());
    if (matcher.matches()
        && matcher.group("series1").equals(matcher.group("series2"))
        && matcher.group("volume1").equals(matcher.group("volume2"))) {
      this.setPublisher(matcher.group("publisher"));
      this.setSeries(matcher.group("series1"));
      this.setVolume(matcher.group("volume1"));
      this.setNumber(matcher.group("number"));
      this.setPosition(Comic.mapPosition(this.getNumber()));
    }
  }

  public List<String> findMissingAttributes() {
    final List<String> missingAttributes = new ArrayList<>();
    if (this.getPublisher() == null) {
      missingAttributes.add("publisher");
    }
    if (this.getSeries() == null) {
      missingAttributes.add("series");
    }
    if (this.getVolume() == null) {
      missingAttributes.add("volume");
    }
    if (this.getNumber() == null) {
      missingAttributes.add("number");
    }
    return missingAttributes;
  }

  public static String mapPosition(final String number) throws InvalidIssueNumberException {
    String convertableNumber = number;
    if (HALF_ISSUE_NUMBERS.contains(convertableNumber)) {
      convertableNumber = "0.5";
    } else if (convertableNumber.endsWith("a")) {
      convertableNumber = convertableNumber.replace("a", ".5");
    }
    try {
      final BigDecimal position = new BigDecimal(convertableNumber);
      return new DecimalFormat("0000.0").format(position);
    } catch (final NumberFormatException | ArithmeticException exception) {
      throw new InvalidIssueNumberException(number, exception);
    }
  }

  @Override
  public String toString() {
    return format(
        "Comic[id=%s, series='%s', volume='%s', number='%s']",
        this.id, this.series, this.volume, this.number);
  }
}
