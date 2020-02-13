package de.wasenweg.alfred.user;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken.Payload;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.http.apache.v2.ApacheHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import de.wasenweg.alfred.security.IJwtService;
import de.wasenweg.alfred.security.JwtCreator;
import de.wasenweg.alfred.settings.SettingsService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
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
public class UserService {

  @Value("${auth.jwt.secret:zY5MzUxODMyMTM0IiwiZW}")
  private String jwtSecret;

  @Autowired
  private JwtCreator tokenCreator;

  @Autowired
  private IJwtService jwtService;

  @Autowired
  private SettingsService settingsService;


  public Optional<User> verify(final String token) {
    if (this.jwtService.verifyToken(token, this.jwtSecret)) {
      return Optional.of(User.builder()
          .token(token)
          .build());
    }
    return Optional.ofNullable(null);
  }

  public Optional<User> signIn(final String token) throws GeneralSecurityException, IOException {

    final ApacheHttpTransport transport = new ApacheHttpTransport();
    final JacksonFactory jsonFactory = new JacksonFactory();
    final String clientId = this.settingsService.get("auth.client.id");
    final List<String> users = Arrays.asList(this.settingsService.get("auth.users").split(","));

    final GoogleIdTokenVerifier verifier = new GoogleIdTokenVerifier.Builder(transport, jsonFactory)
        .setAudience(Collections.singletonList(clientId))
        .build();

    final GoogleIdToken idToken = verifier.verify(token);
    if (idToken != null) {
      final Payload payload = idToken.getPayload();
      final String userId = payload.getSubject();
      final String email = payload.getEmail();
      final String name = (String) payload.get("name");
      final String pictureUrl = (String) payload.get("picture");
      final List<String> claims = new ArrayList<String>();
      claims.add("ANONYMOUS");

      if (users.contains(email)) {
        claims.add("API_ALLOWED");
      } else {
        return Optional.ofNullable(null);
      }

      final String apiToken = this.tokenCreator.issueToken(claims.stream().toArray(String[]::new), userId, this.jwtSecret);

      return Optional.of(User.builder()
          .id(userId)
          .email(email)
          .name(name)
          .picture(pictureUrl)
          .token(apiToken)
          .build());
    } else {
      log.info(format("Invalid ID token: %s.", token));
    }

    throw new GeneralSecurityException();
  }
}