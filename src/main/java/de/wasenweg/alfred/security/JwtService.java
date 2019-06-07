package de.wasenweg.alfred.security;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.Claim;
import com.auth0.jwt.interfaces.DecodedJWT;

import org.springframework.context.annotation.Profile;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Map;

@Service
@Profile({"prod"})
public class JwtService implements IJwtService {

    public Boolean verifyToken(final String token, final String secret) {
        Boolean verified = false;

        try {
            final Algorithm algorithm = Algorithm.HMAC256(secret);
            final JWTVerifier verifier = JWT.require(algorithm)
                    .withIssuer("alfred.cx")
                    .build();
            final DecodedJWT jwt = verifier.verify(token);
            verified = jwt.getClaim("API_ALLOWED").asBoolean();
            final Map<String, Claim> roles = jwt.getClaims();

            final String subject = jwt.getSubject();

            final ArrayList<SimpleGrantedAuthority> authorities = new ArrayList<SimpleGrantedAuthority>();
            for (final String role: roles.keySet()) {
                authorities.add(new SimpleGrantedAuthority(role));
            }

            final UsernamePasswordAuthenticationToken newAuth =
                    new UsernamePasswordAuthenticationToken(subject, "", authorities);

            SecurityContextHolder.getContext().setAuthentication(newAuth);
        } catch (final Exception e) {
        }

        return verified;
    }
}
