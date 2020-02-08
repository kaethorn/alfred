package de.wasenweg.alfred.user;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;

@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class UserController {

  @Autowired
  private UserService userService;

  @GetMapping("/verify/{token}")
  public ResponseEntity<User> verify(@PathVariable("token") final String token) {
    return new ResponseEntity<User>(
      this.userService.verify(token).orElseThrow(() -> new HttpClientErrorException(HttpStatus.UNAUTHORIZED)),
      HttpStatus.OK);
  }

  @PostMapping("/sign-in/{token}")
  public ResponseEntity<User> signIn(@PathVariable("token") final String token) {

    try {
      return new ResponseEntity<User>(
        this.userService.signIn(token).orElseThrow(() -> {
          return new HttpClientErrorException(HttpStatus.FORBIDDEN);
        }),
        HttpStatus.OK);
    } catch (final Exception exception) {
      exception.printStackTrace();
    }

    throw new HttpServerErrorException(HttpStatus.UNAUTHORIZED);
  }
}
