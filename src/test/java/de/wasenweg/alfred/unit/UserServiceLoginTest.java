package de.wasenweg.alfred.unit;

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
public class UserServiceLoginTest {

  @Mock
  private transient SettingsService settingsService;

  @Mock
  private transient JwtCreator jwtCreator;

  @InjectMocks
  private transient UserService userService;

  @BeforeEach
  public void setUp() throws GeneralSecurityException, IOException {
    when(this.settingsService.get("auth.users")).thenReturn("foo@bar.com,bar@foo.com");
    when(this.settingsService.get("auth.passwords")).thenReturn("foo,bar");
  }

  @Test
  public void loginWithValidUser() throws Exception {
    when(this.jwtCreator.issueToken(any(), any(), any())).thenReturn("mock-api-token");

    assertThat(this.userService.login("foo@bar.com", "foo")).isPresent();
    final User user = this.userService.login("foo@bar.com", "foo").get();
    assertThat(user.getEmail()).isEqualTo("foo@bar.com");
    assertThat(user.getPicture()).isNull();
  }

  @Test
  public void loginWithoutPasswords() {
    when(this.settingsService.get("auth.passwords")).thenReturn("");

    assertThrows(GeneralSecurityException.class, () -> this.userService.login("foo@bar.com", "foo"));
  }

  @Test
  public void loginWithoutUsers() {
    when(this.settingsService.get("auth.users")).thenReturn("");

    assertThrows(GeneralSecurityException.class, () -> this.userService.login("foo@bar.com", "foo"));
  }

  @Test
  public void loginWithUnknownUser() throws Exception {
    assertThat(this.userService.login("none@bar.com", "foo")).isNotPresent();
  }

  @Test
  public void loginWithoutMatchingPassword() {
    assertThrows(GeneralSecurityException.class, () -> this.userService.login("foo@bar.com", "bar"));
  }
}