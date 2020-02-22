package de.wasenweg.alfred.security;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.Claim;
import com.auth0.jwt.interfaces.DecodedJWT;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Map;

@Service
@Slf4j
@Profile({"prod"})
public class JwtService implements IJwtService {

  @Override
  public Boolean verifyToken(final String token, final String secret) {
    log.debug("Verifying token: {}", token);

    try {
      final Algorithm algorithm = Algorithm.HMAC256(secret);
      final JWTVerifier verifier = JWT.require(algorithm)
          .withIssuer("alfred.cx")
          .build();
      final DecodedJWT jwt = verifier.verify(token);

      final Claim claim = jwt.getClaim("API_ALLOWED");
      if (claim.isNull()) {
        log.debug("Token claim does not contain API_ALLOWED");
        return false;
      }
      final Boolean verified = claim.asBoolean();

      final Map<String, Claim> roles = jwt.getClaims();

      final String subject = jwt.getSubject();

      final ArrayList<SimpleGrantedAuthority> authorities = new ArrayList<>();
      roles.keySet().forEach(role -> {
        authorities.add(new SimpleGrantedAuthority(role));
      });

      final UsernamePasswordAuthenticationToken newAuth =
          new UsernamePasswordAuthenticationToken(subject, "", authorities);

      SecurityContextHolder.getContext().setAuthentication(newAuth);
      return verified;
    } catch (final IllegalArgumentException | JWTVerificationException exception) {
      log.error("Exception while verifying token.", exception);
      return false;
    }
  }
}
