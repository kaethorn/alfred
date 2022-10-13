package de.wasenweg.alfred.unit;

import de.wasenweg.alfred.scanner.ComicVineService;
import de.wasenweg.alfred.settings.SettingsService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.env.Environment;
import org.springframework.core.env.Profiles;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.RestTemplate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ComicVineServiceTest {

  @Mock
  private transient RestTemplate restTemplate;

  @Mock
  private transient Environment environment;

  @Mock
  private transient SettingsService settingsService;

  @InjectMocks
  private transient ComicVineService comicVineService;

  @BeforeEach
  public void setUp() {
    when(this.settingsService.get("comics.comicVine.ApiKey")).thenReturn("987");
    when(this.environment.acceptsProfiles(any(Profiles.class))).thenReturn(true);
    ReflectionTestUtils.setField(this.comicVineService, "baseUrl", "http://foo");
    ReflectionTestUtils.setField(this.comicVineService, "restTemplate", this.restTemplate);
    this.comicVineService.setup();
  }

  @Test
  void throttlesRequests() throws Exception {
    when(this.restTemplate.exchange(any(String.class), eq(HttpMethod.GET), any(HttpEntity.class), eq(String.class)))
        .thenReturn(new ResponseEntity<String>("{}", HttpStatus.ACCEPTED));

    final long start = System.nanoTime();
    this.comicVineService.getIssueDetails("http://foo/api/1");
    this.comicVineService.getIssueDetails("http://foo/api/2");
    final long end = System.nanoTime();
    // 200 requests per hour, means 18s between requests.
    assertThat((double) (end - start) / 1_000_000_000).isBetween(17d, 20d);
  }

  @Test
  void handlesInvalidResponses() throws Exception {
    when(this.restTemplate.exchange(any(String.class), eq(HttpMethod.GET), any(HttpEntity.class), eq(String.class)))
        .thenReturn(new ResponseEntity<String>("<html />", HttpStatus.ACCEPTED));

    assertThat(this.comicVineService.getIssueDetails("http://foo/api/1")).isNull();
  }
}
