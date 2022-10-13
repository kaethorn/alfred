package de.wasenweg.alfred.security;

public interface IJwtService {

  Boolean verifyToken(String token, String secret);
}
