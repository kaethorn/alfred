package de.wasenweg.alfred.scanner;

import com.fasterxml.jackson.databind.JsonNode;

import de.wasenweg.alfred.comics.Comic;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

@Service
public class ApiMetaDataService {

  private ComicVineService comicVineService;

  private Logger logger = LoggerFactory.getLogger(ApiMetaDataService.class);

  private List<ScannerIssue> scannerIssues = new ArrayList<ScannerIssue>();
  private Pattern pattern;

  @Autowired
  public ApiMetaDataService(final ComicVineService comicVineService) {
    this.comicVineService = comicVineService;

    final String publisherDirPattern = "^.*?(?<publisher>[^/]+)/";
    final String seriesDirPattern = "(?<series1>[^/]+) \\((?<volume1>\\d{4})\\)/";
    final String fileNamePattern = "(?<series2>[^/]+) (?<number>[\\d\\.a½/]+) \\((?<volume2>\\d{4})\\)( [^/]+)?\\.cbz$";
    this.pattern = Pattern
        .compile(publisherDirPattern + seriesDirPattern + fileNamePattern);
  }

  private List<String> findMissingAttributes(final Comic comic) {
    final List<String> missingAttributes = new ArrayList<String>();
    if (comic.getPublisher() == null) {
      missingAttributes.add("publisher");
    }
    if (comic.getSeries() == null) {
      missingAttributes.add("series");
    }
    if (comic.getVolume() == null) {
      missingAttributes.add("volume");
    }
    if (comic.getNumber() == null) {
      missingAttributes.add("number");
    }
    return missingAttributes;
  }

  private Boolean isValid(final Comic comic) {
    return this.findMissingAttributes(comic).isEmpty();
  }

