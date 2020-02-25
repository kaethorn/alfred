package de.wasenweg.alfred.integration;

import de.wasenweg.alfred.AlfredApplication;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.client.ExpectedCount;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.context.WebApplicationContext;

import java.net.URI;

import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withStatus;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = { AlfredApplication.class })
@EnableAutoConfiguration
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@ActiveProfiles("test")
public class UserIntegrationTest {

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
  public void verify() throws Exception {
    this.mockMvc.perform(MockMvcRequestBuilders.get("/api/user/verify/mock-123"))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
        .andExpect(jsonPath("$.token").value("mock-123"));
  }

  // @Test
  // public void signInWithValidUser() throws Exception {
  //   final RestTemplate restTemplate = new RestTemplate();
  //   final MockRestServiceServer mockServer = MockRestServiceServer.createServer(restTemplate);
  //   mockServer.expect(ExpectedCount.once(), requestTo(new URI("https://google.com")))
  //         .andExpect(method(HttpMethod.POST))
  //         .andRespond(withStatus(HttpStatus.OK)
  //         .contentType(MediaType.APPLICATION_JSON)
  //         .body("{}"));

  //   // TODO Intercept request
  //   this.mockMvc.perform(MockMvcRequestBuilders.post("/api/user/sign-in/mock-123"))
  //       .andDo(print()) // FIXME debug
  //       .andExpect(status().isOk())
  //       .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
  //       .andExpect(jsonPath("$.token").value("mock-456"));

  //   mockServer.verify();
  // }

  // @Test
  // public void signInWithInvalidUser() throws Exception {
  //   this.mockMvc.perform(MockMvcRequestBuilders.get("/api/user/sign-in/mock-456"))
  //       .andDo(print()) // FIXME debug
  //       .andExpect(status().isOk())
  //       .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
  //       .andExpect(jsonPath("$.token").value("mock-456"));
  // }
}
