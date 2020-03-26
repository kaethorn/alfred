package de.wasenweg.alfred.security;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.http.apache.v2.ApacheHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class GoogleIdTokenVerifierBuilder {

  @Bean
  public GoogleIdTokenVerifier.Builder getBuilder() {
    final ApacheHttpTransport transport = new ApacheHttpTransport();
    final JacksonFactory jsonFactory = new JacksonFactory();
    return new GoogleIdTokenVerifier.Builder(transport, jsonFactory);
  }
}
