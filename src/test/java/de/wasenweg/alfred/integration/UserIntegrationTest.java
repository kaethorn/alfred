package de.wasenweg.alfred.integration;

import de.wasenweg.alfred.EnableEmbeddedMongo;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@EnableEmbeddedMongo
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@ActiveProfiles("test")
public class UserIntegrationTest {

  private final MockMvc mockMvc;

  @Test
  public void verify() throws Exception {
    this.mockMvc.perform(MockMvcRequestBuilders.get("/api/user/verify/mock-123"))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
        .andExpect(jsonPath("$.token").value("mock-123"));
  }
}
