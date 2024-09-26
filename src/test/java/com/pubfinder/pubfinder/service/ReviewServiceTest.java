package com.pubfinder.pubfinder.service;

import static com.pubfinder.pubfinder.util.TestUtil.generateListOfMockReviews;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.pubfinder.pubfinder.db.PubRepository;
import com.pubfinder.pubfinder.db.ReviewRepository;
import com.pubfinder.pubfinder.db.UserRepository;
import com.pubfinder.pubfinder.dto.RatingDto;
import com.pubfinder.pubfinder.dto.ReviewDto;
import com.pubfinder.pubfinder.exception.ResourceNotFoundException;
import com.pubfinder.pubfinder.exception.ReviewAlreadyExistsException;
import com.pubfinder.pubfinder.mapper.Mapper;
import com.pubfinder.pubfinder.models.Pub;
import com.pubfinder.pubfinder.models.Review;
import com.pubfinder.pubfinder.models.User;
import com.pubfinder.pubfinder.models.enums.Volume;
import com.pubfinder.pubfinder.util.TestUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.apache.coyote.BadRequestException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

@SpringBootTest(properties = {
    "spring.cache.type=none",
    "bucket4j.enabled=false",
    "spring.datasource.url=",
    "spring.jpa.database-platform=",
    "spring.jpa.hibernate.ddl-auto=none"
})
public class ReviewServiceTest {

  @Autowired
  public ReviewService reviewService;
  @MockBean
  public ReviewRepository reviewRepository;
  @MockBean
  public UserRepository userRepository;
  @MockBean
  public PubRepository pubRepository;

  @Test
  public void saveTest()
      throws ResourceNotFoundException, ReviewAlreadyExistsException, BadRequestException {
    User user = TestUtil.generateMockUser();
    Pub pub = TestUtil.generateMockPub();

    when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
    when(pubRepository.findById(pub.getId())).thenReturn(Optional.of(pub));

    when(reviewRepository.findByPubAndReviewer(pub, user)).thenReturn(Optional.empty());
    when(reviewRepository.findByPubAndReviewer(pub, user)).thenReturn(Optional.empty());
    Review review = TestUtil.generateMockReview(user, pub);
    when(reviewRepository.save(any())).thenReturn(review);
    ReviewDto result = reviewService.save(review, pub.getId(), user.getId());

    assertEquals(result, Mapper.INSTANCE.entityToDto(review));
    verify(reviewRepository, times(1)).save(review);
  }

  @Test
  public void saveTest_UserNotFound() throws ResourceNotFoundException {
    User user = TestUtil.generateMockUser();
    Pub pub = TestUtil.generateMockPub();
    Review review = TestUtil.generateMockReview(user, pub);
    assertThrows(ResourceNotFoundException.class,
        () -> reviewService.save(review, pub.getId(), user.getId()));
  }

  @Test
  public void saveTest_PubNotFound() throws ResourceNotFoundException {
    User user = TestUtil.generateMockUser();
    Pub pub = TestUtil.generateMockPub();
    Review review = TestUtil.generateMockReview(user, pub);
    assertThrows(ResourceNotFoundException.class,
        () -> reviewService.save(review, pub.getId(), user.getId()));
  }

  @Test
  public void saveReviewTest_AlreadyExists() throws ResourceNotFoundException {
    User user = TestUtil.generateMockUser();
    Pub pub = TestUtil.generateMockPub();

    Review review = TestUtil.generateMockReview(user, pub);
    when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
    when(pubRepository.findById(pub.getId())).thenReturn(Optional.of(pub));

    when(reviewRepository.findByPubAndReviewer(pub, user)).thenReturn(Optional.of(review));
    assertThrows(ReviewAlreadyExistsException.class,
        () -> reviewService.save(review, pub.getId(), user.getId()));
  }

  @Test
  public void deleteTest() throws ResourceNotFoundException, BadRequestException {
    User user = TestUtil.generateMockUser();
    Pub pub = TestUtil.generateMockPub();
    Review review = TestUtil.generateMockReview(user, pub);
    when(reviewRepository.findById(any())).thenReturn(Optional.of(review));
    doNothing().when(reviewRepository).delete(review);

    reviewService.delete(review.getId());
    verify(reviewRepository, times(1)).delete(review);
  }

