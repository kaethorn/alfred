package de.wasenweg.alfred.scanner;

import com.fasterxml.jackson.databind.JsonNode;

import de.wasenweg.alfred.comics.Comic;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

@Service
public class ApiMetaDataReader {

  private ComicVineService comicVineService;

  private Logger logger = LoggerFactory.getLogger(ApiMetaDataReader.class);

  private List<ScannerIssue> scannerIssues = new ArrayList<ScannerIssue>();
  private Pattern pattern;

  @Autowired
  public ApiMetaDataReader(final ComicVineService comicVineService) {
    this.comicVineService = comicVineService;

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
   * `/{publisher}/{series}/{series} #{number} ({volume}).cbz`
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
    try {
      this.query(comic);
    } catch (final Exception exception) {
      // TODO ass to this.scannerIssues
    }

    return this.scannerIssues;
  }

  private void query(final Comic comic) throws Exception {
    // TODO query cache
    if (this.queryCache(comic)) {
      return;
    }
    this.queryApi(comic);
  }

  private Boolean queryCache(final Comic comic) {
    // The cache will either contain all issues in the volume, or none at all.
    // TODO
    return false;
  }

  private List<JsonNode> filterVolumeSearchResults(final Comic comic, final JsonNode results) {
    final Stream<JsonNode> volumes = IntStream.range(0, results.size()).mapToObj(results::get);
    return volumes.filter(volume -> {
      final String publisher = volume.get("publisher").get("name").asText();
      final String volumeYear = volume.get("start_year").asText();
      final String name = volume.get("name").asText();
      return publisher.equals(comic.getPublisher())
          && volumeYear.equals(comic.getVolume())
          && name.equals(comic.getSeries());
    }).collect(Collectors.toList());
  }

  private String findVolumeId(final Comic comic) throws Exception {
    int page = 1;
    JsonNode response = this.comicVineService.findVolumesBySeries(comic.getSeries(), page);
    List<JsonNode> results = this.filterVolumeSearchResults(comic, response.get("results"));

    final int totalCount = response.get("number_of_total_results").asInt();
    final int limit = response.get("limit").asInt();
    final int lastPage = (totalCount + limit - 1) / limit; // Round up on division
    while (results.size() == 0 && page <= lastPage) {
      response = this.comicVineService.findVolumesBySeries(comic.getSeries(), page);
      results = this.filterVolumeSearchResults(comic, response.get("results"));
      page++;
    }

    if (results.size() > 0) {
      return results.get(0).get("id").asText();
    } else {
      throw new Exception("No result in volume search");
    }
  }

  private List<JsonNode> findVolumeIssues(final String volumeId) {
    final JsonNode response = this.comicVineService.findIssuesInVolume(volumeId);
    final JsonNode results = response.get("results");
    return IntStream.range(0, results.size()).mapToObj(results::get)
        .collect(Collectors.toList());
  }

  private void queryApi(final Comic comic) throws Exception {
    // TODO account for throttling (200 requests/h).
    // TODO query & cache result.
    final String volumeId = this.findVolumeId(comic);
    final List<JsonNode> issues = this.findVolumeIssues(volumeId);
  }
}
