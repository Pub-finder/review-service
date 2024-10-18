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
import com.pubfinder.pubfinder.dto.ReviewRequestDto;
import com.pubfinder.pubfinder.dto.ReviewResponseDto;
import com.pubfinder.pubfinder.exception.ResourceNotFoundException;
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
    when(reviewService.save(any())).thenReturn(reviewResponseDto);

    mockMvc.perform(post("/review/save")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(reviewRequestDto)))
        .andExpect(status().isCreated()).andDo(print());
  }

  @Test
  public void deleteTest() throws Exception {
    mockMvc.perform(delete("/review/delete/{id}", UUID.randomUUID())
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(reviewRequestDto)))
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
  public void getPubReviewsTest() throws Exception {
    when(reviewService.getPubReviews(any())).thenReturn(List.of(reviewResponseDto));

    mockMvc.perform(get("/review/reviews/pub/{id}", UUID.randomUUID())
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk());
  }

  @Test
  public void getUserReviewsTest() throws Exception {
    when(reviewService.getUserReviews(any())).thenReturn(List.of(reviewResponseDto));

    mockMvc.perform(get("/review/reviews/user/{id}", UUID.randomUUID())
                    .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk());
  }

  ReviewResponseDto reviewResponseDto = TestUtil.generateMockReviewResponseDTO();
  ReviewRequestDto reviewRequestDto = TestUtil.generateMockReviewRequestDTO();
}
