package de.wasenweg.alfred.security;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
public class JwtFilter implements Filter {

  private static final String HEADER_PREFIX = "Bearer ";

  private final String secret;
  private final IJwtService jwtService;

  @Override
  public void doFilter(
      final ServletRequest req,
      final ServletResponse res,
      final FilterChain chain) throws IOException, ServletException {

    final HttpServletRequest request = (HttpServletRequest) req;

    log.debug("Running filter on URL: {}", request.getRequestURL().toString());

    final Optional<String> token = Optional.ofNullable(request.getHeader(HttpHeaders.AUTHORIZATION));

    if (!token.isPresent() || !token.get().startsWith(HEADER_PREFIX)) {
      log.debug("No token found in header.");
      res.reset();
      final HttpServletResponse response = (HttpServletResponse) res;
      response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
      return;
    }

    if (this.jwtService.verifyToken(token.get().replace(HEADER_PREFIX, ""), this.secret)) {
      log.debug("Token is valid.");
      chain.doFilter(req, res);
    } else {
      log.debug("Token is invalid.");
      res.reset();
      final HttpServletResponse response = (HttpServletResponse) res;
      response.setStatus(HttpServletResponse.SC_FORBIDDEN);
    }
  }
}
