package de.wasenweg.alfred.comics;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;
import java.util.List;

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
  private String title;

  @NonNull
  @Indexed
  private String series;

  @NonNull
  @Indexed
  private String volume;

  @NonNull
  private String number;

  @NonNull
  @Indexed
  private String position;

  @NonNull
  private Short year;

  @NonNull
  private Short month;

  @NonNull
  @Indexed
  private String publisher;

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

  private Short pageCount;
  private boolean manga;

  private String characters;
  private String teams;
  private String locations;

  private String nextId;
  private String previousId;

  @Builder.Default
  private boolean read = false;

  private List<String> errors;

  @Builder.Default
  private Short currentPage = (short) 0;

  private Date lastRead;

  public Comic() {
    this.read = false;
    this.currentPage = (short) 0;
  }

  @Override
  public String toString() {
    return String.format(
        "Comic[id=%s, series='%s', volume='%s', number='%s']",
        this.id, this.series, this.volume, this.number);
  }
}
