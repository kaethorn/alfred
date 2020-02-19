package de.wasenweg.alfred.security;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
@Profile({"prod"})
public class SecurityConfig {

  private final IJwtService jwtService;

  @Value("${auth.jwt.secret:zY5MzUxODMyMTM0IiwiZW}")
  private String jwtSecret;

  @Configuration
  public class BasicSecurityConfig extends WebSecurityConfigurerAdapter {

    @Override
    public void configure(final WebSecurity web) {
      web.ignoring()
          .antMatchers("/api/user/**", "/api/scan-progress",
            "/", "/index.html", "/manifest.json", "/*.js", "/*.css", "/assets/**", "/svg/**");
    }

    @Override
    protected void configure(final HttpSecurity http) throws Exception {
      http
        .csrf().csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse())
        .and().antMatcher("/api/**")
        .addFilterAfter(new JwtFilter(SecurityConfig.this.jwtSecret, SecurityConfig.this.jwtService), BasicAuthenticationFilter.class);
    }
  }
}
