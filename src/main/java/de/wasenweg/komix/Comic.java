package de.wasenweg.komix;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.Table;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Entity
@Table(name = "COMICS")
public class Comic {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Lob
    private String path;

    @NotBlank
    @Size(max = 255)
    private String title;

    @NotBlank
    @Size(max = 255)
    private String series;

    @NotBlank
    @Size(max = 10)
    private String number;

    @NotBlank
    @Size(max = 6)
    private String position;

    @Size(max = 255)
    private String volume;

    @Lob
    private String summary;

    @Lob
    private String notes;

    private Short year;

    private Short month;

    @Lob
    private String writer;

    @Lob
    private String penciller;

    @Lob
    private String inker;

    @Lob
    private String colorist;

    @Lob
    private String letterer;

    @Lob
    private String coverArtist;

    @Lob
    private String editor;

    @NotBlank
    @Size(max = 255)
    private String publisher;

    @Lob
    private String web;

    private Short pageCount;

    private boolean manga;

    @Lob
    private String characters;

    @Lob
    private String teams;

    @Lob
    private byte[] thumbnail;

    public Comic() {
    }

    public Comic(final String path, final String title,
            final String series, final String number,
            final String position, final Short year,
            final Short month, final String publisher) {
        this.path = path;
        this.title = title;
        this.series = series;
        this.number = number;
        this.position = position;
        this.year = year;
        this.month = month;
        this.publisher = publisher;
    }

    public Long getId() {
        return id;
    }

    public void setId(final Long id) {
        this.id = id;
    }

    public String getPath() {
        return path;
    }

    public void setPath(final String path) {
        this.path = path;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(final String title) {
        this.title = title;
    }

    public String getSeries() {
        return series;
    }

    public void setSeries(final String series) {
        this.series = series;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(final String number) {
        this.number = number;
    }

    public String getPosition() {
        return position;
    }

    public void setPosition(final String position) {
        this.position = position;
    }

    public String getVolume() {
        return volume;
    }

    public void setVolume(final String volume) {
        this.volume = volume;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(final String summary) {
        this.summary = summary;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(final String notes) {
        this.notes = notes;
    }

    public Short getYear() {
        return year;
    }

    public void setYear(final Short year) {
        this.year = year;
    }

    public Short getMonth() {
        return month;
    }

    public void setMonth(final Short month) {
        this.month = month;
    }

    public String getWriter() {
        return writer;
    }

    public void setWriter(final String writer) {
        this.writer = writer;
    }

    public String getPenciller() {
        return penciller;
    }

    public void setPenciller(final String penciller) {
        this.penciller = penciller;
    }

    public String getInker() {
        return inker;
    }

    public void setInker(final String inker) {
        this.inker = inker;
    }

    public String getColorist() {
        return colorist;
    }

    public void setColorist(final String colorist) {
        this.colorist = colorist;
    }

    public String getLetterer() {
        return letterer;
    }

    public void setLetterer(final String letterer) {
        this.letterer = letterer;
    }

    public String getCoverArtist() {
        return coverArtist;
    }

    public void setCoverArtist(final String coverArtist) {
        this.coverArtist = coverArtist;
    }

    public String getEditor() {
        return editor;
    }

    public void setEditor(final String editor) {
        this.editor = editor;
    }

    public String getPublisher() {
        return publisher;
    }

    public void setPublisher(final String publisher) {
        this.publisher = publisher;
    }

    public String getWeb() {
        return web;
    }

    public void setWeb(final String web) {
        this.web = web;
    }

    public Short getPageCount() {
        return pageCount;
    }

    public void setPageCount(final Short pageCount) {
        this.pageCount = pageCount;
    }

    public boolean isManga() {
        return manga;
    }

    public void setManga(final boolean manga) {
        this.manga = manga;
    }

    public String getCharacters() {
        return characters;
    }

    public void setCharacters(final String characters) {
        this.characters = characters;
    }

    public String getTeams() {
        return teams;
    }

    public void setTeams(final String teams) {
        this.teams = teams;
    }

    public byte[] getThumbnail() {
        return thumbnail;
    }

    public void setThumbnail(final byte[] thumbnail) {
        this.thumbnail = thumbnail;
    }
}
