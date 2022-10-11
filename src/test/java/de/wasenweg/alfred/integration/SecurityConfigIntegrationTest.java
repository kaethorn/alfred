package de.wasenweg.alfred.integration;

import de.wasenweg.alfred.EnableEmbeddedMongo;
import de.wasenweg.alfred.unit.TestHelper;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(properties = { "auth.jwt.secret=secret" })
@AutoConfigureMockMvc
@EnableEmbeddedMongo
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@ActiveProfiles("prod")
class SecurityConfigIntegrationTest {

  private final MockMvc mockMvc;

  @Test
  void rejectsUnauthenticatedUsers() throws Exception {
    this.mockMvc.perform(get("/api/stats")
          .contentType(MediaType.APPLICATION_JSON))
          .andExpect(status().isUnauthorized());
  }

  @Test
  void rejectsUserWithInvalidClaims() throws Exception {
    this.mockMvc.perform(get("/api/stats")
          .header(HttpHeaders.AUTHORIZATION, "Bearer " + TestHelper.readString("jwt-invalid-claim.txt"))
          .contentType(MediaType.APPLICATION_JSON))
          .andExpect(status().isForbidden());
  }

  @Test
  void admitsValidUser() throws Exception {
    this.mockMvc.perform(get("/api/stats")
          .header(HttpHeaders.AUTHORIZATION, "Bearer " + TestHelper.readString("jwt-valid.txt"))
          .contentType(MediaType.APPLICATION_JSON))
          .andExpect(status().isOk());
  }
}
