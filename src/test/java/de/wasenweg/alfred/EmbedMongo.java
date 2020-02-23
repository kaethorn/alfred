package de.wasenweg.alfred;

import org.springframework.boot.autoconfigure.mongo.embedded.EmbeddedMongoAutoConfiguration;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Profile;

@Configuration
@Import(EmbeddedMongoAutoConfiguration.class)
@Profile("test")
public class EmbedMongo {

}