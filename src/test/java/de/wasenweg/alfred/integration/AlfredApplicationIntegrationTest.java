package de.wasenweg.alfred.integration;

import de.flapdoodle.embed.mongo.MongodExecutable;
import de.flapdoodle.embed.mongo.MongodProcess;
import de.flapdoodle.embed.mongo.MongodStarter;
import de.flapdoodle.embed.mongo.config.MongodConfig;
import de.flapdoodle.embed.mongo.config.Net;
import de.flapdoodle.embed.mongo.distribution.Version;
import de.flapdoodle.embed.process.runtime.Network;
import de.wasenweg.alfred.AlfredApplication;
import de.wasenweg.alfred.EnableEmbeddedMongo;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.net.HttpURLConnection;
import java.net.URL;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(SpringExtension.class)
@EnableEmbeddedMongo
public class AlfredApplicationIntegrationTest {

  private static final int MONGODB_PORT = 27_017;
  private static final MongodStarter MONGOD_STARTER = MongodStarter.getDefaultInstance();
  private static MongodExecutable mongodExecutable;
  private static MongodProcess mongodProcess;

  @BeforeAll
  public static void setUp() throws Exception {
    mongodExecutable = MONGOD_STARTER.prepare(MongodConfig.builder()
        .version(Version.Main.V3_6)
        .net(new Net(MONGODB_PORT, Network.localhostIsIPv6()))
        .build());
    mongodProcess = mongodExecutable.start();
  }

  @AfterAll
  public static void tearDown() {
    mongodProcess.stop();
    mongodExecutable.stop();
  }

  @Test
  public void startsAlfred() throws Exception {
    AlfredApplication.main(new String[] {"--spring.profiles.active=test"});
    final URL url = new URL("http://localhost:8080/api/stats");
    final HttpURLConnection httpUrlConnection = (HttpURLConnection) url.openConnection();
    httpUrlConnection.connect();

    assertThat(httpUrlConnection.getResponseCode()).isEqualTo(HttpURLConnection.HTTP_OK);
  }
}
