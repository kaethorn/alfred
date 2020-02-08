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
import java.util.Date;
import java.util.List;

import static java.lang.String.format;

@Data
@Builder
@AllArgsConstructor
@RequiredArgsConstructor
@Document
public class Comic {

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

  private Short year;

  private Short month;

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

  private Short pageCount;
  private String nextId;
  private String previousId;

  @Builder.Default
  private boolean read = false;

  private List<ScannerIssue> errors;
  private List<String> files;

  @Builder.Default
  private Short currentPage = (short) 0;

  private Date lastRead;

  public Comic() {
    this.read = false;
    this.currentPage = (short) 0;
  }

  public void setNumber(final String number) {
    this.number = number;
    this.position = Comic.mapPosition(number);
  }

  public static String mapPosition(final String number) throws InvalidIssueNumberException {
    String convertableNumber = number;
    if ("½".equals(number) || "1/2".equals(number)) {
      convertableNumber = "0.5";
    }
    if (number.endsWith("a")) {
      convertableNumber = convertableNumber.replace("a", ".5");
    }
    BigDecimal position = new BigDecimal(0);
    try {
      position = new BigDecimal(convertableNumber);
    } catch (final Exception exception) {
      throw new InvalidIssueNumberException(number);
    }
    return new DecimalFormat("0000.0").format(position);
  }

  @Override
  public String toString() {
    return format(
        "Comic[id=%s, series='%s', volume='%s', number='%s']",
        this.id, this.series, this.volume, this.number);
  }
}
