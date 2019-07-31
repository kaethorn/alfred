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
      // TODO add to this.scannerIssues
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

  @Cacheable("volumeIds")
  private String findVolumeId(final String publisher, final String series, final String volume) throws Exception {
    int page = 1;
    JsonNode response = this.comicVineService.findVolumesBySeries(series, page);
    List<JsonNode> results = this.filterVolumeSearchResults(publisher, series, volume, response.get("results"));

    final int totalCount = response.get("number_of_total_results").asInt();
    final int limit = response.get("limit").asInt();
    final int lastPage = (totalCount + limit - 1) / limit; // Round up on division
    while (results.size() == 0 && page <= lastPage) {
      response = this.comicVineService.findVolumesBySeries(series, page);
      results = this.filterVolumeSearchResults(publisher, series, volume, response.get("results"));
      page++;
    }

    if (results.size() > 0) {
      return results.get(0).get("id").asText();
    } else {
      throw new Exception("No result in volume search");
    }
  }

  @Cacheable("volumeIssues")
  private List<JsonNode> findVolumeIssues(final String volumeId) {
    final JsonNode response = this.comicVineService.findIssuesInVolume(volumeId);
    final JsonNode results = response.get("results");
    return IntStream.range(0, results.size()).mapToObj(results::get)
        .collect(Collectors.toList());
  }

  private void query(final Comic comic) throws Exception {
    // TODO account for throttling (200 requests/h).
    // TODO cache results for search and issues (single issues are fetched one by one)
    final String volumeId = this.findVolumeId(comic.getPublisher(), comic.getSeries(), comic.getVolume());
    final List<JsonNode> issues = this.findVolumeIssues(volumeId);
  }
}
