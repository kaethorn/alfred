package de.wasenweg.komix;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.oauth2.client.EnableOAuth2Sso;

@SpringBootApplication
@EnableOAuth2Sso
public class KomixApplication {

    public static void main(final String[] args) {
        SpringApplication.run(KomixApplication.class, args);
    }
}
