package de.wasenweg.alfred.security;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;

@EnableWebSecurity
@Configuration
@Profile({"prod"})
public class SecurityConfig {

    @Configuration
    @Order(1)
    public class BasicSecurityConfig extends WebSecurityConfigurerAdapter {

        @Override
        protected void configure(final HttpSecurity http) throws Exception {

            http.antMatcher("/api/verify/**").authorizeRequests().anyRequest().permitAll();
            http.csrf().csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse());
        }
    }


    @Configuration
    @Order(2)
    public class JwtSecurityConfig extends WebSecurityConfigurerAdapter {

        @Value("${jwtSecret:zY5MzUxODMyMTM0IiwiZW}")
        private String jwtSecret;

        @Override
        protected void configure(final HttpSecurity http) throws Exception {
            http.addFilterAfter(new JWTFilter(jwtSecret), BasicAuthenticationFilter.class);

            http.authorizeRequests()
                // TODO:
                .antMatchers("/actuator/*").hasAuthority("ACTUATOR")
                .antMatchers("/**").permitAll();

        }
    }
}
