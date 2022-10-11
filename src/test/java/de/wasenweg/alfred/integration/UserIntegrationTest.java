package de.wasenweg.alfred.integration;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import de.wasenweg.alfred.EnableEmbeddedMongo;
import de.wasenweg.alfred.TestUtil;
import de.wasenweg.alfred.fixtures.LoginFixtures;
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
import org.springframework.hateoas.MediaTypes;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(properties = { "auth.jwt.secret=secret" })
@AutoConfigureMockMvc
@EnableEmbeddedMongo
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@ActiveProfiles("prod")
class UserIntegrationTest {

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
  void verify() throws Exception {
    final String token = TestHelper.readString("jwt-valid.txt");
    this.mockMvc.perform(get("/api/user/verify/" + token))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
        .andExpect(jsonPath("$.token").value(token));
  }

  @Test
  void verifyUnauthenticated() throws Exception {
    this.mockMvc.perform(get("/api/user/verify/invalid-token-123"))
        .andExpect(status().isUnauthorized());
  }

  @Test
  void signIn() throws Exception {
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
  void signInWithoutValidUser() throws Exception {
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
  void signInWithoutValidToken() throws Exception {
    when(this.settingsService.get("auth.users")).thenReturn("bar@foo.com");
    when(this.googleIdTokenVerifierBuilder.build()).thenReturn(this.googleIdTokenVerifier);
    when(this.googleIdTokenVerifierBuilder.setAudience(any())).thenReturn(this.googleIdTokenVerifierBuilder);
    when(this.googleIdToken.getPayload()).thenReturn(SecurityFixtures.getMockPayload());
    when(this.jwtCreator.issueToken(any(), any(), any())).thenReturn("mock-token-123");

    this.mockMvc.perform(post("/api/user/sign-in/mock-123"))
        .andExpect(status().isUnauthorized())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
        .andExpect(jsonPath("$.message").value("Unable to verify user."));
  }

  @Test
  void clientId() throws Exception {
    when(this.settingsService.get("auth.client.id")).thenReturn("mock-client-id-123");

    this.mockMvc.perform(get("/api/user/client-id"))
        .andExpect(status().isOk())
        .andExpect(content().contentTypeCompatibleWith(MediaType.TEXT_PLAIN))
        .andExpect(content().string("mock-client-id-123"));
  }

  @Test
  void login() throws Exception {
    when(this.settingsService.get("auth.users")).thenReturn("foo@bar.com,bar@foo.com");
    when(this.settingsService.get("auth.passwords")).thenReturn("foo,bar");
    when(this.jwtCreator.issueToken(any(), any(), any())).thenReturn("mock-token-123");

    this.mockMvc.perform(post("/api/user/login")
        .contentType(MediaType.APPLICATION_JSON_VALUE)
        .accept(MediaTypes.HAL_JSON_VALUE)
        .content(TestUtil.toJson(LoginFixtures.LOGIN_1)))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaTypes.HAL_JSON_VALUE))
        .andExpect(jsonPath("$.token").value("mock-token-123"))
        .andExpect(jsonPath("$.email").value("foo@bar.com"))
        .andExpect(jsonPath("$.id").value("foo@bar.com"))
        .andExpect(jsonPath("$.picture").doesNotExist());
  }

  @Test
  void loginWithInvalidUser() throws Exception {
    when(this.settingsService.get("auth.users")).thenReturn("foo@bar.com,bar@foo.com");
    when(this.settingsService.get("auth.passwords")).thenReturn("foo,bar");
    when(this.jwtCreator.issueToken(any(), any(), any())).thenReturn("mock-token-123");

    this.mockMvc.perform(post("/api/user/login")
        .contentType(MediaType.APPLICATION_JSON_VALUE)
        .accept(MediaTypes.HAL_JSON_VALUE)
        .content(TestUtil.toJson(LoginFixtures.LOGIN_NONEXISTENT)))
        .andExpect(status().isForbidden())
        .andExpect(content().contentType(MediaTypes.HAL_JSON))
        .andExpect(jsonPath("$.message").value("User not allowed."));
  }

  @Test
  void loginWithoutMatch() throws Exception {
    when(this.settingsService.get("auth.users")).thenReturn("foo@bar.com,bar@foo.com");
    when(this.settingsService.get("auth.passwords")).thenReturn("foo,bar");
    when(this.jwtCreator.issueToken(any(), any(), any())).thenReturn("mock-token-123");

    this.mockMvc.perform(post("/api/user/login")
        .contentType(MediaType.APPLICATION_JSON_VALUE)
        .accept(MediaTypes.HAL_JSON_VALUE)
        .content(TestUtil.toJson(LoginFixtures.LOGIN_MISMATCH)))
        .andExpect(status().isUnauthorized())
        .andExpect(content().contentType(MediaTypes.HAL_JSON))
        .andExpect(jsonPath("$.message").value("Unable to login user."));
  }
}
