package de.wasenweg.alfred.security;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;
import java.util.Optional;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.Claim;
import com.auth0.jwt.interfaces.DecodedJWT;

public class JWTFilter implements Filter {

    private static String HEADER_PREFIX = "Bearer ";
    private String jwtSecret;

    public JWTFilter(String jwtSecret){
        this.jwtSecret = jwtSecret;
    }

    @Override
    public void destroy() {
    }

    @Override
    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain) throws IOException, ServletException {

        HttpServletRequest request = (HttpServletRequest) req;
        HttpServletResponse response =(HttpServletResponse) res;

        final Optional<String> token = Optional.ofNullable(request.getHeader("Authorization"));

        if (!token.isPresent() || !token.get().startsWith(HEADER_PREFIX)) {
            res.reset();
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }

        Boolean verified = false;

        try {
            Algorithm algorithm = Algorithm.HMAC256(this.jwtSecret);
            JWTVerifier verifier = JWT.require(algorithm)
                    .withIssuer("alfred.cx")
                    .build();
            DecodedJWT jwt = verifier.verify(token.get().replace(HEADER_PREFIX, ""));
            verified = jwt.getClaim("API_ALLOWED").asBoolean();
            Map<String, Claim> roles = jwt.getClaims();

            String subject = jwt.getSubject();

            ArrayList<SimpleGrantedAuthority> authorities = new ArrayList<SimpleGrantedAuthority>();
            for (String role: roles.keySet()){
                authorities.add(new SimpleGrantedAuthority(role));
            }

            final UsernamePasswordAuthenticationToken newAuth = 
                    new UsernamePasswordAuthenticationToken(subject, "", authorities);

            SecurityContextHolder.getContext().setAuthentication(newAuth);
        } catch (Exception e) {
        }

        if (verified) {
            chain.doFilter(req, res);
        } else {
            res.reset();
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
        }
    }

    @Override
    public void init(FilterConfig arg0) throws ServletException {
    }
}