package de.wasenweg.alfred.unit;

import de.wasenweg.alfred.security.JwtService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
public class JwtServiceTest {

  @InjectMocks
  private transient JwtService jwtService;

  @Test
  public void verifyValidToken() throws Exception {
    assertThat(this.jwtService.verifyToken(
      TestHelper.readString("jwt-valid.txt"),
      "secret")).isTrue();
  }

  @Test
  public void verifyInvalidToken() throws Exception {
    assertThat(this.jwtService.verifyToken("invalid", "")).isFalse();
  }

  @Test
  public void verifyInvalidClaim() throws Exception {
    assertThat(this.jwtService.verifyToken(
      TestHelper.readString("jwt-invalid-claim.txt"),
      "secret")).isFalse();
  }
}