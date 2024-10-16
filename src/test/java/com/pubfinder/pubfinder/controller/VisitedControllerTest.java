package com.pubfinder.pubfinder.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pubfinder.pubfinder.dto.VisitDto;
import com.pubfinder.pubfinder.dto.VisitedDto;
import com.pubfinder.pubfinder.exception.ResourceNotFoundException;
import com.pubfinder.pubfinder.service.VisitedService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import com.pubfinder.pubfinder.util.TestUtil;
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

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    public void saveTest() throws Exception {
        VisitDto visitDto = TestUtil.generateVisitDto();
        doNothing().when(visitedService).save(visitDto);

        mockMvc.perform(post("/visited/save")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(visitDto)))
                .andExpect(status().isCreated());
    }

    @Test
    public void deleteTest() throws Exception {
        doNothing().when(visitedService).delete(any(), any());

        mockMvc.perform(delete("/visited/delete/{userId}/{pubId}", UUID.randomUUID(), UUID.randomUUID())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());
    }

    @Test
    public void getVisitsTest() throws Exception {
        List<VisitedDto> visitedDtos = TestUtil.generateListOfMockVisitedDtos(TestUtil.generateMockUser());
        when(visitedService.getVisitedPubs(any())).thenReturn(visitedDtos);

        mockMvc.perform(get("/visited/visits/{id}", UUID.randomUUID())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    public void getVisitsTest_NotFound() throws Exception {
        when(visitedService.getVisitedPubs(any())).thenThrow(ResourceNotFoundException.class);

        mockMvc.perform(get("/visited/visits/{id}", UUID.randomUUID())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }
}
