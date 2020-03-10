package de.wasenweg.alfred.user;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Optional;

@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class UserController {

  private final UserService userService;

  @GetMapping("/verify/{token}")
  public ResponseEntity<?> verify(@PathVariable("token") final String token) {
    final Optional<User> maybeUser = this.userService.verify(token);
    if (maybeUser.isPresent()) {
      return new ResponseEntity<>(maybeUser.get(), HttpStatus.OK);
    }
    return new ResponseEntity<Error>(HttpStatus.UNAUTHORIZED);
  }

  @PostMapping("/sign-in/{token}")
  public ResponseEntity<?> signIn(@PathVariable("token") final String token) {
    try {
      final Optional<User> maybeUser = this.userService.signIn(token);
      if (maybeUser.isPresent()) {
        return new ResponseEntity<>(maybeUser.get(), HttpStatus.OK);
      }
      return new ResponseEntity<>(new Error("User not allowed."), HttpStatus.FORBIDDEN);
    } catch (final GeneralSecurityException | IOException exception) {
      return new ResponseEntity<>(new Error(exception.getLocalizedMessage()), HttpStatus.UNAUTHORIZED);
    }
  }
}
