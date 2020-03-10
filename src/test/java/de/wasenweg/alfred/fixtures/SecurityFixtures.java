package de.wasenweg.alfred.fixtures;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken.Payload;

public final class SecurityFixtures {

  private SecurityFixtures() {
  }

  public static Payload getMockPayload() {
    final Payload payload = new Payload();
    payload.set("name", "Foo Bar");
    payload.setEmail("foo@bar.com");
    return payload;
  }
}
