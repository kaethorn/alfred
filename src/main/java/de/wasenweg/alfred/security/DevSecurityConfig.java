package de.wasenweg.alfred.security;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
@Profile({"dev", "test"})
public class DevSecurityConfig extends WebSecurityConfigurerAdapter {

  private final IJwtService jwtService;

  @Override
  protected void configure(final HttpSecurity http) throws Exception {
    http
      .csrf().disable()
      .addFilterAfter(new DevJwtFilter(this.jwtService), BasicAuthenticationFilter.class)
      .authorizeRequests()
      .antMatchers("*").permitAll();
  }
}
