package de.wasenweg.alfred.unit;

import de.wasenweg.alfred.IndexController;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.forwardedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
public class IndexControllerTest {

  private transient MockMvc mockMvc;

  @InjectMocks
  private transient IndexController controller;

  @BeforeEach
  public void setUp() {
    this.mockMvc = MockMvcBuilders.standaloneSetup(this.controller)
        .build();
  }

  @Test
  public void redirectToIndex() throws Exception {
    this.mockMvc.perform(get("/error"))
        .andExpect(forwardedUrl("/index.html"))
        .andExpect(status().isOk());
  }

  @Test
  public void returnsErrorPath() throws Exception {
    assertThat(this.controller.getErrorPath()).isEqualTo("/error");
  }
}
