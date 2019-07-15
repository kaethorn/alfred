package de.wasenweg.alfred.security;

public interface IJwtService {

  public Boolean verifyToken(final String token, final String secret);
}
