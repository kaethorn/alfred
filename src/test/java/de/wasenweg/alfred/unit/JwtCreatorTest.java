package de.wasenweg.alfred.unit;

import com.auth0.jwt.JWT;
import com.auth0.jwt.interfaces.DecodedJWT;
import de.wasenweg.alfred.security.JwtCreator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Calendar;
import java.util.Date;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
public class JwtCreatorTest {

  @InjectMocks
  private transient JwtCreator jwtCreator;

  @Test
  public void issueToken() throws Exception {
    final String[] claims = { "API_ALLOWED" };
    final String token = this.jwtCreator.issueToken(claims, "foo@bar.com", "secret");
    final DecodedJWT jwt = JWT.decode(token);
    assertThat(jwt.getClaim("API_ALLOWED").asBoolean()).isTrue();
    assertThat(jwt.getIssuer()).isEqualTo("alfred.cx");
    assertThat(jwt.getSubject()).isEqualTo("foo@bar.com");
    final Date expiryDate = jwt.getExpiresAt();
    assertThat(expiryDate).isBetween(this.getOneMonthFromNowWithOffset(-1), this.getOneMonthFromNowWithOffset(1));
  }

  private Date getOneMonthFromNowWithOffset(final int hoursOffset) {
    final Calendar calendar = Calendar.getInstance();
    calendar.setTime(new Date());
    calendar.add(Calendar.MONTH, 1);
    calendar.add(Calendar.HOUR, hoursOffset);
    return calendar.getTime();
  }
}