package de.wasenweg.alfred.security;

import lombok.RequiredArgsConstructor;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

import java.io.IOException;

@RequiredArgsConstructor
public class DevJwtFilter implements Filter {

  private final IJwtService jwtService;

  @Override
  public void doFilter(
      final ServletRequest req,
      final ServletResponse res,
      final FilterChain chain) throws IOException, ServletException {

    this.jwtService.verifyToken("", "");
    chain.doFilter(req, res);
  }
}
