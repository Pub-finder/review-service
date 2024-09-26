package com.pubfinder.pubfinder.controller;

import static org.mockito.Mockito.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.pubfinder.pubfinder.dto.ReviewDto;
import com.pubfinder.pubfinder.exception.ResourceNotFoundException;
import com.pubfinder.pubfinder.exception.ReviewAlreadyExistsException;
import com.pubfinder.pubfinder.service.ReviewService;
import com.pubfinder.pubfinder.util.TestUtil;

import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;


@WebMvcTest(value = ReviewController.class)
@AutoConfigureMockMvc(addFilters = false)
public class ReviewControllerTest {

  @MockBean
  private ReviewService reviewService;

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private ObjectMapper objectMapper;

  @BeforeEach
  public void setup() {
    objectMapper.registerModule(new JavaTimeModule());
  }

  @Test
  public void saveTest() throws Exception {
    when(reviewService.save(any(), any(), any())).thenReturn(reviewDTO);

    mockMvc.perform(post("/review/save/{pubId}/{userId}", UUID.randomUUID(), UUID.randomUUID())
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(reviewDTO)))
        .andExpect(status().isCreated()).andDo(print());
  }

  @Test
  public void saveTest_NotFound() throws Exception {
    when(reviewService.save(any(), any(), any())).thenThrow(ResourceNotFoundException.class);

    mockMvc.perform(post("/review/save/{pubId}/{userId}", UUID.randomUUID(), UUID.randomUUID())
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(reviewDTO)))
        .andExpect(status().isNotFound()).andDo(print());
  }

  @Test
  public void saveTest_ReviewAlreadyExists() throws Exception {
    when(reviewService.save(any(), any(), any())).thenThrow(
        ReviewAlreadyExistsException.class);

    mockMvc.perform(post("/review/save/{pubId}/{userId}", UUID.randomUUID(), UUID.randomUUID())
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(reviewDTO)))
        .andExpect(status().isConflict());
  }

  @Test
  public void deleteTest() throws Exception {
    mockMvc.perform(delete("/review/delete/{id}", UUID.randomUUID())
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(reviewDTO)))
        .andExpect(status().isNoContent());
  }

  @Test
  public void deletePubTest_NotFound() throws Exception {
    doThrow(ResourceNotFoundException.class).when(reviewService).delete(any());
    mockMvc.perform(delete("/review/delete")
            .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isNotFound());
  }

  @Test
  public void editTest() throws Exception {
    when(reviewService.edit(any())).thenReturn(reviewDTO);

    mockMvc.perform(put("/review/edit")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(reviewDTO)))
        .andExpect(status().isOk());
  }

  @Test
  public void editPubTest_NotFound() throws Exception {
    when(reviewService.edit(any())).thenThrow(ResourceNotFoundException.class);

    mockMvc.perform(put("/pub/editPub")
            .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isNotFound());
  }

  @Test
  public void getPubRatingTest() throws Exception {
    when(reviewService.getPubRating(any())).thenReturn(TestUtil.generateMockRatingDto());

    mockMvc.perform(get("/review/rating/{id}", UUID.randomUUID())
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk());
  }

  @Test
  public void getPubRatingTest_NotFound() throws Exception {
    when(reviewService.getPubRating(any())).thenThrow(ResourceNotFoundException.class);

    mockMvc.perform(get("/review/rating/{id}", UUID.randomUUID())
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isNotFound());
  }

  @Test
  public void getReviewsTest() throws Exception {
    when(reviewService.getPubReviews(any())).thenReturn(List.of(TestUtil.generateMockReviewDTO()));

    mockMvc.perform(get("/review/reviews/{id}", UUID.randomUUID())
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk());
  }

  @Test
  public void getReviewsTest_NotFound() throws Exception {
    when(reviewService.getPubReviews(any())).thenThrow(ResourceNotFoundException.class);

    mockMvc.perform(get("/review/reviews/{id}", UUID.randomUUID())
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isNotFound());
  }

  ReviewDto reviewDTO = TestUtil.generateMockReviewDTO();
}