  private String mapPosition(final String number) {
    try {
      return Comic.mapPosition(number);
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
   * `/{publisher}/{series} ({volume})/{series} #{number} ({volume}).cbz`
   */
  private void setPathParts(final Comic comic) {
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
   * Scrapes and saves information for the given comic.
   *
   * API lookup information is extracted from the file path and/or existing meta
   * data stored in the embedded XML file.
   *
   * @param comic The comic book entity.
   * @return
   */
  public List<ScannerIssue> set(final Comic comic) {
    this.scannerIssues.clear();

    if (!this.isValid(comic)) {
      // Attempt to extract meta data from file path
      this.setPathParts(comic);
    }

    // If neither the XML nor the file path contain enough hints about which
    // comic book this is, we inform the user.
    final List<String> missingAttributes = this.findMissingAttributes(comic);
    if (missingAttributes.size() > 0) {
      this.scannerIssues.add(ScannerIssue.builder()
          .message("Missing meta data: " + String.join(", ", missingAttributes))
          .type(ScannerIssue.Type.ERROR)
          .build());
    }

    // Here we can assume to have enough meta data about the comic to make
    // a query to the Comic Vine API.
    try {
      this.query(comic);
    } catch (final Exception exception) {
      this.logger.error("Error while fetching information for " + comic.getPath(), exception);
      this.scannerIssues.add(ScannerIssue.builder()
          .message("Error during Comic Vine API meta data retrieval")
          .type(ScannerIssue.Type.ERROR)
          .build());
    }

    return this.scannerIssues;
  }

  private List<JsonNode> filterVolumeSearchResults(
      final String publisher, final String series, final String volume, final JsonNode results) {
    final Stream<JsonNode> volumes = IntStream.range(0, results.size()).mapToObj(results::get);
    return volumes.filter(v -> {
      return publisher.equals(v.get("publisher").get("name").asText())
          && series.equals(v.get("name").asText())
          && volume.equals(v.get("start_year").asText());
    }).collect(Collectors.toList());
  }

  private String findIssueDetailsUrl(final Comic comic, final List<JsonNode> issues) {
    final List<JsonNode> filteredIssues = issues.stream()
        .filter(issue -> {
          return issue.get("issue_number").asText().equals(comic.getNumber());
        })
        .collect(Collectors.toList());

    if (filteredIssues.size() == 0) {
      this.scannerIssues.add(ScannerIssue.builder()
          .message("No matching issue found")
          .type(ScannerIssue.Type.ERROR)
          .build());
    }
    if (filteredIssues.size() > 1) {
      this.scannerIssues.add(ScannerIssue.builder()
          .message("No unique issue found")
          .type(ScannerIssue.Type.ERROR)
          .build());
    }

    return filteredIssues.get(0).get("api_detail_url").asText();
  }

  @Cacheable("volumeIds")
  private String findVolumeId(final String publisher, final String series, final String volume) {
    int page = 0;
    JsonNode response = this.comicVineService.findVolumesBySeries(series, page);
    List<JsonNode> results = this.filterVolumeSearchResults(publisher, series, volume, response.get("results"));

    final int totalCount = response.get("number_of_total_results").asInt();
    final int limit = response.get("limit").asInt();
    final int lastPage = totalCount / limit;
    while (results.size() == 0 && page < lastPage) {
      page++;
      response = this.comicVineService.findVolumesBySeries(series, page);
      results = this.filterVolumeSearchResults(publisher, series, volume, response.get("results"));
    }

    if (results.size() > 0) {
      return results.get(0).get("id").asText();
    } else {
      this.scannerIssues.add(ScannerIssue.builder()
          .message("No result in volume search")
          .type(ScannerIssue.Type.ERROR)
          .build());
      return "";
    }
  }

  @Cacheable("volumeIssues")
  private List<JsonNode> findVolumeIssues(final String volumeId) {
    int page = 0;
    final JsonNode response = this.comicVineService.findIssuesInVolume(volumeId, page);
    JsonNode results = response.get("results");
    final List<JsonNode> issues = IntStream.range(0, results.size()).mapToObj(results::get)
        .collect(Collectors.toList());

    final int totalCount = response.get("number_of_total_results").asInt();
    final int limit = response.get("limit").asInt();
    final int lastPage = totalCount / limit;
    while (page < lastPage) {
      page++;
      results = this.comicVineService.findIssuesInVolume(volumeId, page).get("results");
      issues.addAll(IntStream.range(0, results.size()).mapToObj(results::get)
          .collect(Collectors.toList()));
    }

    if (issues.isEmpty()) {
      this.scannerIssues.add(ScannerIssue.builder()
          .message("Empty volume")
          .type(ScannerIssue.Type.ERROR)
          .build());
    }
    return issues;
  }

  private String getEntities(final JsonNode entities) {
    return IntStream.range(0, entities.size()).mapToObj(entities::get)
        .map(character -> character.get("name").asText())
        .collect(Collectors.joining(", "));
  }

  private String getCharacters(final JsonNode details) {
    if (details.has("character_credits")) {
      return this.getEntities(details.get("character_credits"));
    }
    return "";
  }

  private String getTeams(final JsonNode details) {
    if (details.has("team_credits")) {
      return this.getEntities(details.get("team_credits"));
    }
    return "";
  }

  private String getLocations(final JsonNode details) {
    if (details.has("location_credits")) {
      return this.getEntities(details.get("location_credits"));
    }
    return "";
  }

  /**
   * Gathers a comma separated list of persons per role.
   * @param details The array of persons
   * @return
   */
  private Map<String, String> getPersons(final JsonNode details) {
    final JsonNode persons = details.get("person_credits");
    return IntStream.range(0, persons.size())
        .mapToObj(persons::get)
        .collect(Collectors.groupingBy(
            person -> person.get("role").asText(),
            Collectors.mapping(
                person -> person.get("name").asText(),
                Collectors.joining(", "))));
  }

  private String getNodeText(final JsonNode response, final String key) {
    if (response.has(key)) {
      return response.get(key).asText();
    }
    return "";
  }

  private void applyIssueDetails(final String url, final Comic comic) {
    final JsonNode response = this.comicVineService.getIssueDetails(url).get("results");
    comic.setTitle(this.getNodeText(response, "name"));
    comic.setSummary(this.getNodeText(response, "description"));
    final String[] coverDate = response.get("cover_date").asText().split("-");
    comic.setYear(Short.valueOf(coverDate[0]));
    comic.setMonth(Short.valueOf(coverDate[1]));
    comic.setCharacters(this.getCharacters(response));
    comic.setTeams(this.getTeams(response));
    comic.setLocations(this.getLocations(response));
    final Map<String, String> persons = this.getPersons(response);
    comic.setWriter(persons.get("writer"));
    comic.setPenciller(persons.get("penciller"));
    comic.setInker(persons.get("inker"));
    comic.setColorist(persons.get("colorist"));
    comic.setLetterer(persons.get("letterer"));
    comic.setCoverArtist(persons.get("artist, cover"));
    comic.setEditor(persons.get("editor"));
    comic.setWeb(response.get("site_detail_url").asText());
  }

  private void query(final Comic comic) {
    final String volumeId = this.findVolumeId(comic.getPublisher(), comic.getSeries(), comic.getVolume());
    if ("".equals(volumeId)) {
      return;
    }
    final List<JsonNode> issues = this.findVolumeIssues(volumeId);
    if (issues.isEmpty()) {
      return;
    }
    final String issueDetailsUrl = this.findIssueDetailsUrl(comic, issues);
    this.applyIssueDetails(issueDetailsUrl, comic);
  }
}
