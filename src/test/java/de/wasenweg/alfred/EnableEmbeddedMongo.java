package de.wasenweg.alfred;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import org.springframework.boot.autoconfigure.mongo.embedded.EmbeddedMongoAutoConfiguration;
import org.springframework.context.annotation.Import;

@Retention(RetentionPolicy.RUNTIME)
@Import(EmbeddedMongoAutoConfiguration.class)
public @interface EnableEmbeddedMongo {

}