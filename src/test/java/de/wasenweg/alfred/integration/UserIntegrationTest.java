package de.wasenweg.alfred.integration;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import de.wasenweg.alfred.EnableEmbeddedMongo;
import de.wasenweg.alfred.fixtures.SecurityFixtures;
import de.wasenweg.alfred.security.JwtCreator;
import de.wasenweg.alfred.settings.SettingsService;
import de.wasenweg.alfred.unit.TestHelper;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(properties = { "auth.jwt.secret=secret" })
@AutoConfigureMockMvc
@EnableEmbeddedMongo
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@ActiveProfiles("prod")
public class UserIntegrationTest {

  private final MockMvc mockMvc;

  @Mock
  private transient GoogleIdToken googleIdToken;

  @Mock
  private transient GoogleIdTokenVerifier googleIdTokenVerifier;

  @MockBean
  private transient JwtCreator jwtCreator;

  @MockBean
  private transient GoogleIdTokenVerifier.Builder googleIdTokenVerifierBuilder;

  @MockBean
  private transient SettingsService settingsService;

  @Test
  public void verify() throws Exception {
    final String token = TestHelper.readString("jwt-valid.txt");
    this.mockMvc.perform(get("/api/user/verify/" + token))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
        .andExpect(jsonPath("$.token").value(token));
  }

  @Test
  public void verifyUnauthenticated() throws Exception {
    this.mockMvc.perform(get("/api/user/verify/invalid-token-123"))
        .andExpect(status().isUnauthorized());
  }

  @Test
  public void signIn() throws Exception {
    when(this.settingsService.get("auth.users")).thenReturn("foo@bar.com,bar@foo.com");
    when(this.googleIdTokenVerifier.verify("mock-123")).thenReturn(this.googleIdToken);
    when(this.googleIdTokenVerifierBuilder.build()).thenReturn(this.googleIdTokenVerifier);
    when(this.googleIdTokenVerifierBuilder.setAudience(any())).thenReturn(this.googleIdTokenVerifierBuilder);
    when(this.googleIdToken.getPayload()).thenReturn(SecurityFixtures.getMockPayload());
    when(this.jwtCreator.issueToken(any(), any(), any())).thenReturn("mock-token-123");

    this.mockMvc.perform(post("/api/user/sign-in/mock-123"))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
        .andExpect(jsonPath("$.token").value("mock-token-123"))
        .andExpect(jsonPath("$.email").value("foo@bar.com"))
        .andExpect(jsonPath("$.name").value("Foo Bar"))
        .andExpect(jsonPath("$.picture").value(""));
  }

  @Test
  public void signInWithoutValidUser() throws Exception {
    when(this.settingsService.get("auth.users")).thenReturn("bar@foo.com");
    when(this.googleIdTokenVerifier.verify("mock-123")).thenReturn(this.googleIdToken);
    when(this.googleIdTokenVerifierBuilder.build()).thenReturn(this.googleIdTokenVerifier);
    when(this.googleIdTokenVerifierBuilder.setAudience(any())).thenReturn(this.googleIdTokenVerifierBuilder);
    when(this.googleIdToken.getPayload()).thenReturn(SecurityFixtures.getMockPayload());
    when(this.jwtCreator.issueToken(any(), any(), any())).thenReturn("mock-token-123");

    this.mockMvc.perform(post("/api/user/sign-in/mock-123"))
        .andExpect(status().isForbidden())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
        .andExpect(jsonPath("$.message").value("User not allowed."));
  }

  @Test
  public void signInWithoutValidToken() throws Exception {
    when(this.settingsService.get("auth.users")).thenReturn("bar@foo.com");
    when(this.googleIdTokenVerifierBuilder.build()).thenReturn(this.googleIdTokenVerifier);
    when(this.googleIdTokenVerifierBuilder.setAudience(any())).thenReturn(this.googleIdTokenVerifierBuilder);
    when(this.googleIdToken.getPayload()).thenReturn(SecurityFixtures.getMockPayload());
    when(this.jwtCreator.issueToken(any(), any(), any())).thenReturn("mock-token-123");

    this.mockMvc.perform(post("/api/user/sign-in/mock-123"))
        .andDo(print())
        .andExpect(status().isUnauthorized())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
        .andExpect(jsonPath("$.message").value("Unable to verify user."));
  }
}
