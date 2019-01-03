package de.wasenweg.comix;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.Table;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Entity
@Table(name = "COMICS")
public class Comic {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

	@NotBlank
	@Lob
    private String path;

	@NotBlank
	@Size(max = 255)
    private String title;

	@NotBlank
	@Size(max = 255)
    private String series;
	
	@NotNull
	private Short number;
	
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
	private String cover_artist;
	
	@Lob
	private String editor;
	
	@NotBlank
	@Size(max = 255)
	private String publisher;

	@Lob
	private String web;
	
	private Short page_count;
	
	private boolean manga;

	@Lob
	private String characters;

	@Lob
	private String teams;
	
	public Comic() {}

	public Comic(String path, String title, String series, Short number, Short year, Short month, String publisher) {
		this.path = path;
		this.title = title;
		this.series = series;
		this.number = number;
		this.year = year;
		this.month = month;
		this.publisher = publisher;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getSeries() {
		return series;
	}

	public void setSeries(String series) {
		this.series = series;
	}

	public Short getNumber() {
		return number;
	}

	public void setNumber(Short number) {
		this.number = number;
	}

	public String getVolume() {
		return volume;
	}

	public void setVolume(String volume) {
		this.volume = volume;
	}

	public String getSummary() {
		return summary;
	}

	public void setSummary(String summary) {
		this.summary = summary;
	}

	public String getNotes() {
		return notes;
	}

	public void setNotes(String notes) {
		this.notes = notes;
	}

	public Short getYear() {
		return year;
	}

	public void setYear(Short year) {
		this.year = year;
	}

	public Short getMonth() {
		return month;
	}

	public void setMonth(Short month) {
		this.month = month;
	}

	public String getWriter() {
		return writer;
	}

	public void setWriter(String writer) {
		this.writer = writer;
	}

	public String getPenciller() {
		return penciller;
	}

	public void setPenciller(String penciller) {
		this.penciller = penciller;
	}

	public String getInker() {
		return inker;
	}

	public void setInker(String inker) {
		this.inker = inker;
	}

	public String getColorist() {
		return colorist;
	}

	public void setColorist(String colorist) {
		this.colorist = colorist;
	}

	public String getLetterer() {
		return letterer;
	}

	public void setLetterer(String letterer) {
		this.letterer = letterer;
	}

	public String getCover_artist() {
		return cover_artist;
	}

	public void setCover_artist(String cover_artist) {
		this.cover_artist = cover_artist;
	}

	public String getEditor() {
		return editor;
	}

	public void setEditor(String editor) {
		this.editor = editor;
	}

	public String getPublisher() {
		return publisher;
	}

	public void setPublisher(String publisher) {
		this.publisher = publisher;
	}

	public String getWeb() {
		return web;
	}

	public void setWeb(String web) {
		this.web = web;
	}

	public Short getPage_count() {
		return page_count;
	}

	public void setPage_count(Short page_count) {
		this.page_count = page_count;
	}

	public boolean isManga() {
		return manga;
	}

	public void setManga(boolean manga) {
		this.manga = manga;
	}

	public String getCharacters() {
		return characters;
	}

	public void setCharacters(String characters) {
		this.characters = characters;
	}

	public String getTeams() {
		return teams;
	}

	public void setTeams(String teams) {
		this.teams = teams;
	}
}