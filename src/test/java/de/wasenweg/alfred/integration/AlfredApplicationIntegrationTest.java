package de.wasenweg.alfred.integration;

import com.mongodb.MongoException;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoDatabase;
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
import org.bson.BsonDocument;
import org.bson.BsonInt64;
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
    if (!isMongoDbRunning()) {
      final Logger logger = LoggerFactory.getLogger(AlfredApplicationIntegrationTest.class.getName());
      final RuntimeConfig runtimeConfig = Defaults.runtimeConfigFor(Command.MongoD, logger).build();
      final MongodStarter mongodStarter = MongodStarter.getInstance(runtimeConfig);
      final int mongodPort = Network.freeServerPort(InetAddress.getLocalHost());
      mongodExecutable = mongodStarter.prepare(MongodConfig.builder()
          .version(Version.Main.V4_2)
          .net(new Net(mongodPort, Network.localhostIsIPv6()))
          .build());
      mongodProcess = mongodExecutable.start();
    }
  }

  @AfterAll
  public static void tearDown() {
    if (null != mongodProcess) {
      mongodProcess.stop();
    }
    if (null != mongodExecutable) {
      mongodExecutable.stop();
    }
  }

  @Test
  void startsAlfred() throws Exception {
    AlfredApplication.main(new String[] {"--spring.profiles.active=test"});
    final URL url = new URL("http://localhost:8080/api/stats");
    final HttpURLConnection httpUrlConnection = (HttpURLConnection) url.openConnection();
    httpUrlConnection.connect();

    assertThat(httpUrlConnection.getResponseCode()).isEqualTo(HttpURLConnection.HTTP_OK);
  }

  private static boolean isMongoDbRunning() {
    try (MongoClient mongoClient = MongoClients.create("mongodb://localhost:27017")) {
      final MongoDatabase database = mongoClient.getDatabase("admin");
      try {
        database.runCommand(new BsonDocument("ping", new BsonInt64(1)));
        return true;
      } catch (final MongoException me) {
        return false;
      }
    }
  }
}
