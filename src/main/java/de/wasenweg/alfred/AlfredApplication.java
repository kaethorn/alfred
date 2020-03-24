package de.wasenweg.alfred;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.mongo.embedded.EmbeddedMongoAutoConfiguration;
import org.springframework.cache.annotation.EnableCaching;

@EnableCaching
@SpringBootApplication(exclude = EmbeddedMongoAutoConfiguration.class)
public class AlfredApplication {  // NOPMD

  public static void main(final String[] args) {
    SpringApplication.run(AlfredApplication.class, args);
  }
}
