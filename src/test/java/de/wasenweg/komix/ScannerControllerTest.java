package de.wasenweg.komix;

import de.wasenweg.komix.scanner.ScannerController;
import de.wasenweg.komix.scanner.ScannerService;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(MockitoJUnitRunner.class)
public class ScannerControllerTest {

    private MockMvc mockMvc;

    @Mock
    private ScannerService service;

    @InjectMocks
    private ScannerController controller;

    @Before
    public void setup() {
        mockMvc = MockMvcBuilders.standaloneSetup(controller)
                .build();
    }

    @Test
    public void startsScanProgressEvents() throws Exception {
        // when
        mockMvc.perform(get("/api/scan-progress"))
                .andExpect(status().isOk())
                .andReturn();

        // then
        // Wait for scan to start
        Thread.sleep(500);
        verify(service).scanComics();
    }
}
