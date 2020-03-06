package de.wasenweg.alfred.user;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken.Payload;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import de.wasenweg.alfred.security.IJwtService;
import de.wasenweg.alfred.security.JwtCreator;
import de.wasenweg.alfred.settings.SettingsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static java.lang.String.format;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserService {

  @Value("${auth.jwt.secret:zY5MzUxODMyMTM0IiwiZW}")
  private String jwtSecret;

  private final JwtCreator tokenCreator;
  private final IJwtService jwtService;
  private final SettingsService settingsService;
  private final GoogleIdTokenVerifier.Builder verifierBuilder;

  public Optional<User> verify(final String token) {
    if (this.jwtService.verifyToken(token, this.jwtSecret)) {
      return Optional.of(User.builder()
          .token(token)
          .build());
    }
    return Optional.ofNullable(null);
  }

  public Optional<User> signIn(final String token) throws GeneralSecurityException, IOException {
    final String clientId = this.settingsService.get("auth.client.id");

    final GoogleIdTokenVerifier verifier = this.verifierBuilder
        .setAudience(Collections.singletonList(clientId))
        .build();

    final GoogleIdToken idToken = verifier.verify(token);
    if (idToken == null) {
      log.info(format("Invalid ID token: %s.", token));
    } else {
      final Payload payload = idToken.getPayload();
      final String email = payload.getEmail();
      final List<String> claims = new ArrayList<>();
      claims.add("ANONYMOUS");

      if (Arrays.asList(this.settingsService.get("auth.users").split(",")).contains(email)) {
        claims.add("API_ALLOWED");
      } else {
        log.debug(format("User %s is not present in the white list. Rejecting.", email));
        return Optional.ofNullable(null);
      }

      final String userId = payload.getSubject();
      final String apiToken = this.tokenCreator.issueToken(claims.stream().toArray(String[]::new), userId, this.jwtSecret);
      final String name = this.getKey(payload, "name");
      final String picture = this.getKey(payload, "picture");

      return Optional.of(User.builder()
          .id(userId)
          .email(email)
          .name(name)
          .picture(picture)
          .token(apiToken)
          .build());
    }

    throw new GeneralSecurityException();
  }

  private String getKey(final Payload payload, final String key) {
    if (payload.containsKey(key)) {
      return (String) payload.get(key);
    }
    return "";
  }
}