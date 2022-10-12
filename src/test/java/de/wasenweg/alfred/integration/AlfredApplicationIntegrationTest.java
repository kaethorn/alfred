package de.wasenweg.alfred.integration;

import de.flapdoodle.embed.mongo.MongodExecutable;
import de.flapdoodle.embed.mongo.MongodProcess;
import de.flapdoodle.embed.mongo.MongodStarter;
import de.flapdoodle.embed.mongo.config.Defaults;
import de.flapdoodle.embed.mongo.config.MongodConfig;
import de.flapdoodle.embed.mongo.config.Net;
import de.flapdoodle.embed.mongo.distribution.Version;
import de.flapdoodle.embed.mongo.packageresolver.Command;
import de.flapdoodle.embed.process.config.RuntimeConfig;
import de.flapdoodle.embed.process.runtime.Network;
import de.wasenweg.alfred.AlfredApplication;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.HttpURLConnection;
import java.net.InetAddress;
import java.net.URL;

import static org.assertj.core.api.Assertions.assertThat;

class AlfredApplicationIntegrationTest {

  private static MongodExecutable mongodExecutable;
  private static MongodProcess mongodProcess;

  @BeforeAll
  public static void setUp() throws Exception {
    final Logger logger = LoggerFactory.getLogger(AlfredApplicationIntegrationTest.class.getName());
    final RuntimeConfig runtimeConfig = Defaults.runtimeConfigFor(Command.MongoD, logger)
        .build();
    final MongodStarter mongodStarter = MongodStarter.getInstance(runtimeConfig);

    final int mongodPort = Network.freeServerPort(InetAddress.getLocalHost());
    mongodExecutable = mongodStarter.prepare(MongodConfig.builder()
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
