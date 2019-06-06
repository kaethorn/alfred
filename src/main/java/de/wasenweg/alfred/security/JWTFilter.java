package de.wasenweg.alfred.security;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.Claim;
import com.auth0.jwt.interfaces.DecodedJWT;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;
import java.util.Optional;

public class JWTFilter implements Filter {

    private static final String HEADER_PREFIX = "Bearer ";
    private String jwtSecret;

    public JWTFilter(final String jwtSecret) {
        this.jwtSecret = jwtSecret;
    }

    @Override
    public void destroy() {
    }

    @Override
    public void doFilter(
            final ServletRequest req,
            final ServletResponse res,
            final FilterChain chain) throws IOException, ServletException {

        final HttpServletRequest request = (HttpServletRequest) req;
        final HttpServletResponse response = (HttpServletResponse) res;

        final Optional<String> token = Optional.ofNullable(request.getHeader("Authorization"));

        if (!token.isPresent() || !token.get().startsWith(HEADER_PREFIX)) {
            res.reset();
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }

        Boolean verified = false;

        try {
            final Algorithm algorithm = Algorithm.HMAC256(this.jwtSecret);
            final JWTVerifier verifier = JWT.require(algorithm)
                    .withIssuer("alfred.cx")
                    .build();
            final DecodedJWT jwt = verifier.verify(token.get().replace(HEADER_PREFIX, ""));
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

        if (verified) {
            chain.doFilter(req, res);
        } else {
            res.reset();
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
        }
    }

    @Override
    public void init(final FilterConfig arg0) throws ServletException {
    }
}
