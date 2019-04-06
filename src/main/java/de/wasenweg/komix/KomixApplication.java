package de.wasenweg.komix;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.oauth2.client.EnableOAuth2Sso;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;

@SpringBootApplication
@EnableOAuth2Sso
public class KomixApplication extends WebSecurityConfigurerAdapter {

    public static void main(final String[] args) {
        SpringApplication.run(KomixApplication.class, args);
    }

    @Override
    protected void configure(final HttpSecurity http) throws Exception {
      http
        .antMatcher("/**")
          .authorizeRequests()
            .antMatchers("/", "/login**", "/error**")
            .permitAll()
            .anyRequest().authenticated()
          .and()
        .logout().logoutUrl("/api/logout").logoutSuccessUrl("/").permitAll()
        .and().csrf().csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse());
    }
}
