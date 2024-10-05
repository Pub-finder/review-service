package com.pubfinder.pubfinder.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.pubfinder.pubfinder.db.PubRepository;
import com.pubfinder.pubfinder.db.ReviewRepository;
import com.pubfinder.pubfinder.db.UserRepository;
import com.pubfinder.pubfinder.models.Pub;
import com.pubfinder.pubfinder.models.Review;
import com.pubfinder.pubfinder.models.User;
import com.pubfinder.pubfinder.util.TestUtil;

import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.TestPropertySource;

@DataJpaTest
@TestPropertySource(properties = {
    "spring.test.database.replace=none",
    "spring.datasource.url=jdbc:tc:postgresql:16-alpine:///db"
})
public class ReviewRepositoryTest {

  @Autowired
  private PubRepository pubRepository;

  @Autowired
  private UserRepository userRepository;

  @Autowired
  private ReviewRepository reviewRepository;

  @Test
  public void saveAndGetReviewTest() {
    Review review = saveReview();

    Optional<Review> result = reviewRepository.findById(review.getId());
    assertTrue(result.isPresent());
    assertEquals(result.get(), review);
  }

  @Test
  public void deleteReviewTest() {
    Review review = saveReview();

    Optional<Review> result1 = reviewRepository.findById(review.getId());
    assertTrue(result1.isPresent());
    assertEquals(result1.get(), review);

    reviewRepository.delete(review);

    Optional<Review> result2 = reviewRepository.findById(review.getId());
    assertTrue(result2.isEmpty());
  }

  @Test
  public void editReviewTest() {
    Review review = saveReview();

    Optional<Review> result1 = reviewRepository.findById(review.getId());
    assertTrue(result1.isPresent());
    assertEquals(result1.get(), review);

    review = result1.get();
    review.setReview("This place was shit");
    reviewRepository.save(review);

    Optional<Review> result2 = reviewRepository.findById(review.getId());
    assertTrue(result2.isPresent());
    assertEquals(result2.get(), review);
  }

  @Test
  public void findAllReviewsForPubTest() {
    User user = userRepository.save(TestUtil.generateMockUser());
    Pub pub = pubRepository.save(TestUtil.generateMockPub());
    Review review1 = TestUtil.generateMockReview(user, pub);
    Review review2 = TestUtil.generateMockReview(user, pub);
    Review review3 = TestUtil.generateMockReview(user, pub);

    reviewRepository.save(review1);
    reviewRepository.save(review2);
    reviewRepository.save(review3);

    List<Review> result = reviewRepository.findAllByPub(pub);
    assertEquals(result.size(), 3);
  }

  @Test
  public void findAllReviewsForUserTest() {
    User user = userRepository.save(TestUtil.generateMockUser());
    Pub pub = pubRepository.save(TestUtil.generateMockPub());
    Review review1 = TestUtil.generateMockReview(user, pub);
    Review review2 = TestUtil.generateMockReview(user, pub);
    Review review3 = TestUtil.generateMockReview(user, pub);

    reviewRepository.save(review1);
    reviewRepository.save(review2);
    reviewRepository.save(review3);

    List<Review> result = reviewRepository.findAllByReviewer(user);
    assertEquals(result.size(), 3);
  }

  private Review saveReview() {
    User user = userRepository.save(TestUtil.generateMockUser());
    Pub pub = pubRepository.save(TestUtil.generateMockPub());
    Review review = TestUtil.generateMockReview(user, pub);
    return reviewRepository.save(review);
  }
}
