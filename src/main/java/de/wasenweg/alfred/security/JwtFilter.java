package de.wasenweg.alfred.security;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.Optional;

public class JwtFilter implements Filter {

  private static final String HEADER_PREFIX = "Bearer ";

  private String secret;

  private IJwtService jwtService;

  private Logger logger = LoggerFactory.getLogger(JwtFilter.class);

  public JwtFilter(final String secret, final IJwtService jwtService) {
    this.secret = secret;
    this.jwtService = jwtService;
  }

  @Override
  public void doFilter(
      final ServletRequest req,
      final ServletResponse res,
      final FilterChain chain) throws IOException, ServletException {

    this.logger.info("Running filter on URL: {}", ((HttpServletRequest)req).getRequestURL().toString());

    final HttpServletRequest request = (HttpServletRequest) req;
    final HttpServletResponse response = (HttpServletResponse) res;

    final Optional<String> token = Optional.ofNullable(request.getHeader("Authorization"));

    if (!token.isPresent() || !token.get().startsWith(HEADER_PREFIX)) {
      this.logger.info("No token found in header.");
      res.reset();
      response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
      return;
    }

    if (this.jwtService.verifyToken(token.get().replace(HEADER_PREFIX, ""), this.secret)) {
      this.logger.info("Token is valid.");
      chain.doFilter(req, res);
    } else {
      this.logger.info("Token is invalid.");
      res.reset();
      response.setStatus(HttpServletResponse.SC_FORBIDDEN);
    }
  }
}
