package de.wasenweg.alfred.security;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

import java.io.IOException;
import java.util.ArrayList;

@EnableWebSecurity
@Configuration
@Profile({"dev", "test"})
public class DevSecurityConfig extends WebSecurityConfigurerAdapter {

  private class DevJwtFilter implements Filter {

    @Override
    public void doFilter(
        final ServletRequest req,
        final ServletResponse res,
        final FilterChain chain) throws IOException, ServletException {

      final ArrayList<SimpleGrantedAuthority> authorities = new ArrayList<SimpleGrantedAuthority>();
      authorities.add(new SimpleGrantedAuthority("iss"));
      authorities.add(new SimpleGrantedAuthority("sub"));
      authorities.add(new SimpleGrantedAuthority("API_ALLOWED"));
      authorities.add(new SimpleGrantedAuthority("exp"));

      final UsernamePasswordAuthenticationToken mockAuth =
          new UsernamePasswordAuthenticationToken("mock-user-1", "", authorities);

      SecurityContextHolder.getContext().setAuthentication(mockAuth);

      chain.doFilter(req, res);
    }
  }

  @Override
  protected void configure(final HttpSecurity http) throws Exception {
    http.addFilterAfter(new DevJwtFilter(), BasicAuthenticationFilter.class);

    http.csrf().disable()
    .authorizeRequests()
    .antMatchers("*").permitAll();
  }
}
