package de.wasenweg.comix;

import de.flapdoodle.embed.mongo.config.IMongodConfig;
import de.flapdoodle.embed.mongo.config.MongodConfigBuilder;
import de.flapdoodle.embed.mongo.distribution.Version;

import org.springframework.context.annotation.Bean;

import java.io.IOException;

public class EmbeddedMongoConfig {

    @Bean
    public IMongodConfig embeddedMongoConfiguration() throws IOException {
         return new MongodConfigBuilder().version(Version.Main.V3_6).build();
    }
}
