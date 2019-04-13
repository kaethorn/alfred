package de.wasenweg.komix.comics;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@RequiredArgsConstructor
@Document
public class Comic {

    @Id
    private String id;

    @NonNull
    private String path;
    @NonNull
    private String title;
    @NonNull
    private String series;
    @NonNull
    private String volume;
    @NonNull
    private String number;
    @NonNull
    private String position;
    @NonNull
    private Short year;
    @NonNull
    private Short month;
    @NonNull
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

    private byte[] thumbnail;

    @Builder.Default
    private boolean read = false;
    @Builder.Default
    private Short currentPage = (short) 0;
    private Date lastRead;

    @Override
    public String toString() {
        return String.format(
                "Comic[id=%s, series='%s', volume='%s', number='%s']",
                id, series, volume, number);
    }
}