  @Test
  public void deleteReviewTest_NotFound() {
    User user = TestUtil.generateMockUser();
    Pub pub = TestUtil.generateMockPub();
    Review review = TestUtil.generateMockReview(user, pub);
    when(reviewRepository.findById(any())).thenReturn(Optional.empty());
    assertThrows(ResourceNotFoundException.class, () -> reviewService.delete(review.getId()));
  }

  @Test
  public void editTest() throws ResourceNotFoundException {
    User user = TestUtil.generateMockUser();
    Pub pub = TestUtil.generateMockPub();
    Review review = TestUtil.generateMockReview(user, pub);
    when(reviewRepository.findById(any())).thenReturn(Optional.of(review));
    review.setVolume(Volume.LOUD);
    when(reviewRepository.save(any())).thenReturn(review);

    ReviewDto result = reviewService.edit(review);
    assertEquals(Mapper.INSTANCE.entityToDto(review), result);
    verify(reviewRepository, times(1)).save(review);
  }

  @Test
  public void editReviewTest_NotFound() {
    User user = TestUtil.generateMockUser();
    Pub pub = TestUtil.generateMockPub();
    Review review = TestUtil.generateMockReview(user, pub);
    when(reviewRepository.findById(any())).thenReturn(Optional.empty());
    assertThrows(ResourceNotFoundException.class, () -> reviewService.edit(review));
  }

  @Test
  public void getPubRatingTest() throws BadRequestException, ResourceNotFoundException {
    Pub pub = TestUtil.generateMockPub();

    List<Review> reviews = new ArrayList<>();

    for (int i = 0;i<10;i++) {
      Review review = TestUtil.generateMockReview(TestUtil.generateMockUser(), pub);
      reviews.add(review);
    }

    when(pubRepository.findById(pub.getId())).thenReturn(Optional.of(pub));
    when(reviewRepository.findAllByPub(pub)).thenReturn(reviews);

    RatingDto ratingDto = reviewService.getPubRating(pub.getId());

    assertEquals(ratingDto.getRating(), 5);
    assertEquals(ratingDto.getToiletRating(), 5);
    assertEquals(ratingDto.getServiceRating(), 5);
    assertEquals(ratingDto.getVolume(), Volume.AVERAGE);
  }

  @Test
  public void getPubRatingTest_NoReviews() throws BadRequestException, ResourceNotFoundException {
    Pub pub = TestUtil.generateMockPub();

    when(pubRepository.findById(pub.getId())).thenReturn(Optional.of(pub));
    when(reviewRepository.findAllByPub(pub)).thenReturn(List.of());

    RatingDto ratingDto = reviewService.getPubRating(pub.getId());

    assertEquals(ratingDto.getRating(), 0);
    assertEquals(ratingDto.getToiletRating(), 0);
    assertEquals(ratingDto.getServiceRating(), 0);
    assertEquals(ratingDto.getVolume(), null);
  }

  @Test
  public void getPubRatingTest_NotFound() throws BadRequestException, ResourceNotFoundException {
    Pub pub = TestUtil.generateMockPub();
    when(pubRepository.findById(pub.getId())).thenReturn(Optional.empty());
    assertThrows(ResourceNotFoundException.class, () -> reviewService.getPubRating(pub.getId()));
  }

  @Test
  public void getPubReviewsTest() throws BadRequestException, ResourceNotFoundException {
    Pub pub = TestUtil.generateMockPub();

    List<Review> reviews = generateListOfMockReviews();

    when(pubRepository.findById(pub.getId())).thenReturn(Optional.of(pub));
    when(reviewRepository.findAllByPub(pub)).thenReturn(reviews);

    List<ReviewDto> reviewDtos = reviewService.getPubReviews(pub.getId());

    assertEquals(reviewDtos.size(), reviews.size());
  }

  @Test
  public void getPubReviewsTest_NotFound() throws BadRequestException, ResourceNotFoundException {
    Pub pub = TestUtil.generateMockPub();
    when(pubRepository.findById(pub.getId())).thenReturn(Optional.empty());
    assertThrows(ResourceNotFoundException.class, () -> reviewService.getPubReviews(pub.getId()));
  }
}
