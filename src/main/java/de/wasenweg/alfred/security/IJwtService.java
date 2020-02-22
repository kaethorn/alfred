package de.wasenweg.alfred.security;

public interface IJwtService {

  Boolean verifyToken(final String token, final String secret);
}
