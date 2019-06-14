package de.wasenweg.alfred.user;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken.Payload;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.http.apache.ApacheHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;

import de.wasenweg.alfred.security.IJwtService;
import de.wasenweg.alfred.security.JwtCreator;
import de.wasenweg.alfred.settings.SettingsService;
import lombok.RequiredArgsConstructor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class UserController {

    @Autowired
    private JwtCreator tokenCreator;

    @Autowired
    private IJwtService jwtService;

    @Autowired
    private SettingsService settingsService;

    @Value("${jwtSecret:zY5MzUxODMyMTM0IiwiZW}")
    private String jwtSecret;

    @GetMapping("/verify/{token}")
    public ResponseEntity<?> verify(@PathVariable("token") final String token) {
        if (this.jwtService.verifyToken(token, this.jwtSecret)) {
            final User user = User.builder()
                    .token(token)
                    .build();
            return new ResponseEntity<User>(user, HttpStatus.OK);
        } else {
           return new ResponseEntity<Error>(HttpStatus.UNAUTHORIZED);
        }
    }

    @PostMapping("/sign-in/{token}")
    public ResponseEntity<?> signIn(@PathVariable("token") final String token) {

        final ApacheHttpTransport transport = new ApacheHttpTransport();
        final JacksonFactory jsonFactory = new JacksonFactory();
        final String clientId = this.settingsService.get("auth.client.id");
        final List<String> users = Arrays.asList(this.settingsService.get("auth.users").split(","));

        final GoogleIdTokenVerifier verifier = new GoogleIdTokenVerifier.Builder(transport, jsonFactory)
                .setAudience(Collections.singletonList(clientId))
                .build();

        try {
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
                    return new ResponseEntity<Error>(HttpStatus.FORBIDDEN);
                }

                final String apiToken = tokenCreator.issueToken(claims.stream().toArray(String[]::new), userId, this.jwtSecret);

                final User user = User.builder()
                        .id(userId)
                        .email(email)
                        .name(name)
                        .picture(pictureUrl)
                        .token(apiToken)
                        .build();

                return new ResponseEntity<User>(user, HttpStatus.OK);
            } else {
                System.out.println("Invalid ID token: " + token);
            }
        } catch (final GeneralSecurityException e) {
            e.printStackTrace();
        } catch (final IOException e) {
            e.printStackTrace();
        }

        return new ResponseEntity<Error>(HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
