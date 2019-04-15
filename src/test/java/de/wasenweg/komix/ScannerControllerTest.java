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
        // FIXME this doesn't work reliably because the controller
        // calls the service in a new thread.
        // verify(service).scanComics(Mockito.any());
    }
}
