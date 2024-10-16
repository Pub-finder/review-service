package com.pubfinder.pubfinder.util;

import com.pubfinder.pubfinder.dto.*;
import com.pubfinder.pubfinder.mapper.Mapper;
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

  public static ReviewResponseDto generateMockReviewResponseDTO() {
    return ReviewResponseDto.builder()
        .id(UUID.randomUUID())
        .pubId(UUID.randomUUID())
        .username("username")
        .reviewDate(LocalDateTime.now())
        .rating(5)
        .build();
  }

  public static ReviewRequestDto generateMockReviewRequestDTO() {
    return ReviewRequestDto.builder()
            .id(UUID.randomUUID())
            .pubId(UUID.randomUUID())
            .username("username")
            .reviewDate(LocalDateTime.now())
            .rating(5)
            .build();
  }

  public static Visited generateMockVisited(User visitor) {
    return Visited.builder()
        .visitedDate(LocalDateTime.now())
        .pub(TestUtil.generateMockPub())
        .visitor(visitor)
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

  public static RatingDto generateMockRatingDto() {
    return  RatingDto.builder()
            .pubId(UUID.randomUUID())
            .rating(5)
            .toiletRating(5)
            .serviceRating(5)
            .volume(Volume.AVERAGE)
            .build();
  }

  public static List<Review> generateListOfMockReviews(Pub pub) {
    List<Review> reviews = new ArrayList<>();

    for (int i = 0;i<10;i++) {
      Review review = TestUtil.generateMockReview(TestUtil.generateMockUser(), pub);
      reviews.add(review);
    }

    return reviews;
  }

  public static List<Visited> generateListOfMockVisits(User user) {
    List<Visited> visits = new ArrayList<>();

    for (int i = 0;i<10;i++) {
      Visited visit = TestUtil.generateMockVisited(TestUtil.generateMockUser());
      visits.add(visit);
    }

    return visits;
  }

  public static List<VisitedDto> generateListOfMockVisitedDtos(User user) {
    List<VisitedDto> visits = new ArrayList<>();

    for (int i = 0;i<10;i++) {
      Visited visit = TestUtil.generateMockVisited(TestUtil.generateMockUser());
      visits.add(Mapper.INSTANCE.entityToDto(visit));
    }

    return visits;
  }

  public static VisitDto generateVisitDto() {
    return VisitDto.builder()
            .username("username")
            .userId(UUID.randomUUID())
            .pubId(UUID.randomUUID())
            .build();
  }
}
