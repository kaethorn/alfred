package de.wasenweg.alfred.integration;

import de.wasenweg.alfred.AlfredApplication;
import de.wasenweg.alfred.EnableEmbeddedMongo;
import de.wasenweg.alfred.unit.TestHelper;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith({SpringExtension.class})
@SpringBootTest(
    classes = { AlfredApplication.class },
    properties = { "auth.jwt.secret=secret" }
)
@EnableEmbeddedMongo
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@ActiveProfiles({"prod"})
public class SecurityConfigIntegrationTest {

  private final WebApplicationContext context;

  private MockMvc mockMvc;

  @BeforeEach
  public void setUp() {
    this.mockMvc = MockMvcBuilders
        .webAppContextSetup(this.context)
        .apply(springSecurity())
        .build();
  }

  @Test
  public void rejectsUnauthenticatedUsers() throws Exception {
    this.mockMvc.perform(get("/api/stats")
          .contentType(MediaType.APPLICATION_JSON))
          .andExpect(status().isUnauthorized());
  }

  @Test
  public void rejectsUserWithInvalidClaims() throws Exception {
    this.mockMvc.perform(get("/api/stats")
          .header("Authorization", "Bearer " + TestHelper.readString("jwt-invalid-claim.txt"))
          .contentType(MediaType.APPLICATION_JSON))
          .andExpect(status().isForbidden());
  }

  @Test
  public void admitsValidUser() throws Exception {
    this.mockMvc.perform(get("/api/stats")
          .header("Authorization", "Bearer " + TestHelper.readString("jwt-valid.txt"))
          .contentType(MediaType.APPLICATION_JSON))
          .andExpect(status().isOk());
  }
}