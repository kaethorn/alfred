package de.wasenweg.alfred.unit;

import de.wasenweg.alfred.scanner.ScannerController;
import de.wasenweg.alfred.scanner.ScannerService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
public class ScannerControllerTest {

  private transient MockMvc mockMvc;

  @Mock
  private transient ScannerService service;

  @InjectMocks
  private transient ScannerController controller;

  @BeforeEach
  public void setUp() {
    this.mockMvc = MockMvcBuilders.standaloneSetup(this.controller)
        .build();
  }

  @Test
  public void startsScanProgressEvents() throws Exception {
    // when
    this.mockMvc.perform(get("/api/scan/start"))
        .andExpect(status().isOk())
        .andReturn();

    // then
    // Wait for scan to start
    Thread.sleep(500);
    verify(this.service).scan();
  }
}
