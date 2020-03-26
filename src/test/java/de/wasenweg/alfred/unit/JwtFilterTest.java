package de.wasenweg.alfred.unit;

import de.wasenweg.alfred.security.JwtFilter;
import de.wasenweg.alfred.security.JwtService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpHeaders;

import javax.servlet.FilterChain;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class JwtFilterTest {

  @Mock
  private transient JwtService jwtService;

  @Mock
  private transient HttpServletRequest servletRequest;

  @Spy
  private transient HttpServletResponse servletResponse;

  @Spy
  private transient FilterChain filterChain;

  private transient JwtFilter jwtFilter;

  @BeforeEach
  public void setUp() {
    this.jwtFilter = new JwtFilter("secret", this.jwtService);
    when(this.servletRequest.getRequestURL()).thenReturn(new StringBuffer(""));
  }

  @Test
  public void withValidToken() throws Exception {
    when(this.servletRequest.getHeader(HttpHeaders.AUTHORIZATION)).thenReturn("Bearer valid-token");
    when(this.jwtService.verifyToken("valid-token", "secret")).thenReturn(true);
    this.jwtFilter.doFilter(this.servletRequest, this.servletResponse, this.filterChain);
    verify(this.servletResponse, times(0)).reset();
    verify(this.filterChain).doFilter(this.servletRequest, this.servletResponse);
  }

  @Test
  public void withInvalidToken() throws Exception {
    when(this.servletRequest.getHeader(HttpHeaders.AUTHORIZATION)).thenReturn("Bearer invalid-token");
    when(this.jwtService.verifyToken("invalid-token", "secret")).thenReturn(false);
    this.jwtFilter.doFilter(this.servletRequest, this.servletResponse, this.filterChain);
    verify(this.servletResponse).reset();
    verify(this.servletResponse).setStatus(HttpServletResponse.SC_FORBIDDEN);
  }

  @Test
  public void withoutAuthorizatioHeader() throws Exception {
    this.jwtFilter.doFilter(this.servletRequest, this.servletResponse, this.filterChain);
    verify(this.servletResponse).reset();
    verify(this.servletResponse).setStatus(HttpServletResponse.SC_UNAUTHORIZED);
  }

  @Test
  public void withInvalidAuthorizationHeader() throws Exception {
    when(this.servletRequest.getHeader(HttpHeaders.AUTHORIZATION)).thenReturn("valid-token-without-bearer");
    this.jwtFilter.doFilter(this.servletRequest, this.servletResponse, this.filterChain);
    verify(this.servletResponse).reset();
    verify(this.servletResponse).setStatus(HttpServletResponse.SC_UNAUTHORIZED);
  }
}