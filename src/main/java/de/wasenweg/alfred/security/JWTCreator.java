package de.wasenweg.alfred.security;

import java.util.Calendar;
import java.util.Date;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTCreator.Builder;
import com.auth0.jwt.algorithms.Algorithm;

@Service
public class JWTCreator {

    @Value("${jwtSecret:zY5MzUxODMyMTM0IiwiZW}")
    private String jwtSecret;

    private Algorithm algorithm = null;

    public JWTCreator() { }

    public String issueToken(String[] claims, String subject, Date expiryDate){

        try {
            this.algorithm = Algorithm.HMAC256(jwtSecret);
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        }

        /*
         * If no expiry date given, default to a 24hour expiry time
         */
        if (expiryDate == null){
            Calendar c = Calendar.getInstance();
            c.setTime(new Date()); // Use today's date.
            c.add(Calendar.HOUR_OF_DAY, 24); // Adds 24 hours 
            expiryDate = c.getTime();
        }

        Builder tokenBuilder = JWT.create()
                .withIssuer("alfred.cx")
                .withSubject(subject)
                .withExpiresAt(expiryDate);

        /*
         * Cycle through the claims and add them to our JWT
         */
        for (String claim : claims){
            tokenBuilder.withClaim(claim, true);
        }

        /*
         * Sign and issue the JWT
         */
        String issuedToken = tokenBuilder.sign(algorithm);

        return issuedToken;
    }
}
