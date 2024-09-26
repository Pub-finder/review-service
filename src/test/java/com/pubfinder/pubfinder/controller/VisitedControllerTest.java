package com.pubfinder.pubfinder.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.pubfinder.pubfinder.service.VisitedService;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(value = VisitedController.class)
@AutoConfigureMockMvc(addFilters = false)
public class VisitedControllerTest {

  @MockBean
  private VisitedService visitedService;

  @Autowired
  private MockMvc mockMvc;

  @Test
  public void saveTest() throws Exception {
    doNothing().when(visitedService).save(any(), any());

    mockMvc.perform(post("/visited/save/{pubId}/{username}", UUID.randomUUID(), "username")
            .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isCreated());
  }

  @Test
  public void deleteTest() throws Exception {
    doNothing().when(visitedService).delete(any());

    mockMvc.perform(delete("/visited/delete/{id}", UUID.randomUUID())
            .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isNoContent());
  }
}
