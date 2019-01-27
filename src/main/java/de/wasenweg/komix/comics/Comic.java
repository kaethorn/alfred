package de.wasenweg.komix.comics;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
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
    private String number;
    @NonNull
    private String position;
    @NonNull
    private Short year;
    @NonNull
    private Short month;
    @NonNull
    private String publisher;

    private String volume;
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
    private byte[] thumbnail;

    @Override
    public String toString() {
        return String.format(
                "Comic[id=%s, series='%s', volume='%s', number='%s']",
                id, series, volume, number);
    }
}
