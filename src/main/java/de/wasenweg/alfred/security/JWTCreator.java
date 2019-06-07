package de.wasenweg.alfred.security;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTCreator.Builder;
import com.auth0.jwt.algorithms.Algorithm;
import org.springframework.stereotype.Service;

import java.util.Calendar;
import java.util.Date;

@Service
public class JWTCreator {

    private Algorithm algorithm = null;

    public JWTCreator() { }

    public String issueToken(final String[] claims, final String subject, Date expiryDate, final String jwtSecret) {

        this.algorithm = Algorithm.HMAC256(jwtSecret);

        if (expiryDate == null) {
            final Calendar calendar = Calendar.getInstance();
            calendar.setTime(new Date());
            calendar.add(Calendar.HOUR_OF_DAY, 24);
            expiryDate = calendar.getTime();
        }

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
