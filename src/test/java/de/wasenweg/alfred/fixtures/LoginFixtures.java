package de.wasenweg.alfred.fixtures;

import de.wasenweg.alfred.user.Login;

public final class LoginFixtures {

  public static final Login LOGIN_1 = Login.builder()
      .username("foo@bar.com")
      .password("foo")
      .build();

  public static final Login LOGIN_MISMATCH = Login.builder()
      .username("foo@bar.com")
      .password("bar")
      .build();

  public static final Login LOGIN_NONEXISTENT = Login.builder()
      .username("new@user.com")
      .password("oh")
      .build();

  private LoginFixtures() {
  }
}
