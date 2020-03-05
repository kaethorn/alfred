package de.wasenweg.alfred.scanner;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.util.concurrent.RateLimiter;
import de.wasenweg.alfred.settings.SettingsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.core.env.Profiles;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import javax.annotation.PostConstruct;

import java.io.IOException;
import java.util.Arrays;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class ComicVineService {


  @Value("${comics.comicVine.baseUrl:https://comicvine.gamespot.com/api/}")
  private String baseUrl;

  private final SettingsService settingsService;
  private final Environment environment;

  private final ObjectMapper mapper = new ObjectMapper();
  // Throttle to 200 requests per hour
  private final RateLimiter throttle = RateLimiter.create(200.0 / 3600.0);
  private final RestTemplate restTemplate = new RestTemplate();

  private String apiKey;

  @PostConstruct
  public void setup() {
    this.apiKey = this.settingsService.get("comics.comicVine.ApiKey");
  }

  // Get details about a specific issue
  public JsonNode getIssueDetails(final String detailsUrl) {
    final String path = detailsUrl.split("/api/")[1];
    final Map<String, String> requestParams = new ConcurrentHashMap<>();
    requestParams.put("api_key", this.apiKey);
    requestParams.put("format", "json");
    requestParams.put("field_list",
        "character_credits,cover_date,location_credits,team_credits,person_credits,description,site_detail_url");

    final String url = requestParams.keySet().stream()
        .map(key -> key + "=" + requestParams.get(key))
        .collect(Collectors.joining("&", this.baseUrl + path + "?", ""));

    return this.query(url);
  }

  // Search for volumes by name
  public JsonNode findVolumesBySeries(final String series, final int page) {
    // The query parameter will match entries matching any one of the terms, so searching
    // for `Foo Bar` will match `Foo` and `Bar` entries. This looks like a bug on the API
    // side. As a workaround, using the first term actually improves result numbers.
    final String query = series.split(" ")[0];

    final Map<String, String> requestParams = new ConcurrentHashMap<>();
    requestParams.put("api_key", this.apiKey);
    requestParams.put("format", "json");
    requestParams.put("resources", "volume");
    requestParams.put("query", query);
    requestParams.put("field_list", "id,name,publisher,start_year");
    requestParams.put("offset", String.valueOf(page * 10));

    final String url = requestParams.keySet().stream()
        .map(key -> key + "=" + requestParams.get(key))
        .collect(Collectors.joining("&", this.baseUrl + "search/?", ""));

    return this.query(url);
  }

  // Get basic details about all issues in a volume
  public JsonNode findIssuesInVolume(final String volumeId, final int page) {
    final Map<String, String> requestParams = new ConcurrentHashMap<>();
    requestParams.put("api_key", this.apiKey);
    requestParams.put("format", "json");
    requestParams.put("filter", "volume:" + volumeId);
    requestParams.put("field_list", "id,name,api_detail_url,issue_number");
    requestParams.put("offset", String.valueOf(page * 100));

    final String url = requestParams.keySet().stream()
        .map(key -> key + "=" + requestParams.get(key))
        .collect(Collectors.joining("&", this.baseUrl + "issues/?", ""));

    return this.query(url);
  }

  private JsonNode query(final String url) {
    if (this.environment.acceptsProfiles(Profiles.of("prod"))) {
      this.throttle.acquire();
    }
    final HttpHeaders headers = new HttpHeaders();
    headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
    headers.add("user-agent", "curl/7.52.1");
    final HttpEntity<String> entity = new HttpEntity<>("parameters", headers);
    final ResponseEntity<String> response = this.restTemplate.exchange(url, HttpMethod.GET, entity, String.class);
    try {
      return this.mapper.readTree(response.getBody());
    } catch (final IOException exception) {
      log.error("Error parsing response", exception);
      return null;
    }
  }
}
