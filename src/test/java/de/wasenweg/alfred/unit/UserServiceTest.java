package de.wasenweg.alfred.unit;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken.Payload;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import de.wasenweg.alfred.security.JwtCreator;
import de.wasenweg.alfred.settings.SettingsService;
import de.wasenweg.alfred.user.User;
import de.wasenweg.alfred.user.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;
import java.security.GeneralSecurityException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

  @Mock
  private transient SettingsService settingsService;

  @Mock
  private transient JwtCreator jwtCreator;

  @Mock
  private transient GoogleIdTokenVerifier.Builder mockVerifierBuilder;

  @Mock
  private transient GoogleIdTokenVerifier mockVerifier;

  @Mock
  private transient GoogleIdToken mockIdToken;

  @Mock
  private transient Payload mockPayload;

  @InjectMocks
  private transient UserService userService;

  @BeforeEach
  public void setUp() throws GeneralSecurityException, IOException {
    when(this.settingsService.get("auth.client.id")).thenReturn("fake-client-id-123");
    when(this.mockVerifier.verify("mock-123")).thenReturn(this.mockIdToken);
    when(this.mockVerifierBuilder.build()).thenReturn(this.mockVerifier);
    when(this.mockVerifierBuilder.setAudience(any())).thenReturn(this.mockVerifierBuilder);
  }

  @Test
  public void signInWithValidToken() throws Exception {
    when(this.settingsService.get("auth.users")).thenReturn("foo@bar.com,bar@foo.com");
    when(this.mockPayload.getEmail()).thenReturn("foo@bar.com");
    when(this.mockIdToken.getPayload()).thenReturn(this.mockPayload);
    when(this.jwtCreator.issueToken(any(), any(), any())).thenReturn("mock-api-token");

    final User user = this.userService.signIn("mock-123").get();
    assertThat(user.getEmail()).isEqualTo("foo@bar.com");
  }

  @Test
  public void signInWithoutValidToken() throws Exception {
    when(this.mockVerifier.verify("mock-123")).thenReturn(null);
    assertThrows(GeneralSecurityException.class, () -> this.userService.signIn("mock-123"));
  }

  @Test
  public void signInWithoutValidUser() throws Exception {
    when(this.settingsService.get("auth.users")).thenReturn("foo@bar.com,bar@foo.com");
    when(this.mockPayload.getEmail()).thenReturn("foo@bar.com");
    when(this.mockIdToken.getPayload()).thenReturn(this.mockPayload);

    when(this.mockPayload.getEmail()).thenReturn("invalid@user.com");
    assertThat(this.userService.signIn("mock-123").isPresent()).isFalse();
  }
}