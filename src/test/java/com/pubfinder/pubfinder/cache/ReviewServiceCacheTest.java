package com.pubfinder.pubfinder.cache;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.times;

import com.pubfinder.pubfinder.db.PubRepository;
import com.pubfinder.pubfinder.db.ReviewRepository;
import com.pubfinder.pubfinder.dto.RatingDto;
import com.pubfinder.pubfinder.dto.ReviewRequestDto;
import com.pubfinder.pubfinder.dto.ReviewResponseDto;
import com.pubfinder.pubfinder.exception.ResourceNotFoundException;
import com.pubfinder.pubfinder.models.Pub;
import com.pubfinder.pubfinder.service.ReviewService;
import com.pubfinder.pubfinder.util.TestUtil;
import org.apache.coyote.BadRequestException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.cache.CacheManager;

import java.util.List;
import java.util.Optional;

@SpringBootTest(properties = {
    "spring.datasource.url=",
    "spring.jpa.database-platform=",
    "spring.jpa.hibernate.ddl-auto=none",
})
public class ReviewServiceCacheTest {
  @Autowired
  private ReviewService reviewService;
  @MockBean
  private ReviewRepository reviewRepository;
  @MockBean
  private PubRepository pubRepository;
  @Autowired
  private CacheManager cacheManager;

  @Test
  public void getPubRatingTest_CacheHit()
          throws BadRequestException, ResourceNotFoundException {
    Pub pub = TestUtil.generateMockPub();

    when(pubRepository.findById(pub.getId())).thenReturn(Optional.of(pub));
    when(reviewRepository.findAllByPub(pub)).thenReturn(TestUtil.generateListOfMockReviews(pub));

    RatingDto response1 = reviewService.getPubRating(pub.getId());
    RatingDto response2 = reviewService.getPubRating(pub.getId());

    assertEquals(response1, response2);
    verify(reviewRepository, times(1)).findAllByPub(pub);
  }

  @Test
  public void getPubReviews_CacheHit()
          throws BadRequestException, ResourceNotFoundException {
    Pub pub = TestUtil.generateMockPub();

    when(pubRepository.findById(pub.getId())).thenReturn(Optional.of(pub));
    when(reviewRepository.findAllByPub(pub)).thenReturn(TestUtil.generateListOfMockReviews(pub));

    List<ReviewResponseDto> response1 = reviewService.getPubReviews(pub.getId());
    List<ReviewResponseDto> response2 = reviewService.getPubReviews(pub.getId());

    assertEquals(response1, response2);
    verify(reviewRepository, times(1)).findAllByPub(pub);
  }
}
