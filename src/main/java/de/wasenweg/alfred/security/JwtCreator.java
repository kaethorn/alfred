package de.wasenweg.alfred.security;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTCreator.Builder;
import com.auth0.jwt.algorithms.Algorithm;
import org.springframework.stereotype.Service;

import java.util.Calendar;
import java.util.Date;

@Service
public class JwtCreator {

  public String issueToken(final String[] claims, final String subject, final String jwtSecret) {

    final Algorithm algorithm = Algorithm.HMAC256(jwtSecret);

    final Calendar calendar = Calendar.getInstance();
    calendar.setTime(new Date());
    calendar.add(Calendar.MONTH, 1);
    final Date expiryDate = calendar.getTime();

    final Builder tokenBuilder = JWT.create()
        .withIssuer("alfred.cx")
        .withSubject(subject)
        .withExpiresAt(expiryDate);

    for (final String claim : claims) {
      tokenBuilder.withClaim(claim, true);
    }

    return tokenBuilder.sign(algorithm);
  }
}
