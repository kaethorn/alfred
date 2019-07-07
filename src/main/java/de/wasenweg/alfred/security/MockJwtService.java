package de.wasenweg.alfred.security;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

@Service
@Profile({"dev", "test"})
public class MockJwtService implements IJwtService {

  public Boolean verifyToken(final String token, final String secret) {
    return true;
  }
}
