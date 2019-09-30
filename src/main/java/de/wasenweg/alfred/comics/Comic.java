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

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.Date;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@RequiredArgsConstructor
@Document
@XmlRootElement(name = "ComicInfo")
public class Comic {

  @Id
  private String id;

  @NonNull
  private String path;

  @NonNull
  private String fileName;

  @NonNull
  @Indexed
  @XmlElement(name = "Publisher")
  private String publisher;

  @NonNull
  @Indexed
  @XmlElement(name = "Series")
  private String series;

  @NonNull
  @Indexed
  @XmlElement(name = "Volume")
  private String volume;

  @NonNull
  @XmlElement(name = "Number")
  private String number;

  @Indexed
  private String position;

  @XmlElement(name = "Year")
  private Short year;

  @XmlElement(name = "Month")
  private Short month;

  @XmlElement(name = "Title")
  private String title;

  @XmlElement(name = "Summary")
  private String summary;

  @XmlElement(name = "Notes")
  private String notes;

  @XmlElement(name = "Writer")
  private String writer;

  @XmlElement(name = "Penciller")
  private String penciller;

  @XmlElement(name = "Inker")
  private String inker;

  @XmlElement(name = "Colorist")
  private String colorist;

  @XmlElement(name = "Letterer")
  private String letterer;

  @XmlElement(name = "CoverArtist")
  private String coverArtist;

  @XmlElement(name = "Editor")
  private String editor;

  @XmlElement(name = "Web")
  private String web;

  @XmlElement(name = "Manga")
  private boolean manga;

  @XmlElement(name = "Characters")
  private String characters;

  @XmlElement(name = "Teams")
  private String teams;

  @XmlElement(name = "Locations")
  private String locations;

  private Short pageCount;
  private String nextId;
  private String previousId;

  @Builder.Default
  private boolean read = false;

  private List<ScannerIssue> errors;

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
    return String.format(
        "Comic[id=%s, series='%s', volume='%s', number='%s']",
        this.id, this.series, this.volume, this.number);
  }
}
