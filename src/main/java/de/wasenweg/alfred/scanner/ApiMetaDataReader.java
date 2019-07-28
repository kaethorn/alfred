package de.wasenweg.alfred.scanner;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import de.wasenweg.alfred.comics.Comic;
import de.wasenweg.alfred.settings.SettingsService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

@Service
public class ApiMetaDataReader {

  private SettingsService settingsService;

  private Logger logger = LoggerFactory.getLogger(ApiMetaDataReader.class);

  private List<ScannerIssue> scannerIssues = new ArrayList<ScannerIssue>();
  private Pattern pattern;
  private String baseUrl = "https://comicvine.gamespot.com/api/";
  private String apiKey;
  private ObjectMapper mapper;

  @Autowired
  public ApiMetaDataReader(final SettingsService settingsService) {
    this.settingsService = settingsService;
    this.mapper = new ObjectMapper();

    final String publisherDirPattern = "^.*?(?<publisher>[^/]+)/";
    final String seriesDirPattern = "(?<series1>[^/]+) \\((?<volume1>\\d{4})\\)/";
    final String fileNamePattern = "(?<series2>[^/]+) (?<number>[\\d\\.a½/]+) \\((?<volume2>\\d{4})\\) [^/]+\\.cbz$";
    this.pattern = Pattern
        .compile(publisherDirPattern + seriesDirPattern + fileNamePattern);

    this.apiKey = this.settingsService.get("comics.comicVineApiKey");
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

  private String encodeValue(final String value) {
    try {
      return URLEncoder.encode(value, StandardCharsets.UTF_8.toString());
    } catch (final UnsupportedEncodingException exception) {
      exception.printStackTrace();
      return "";
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
    this.query(comic);

    return this.scannerIssues;
  }

  private void query(final Comic comic) {
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

  private String getSearchUrl(final String series) {
    final Map<String, String> requestParams = new HashMap<>();
    requestParams.put("resources", "volume");
    requestParams.put("query", series);
    requestParams.put("api_key", this.apiKey);
    requestParams.put("format", "json");

    final String url = this.baseUrl + "search/?";
    return requestParams.keySet().stream()
        .map(key -> key + "=" + this.encodeValue(requestParams.get(key)))
        .collect(Collectors.joining("&", url, ""));
  }

  private String getIssuesUrl(final String volumeId) {
    final Map<String, String> requestParams = new HashMap<>();
    requestParams.put("filter", "volume:" + volumeId);
    requestParams.put("api_key", this.apiKey);
    requestParams.put("format", "json");

    final String url = this.baseUrl + "issues/?";
    return requestParams.keySet().stream()
        .map(key -> key + "=" + this.encodeValue(requestParams.get(key)))
        .collect(Collectors.joining("&", url, ""));
  }

  private String findVolumeId(final String series) {
    final String url = this.getSearchUrl(series);
    final RestTemplate restTemplate = new RestTemplate();

    final HttpHeaders headers = new HttpHeaders();
    headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
    headers.add("user-agent", "curl/7.52.1");
    final HttpEntity<String> entity = new HttpEntity<String>("parameters", headers);

    final ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, entity, String.class);

    JsonNode root = null;
    try {
      root = this.mapper.readTree(response.getBody());
    } catch (final IOException exception) {
      exception.printStackTrace();
    }
    final JsonNode results = root.get("results");

    final Stream<JsonNode> volumes = IntStream.range(0, results.size()).mapToObj(results::get);
    return volumes.findAny().toString();
  }

  private void queryApi(final Comic comic) {
    // FIXME account for throttling (200 requests/h).
    // 1. Fetch issues by publisher, series and volume:
    //   a. Search for volume by series: `search/?resources=volume&query=Batman`.
    //   b. Match publisher on `publisher.name`.
    //   c. Match volume on `start_year`.
    //   d. The `id` is used to fetch all issues:
    //   e. `issues/?filter=volume:796&sort=issue_number:asc`.
    this.findVolumeId(comic.getSeries());


    // 2. The response will indicate how often the request needs to be repeated in order
    //    to fetch all issues (`number_of_total_results` & `offset`).
    // 3. Fetch all issues in the volumes, in chunks of 100.
    // 4. Cache all results

    //this.getIssuesUrl("TODO123");

    // TODO query & cache result.
  }
}
