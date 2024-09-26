package com.pubfinder.pubfinder.util;

import com.pubfinder.pubfinder.dto.ReviewDto;
import com.pubfinder.pubfinder.models.Pub;
import com.pubfinder.pubfinder.models.Review;
import com.pubfinder.pubfinder.models.User;
import com.pubfinder.pubfinder.models.Visited;
import com.pubfinder.pubfinder.models.enums.Volume;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;

public class TestUtil {

  public static Pub generateMockPub() {
    return Pub.builder()
        .id(UUID.randomUUID())
        .build();
  }

  public static User generateMockUser() {
    return User.builder()
        .id(UUID.randomUUID())
        .username("username")
        .build();
  }

  public static Visited generateUserVisitedPub() {
    return Visited.builder()
        .visitor(generateMockUser())
        .pub(generateMockPub())
        .visitedDate(LocalDateTime.now())
        .build();
  }

  public static Review generateMockReview(User user, Pub pub) {
    return Review.builder()
        .pub(pub)
        .reviewer(user)
        .reviewDate(LocalDateTime.now())
        .rating(5)
        .toilets(5)
        .volume(Volume.AVERAGE)
        .service(5)
        .build();
  }

  public static ReviewDto generateMockReviewDTO() {
    return ReviewDto.builder()
        .id(UUID.randomUUID())
        .pubId(UUID.randomUUID())
        .username("username")
        .reviewDate(LocalDateTime.now())
        .rating(5)
        .build();
  }

  public static Visited generateMockVisited() {
    return Visited.builder()
        .visitedDate(LocalDateTime.now())
        .pub(TestUtil.generateMockPub())
        .visitor(TestUtil.generateMockUser())
        .build();
  }

  public static List<Review> generateListOfMockReviews() {
    Random rand = new Random();
    Pub pub = generateMockPub();
    List<Review> reviews = new ArrayList<>();
    for (int i = 0; i < 20; i++) {
      Review review = generateMockReview(generateMockUser(), pub);
      review.setRating(rand.nextInt(5));
      review.setService(rand.nextInt(5));
      review.setToilets(rand.nextInt(5));
      if (rand.nextBoolean()) {
        review.setVolume(Volume.values()[rand.nextInt(5)]);
      }
      reviews.add(review);
    }
    return reviews;
  }

}
