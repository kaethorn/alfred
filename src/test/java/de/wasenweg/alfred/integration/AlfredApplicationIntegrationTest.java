package de.wasenweg.alfred.integration;

import de.flapdoodle.embed.mongo.MongodExecutable;
import de.flapdoodle.embed.mongo.MongodProcess;
import de.flapdoodle.embed.mongo.MongodStarter;
import de.flapdoodle.embed.mongo.config.MongodConfig;
import de.flapdoodle.embed.mongo.config.Net;
import de.flapdoodle.embed.mongo.distribution.Version;
import de.flapdoodle.embed.process.runtime.Network;
import de.wasenweg.alfred.AlfredApplication;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.mongo.embedded.EmbeddedMongoAutoConfiguration;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;

import java.net.HttpURLConnection;
import java.net.InetAddress;
import java.net.URL;

import static org.assertj.core.api.Assertions.assertThat;

@DataMongoTest(excludeAutoConfiguration = EmbeddedMongoAutoConfiguration.class)
class AlfredApplicationIntegrationTest {

  private static final MongodStarter MONGOD_STARTER = MongodStarter.getDefaultInstance();
  private static MongodExecutable mongodExecutable;
  private static MongodProcess mongodProcess;

  @BeforeAll
  public static void setUp() throws Exception {
    final int mongodPort = Network.freeServerPort(InetAddress.getLocalHost());
    mongodExecutable = MONGOD_STARTER.prepare(MongodConfig.builder()
        .version(Version.Main.V4_2)
        .net(new Net(mongodPort, Network.localhostIsIPv6()))
        .build());
    mongodProcess = mongodExecutable.start();
  }

  @AfterAll
  public static void tearDown() {
    mongodProcess.stop();
    mongodExecutable.stop();
  }

  @Test
  void startsAlfred() throws Exception {
    AlfredApplication.main(new String[] {"--spring.profiles.active=test"});
    final URL url = new URL("http://localhost:8080/api/stats");
    final HttpURLConnection httpUrlConnection = (HttpURLConnection) url.openConnection();
    httpUrlConnection.connect();

    assertThat(httpUrlConnection.getResponseCode()).isEqualTo(HttpURLConnection.HTTP_OK);
  }
}
