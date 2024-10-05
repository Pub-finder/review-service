package com.pubfinder.pubfinder.service;

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
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.function.ToIntFunction;

import com.pubfinder.pubfinder.models.enums.Volume;
import org.apache.coyote.BadRequestException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

/**
 * The type Review service.
 */
@Service
public class ReviewService {

  @Autowired
  private ReviewRepository reviewRepository;

  @Autowired
  private UserRepository userRepository;

  @Autowired
  private PubRepository pubRepository;

  /**
   * Saves user review
   *
   * @param review   the review
   * @param pubId    the pub id
   * @param userId the user id
   * @return the review dto
   * @throws ResourceNotFoundException    the user or pub not found exception
   * @throws ReviewAlreadyExistsException the review already exists exception
   */
  public ReviewDto save(Review review, UUID pubId, UUID userId)
      throws ResourceNotFoundException, ReviewAlreadyExistsException, BadRequestException {

    Optional<User> user = userRepository.findById(userId);
    if (user.isEmpty()) {
      // TODO: Make call to check if the user exist in the user db
      // User u = User.builder().id(userId).username(fetch.username).build();
      // User user = userRepository.save(u)
      throw new ResourceNotFoundException("User: " + userId + " does not exist");
    }

    Optional<Pub> pub = pubRepository.findById(pubId);
    if (pub.isEmpty()) {
      // TODO: Make call to check if the pub exist in the pub db
      // Pub u = User.builder().id(pubId).build();
      // Pub pub = pubRepository.save(u)
      throw new ResourceNotFoundException("Pub: " + pubId + " does not exist");
    }

    if (reviewRepository.findByPubAndReviewer(pub.get(), user.get()).isPresent()) {
      throw new ReviewAlreadyExistsException(
          "User: " + userId + " has already made a review on Pub: " + pubId);
    }

    review.setReviewer(user.get());
    review.setPub(pub.get());
    review.setReviewDate(LocalDateTime.now());

    return Mapper.INSTANCE.entityToDto(reviewRepository.save(review));
  }

  /**
   * Delete review.
   *
   * @param id the id
   * @throws ResourceNotFoundException the review not found exception
   */
  public void delete(UUID id) throws ResourceNotFoundException, BadRequestException {
    Review review = reviewRepository.findById(id)
        .orElseThrow(() -> new ResourceNotFoundException("Review: " + id + " not found."));
    reviewRepository.delete(review);
  }

  /**
   * Update review review dto.
   *
   * @param review the review
   * @return the review dto
   * @throws ResourceNotFoundException the review not found exception
   */
  public ReviewDto edit(Review review) throws ResourceNotFoundException {
    Review foundReview = reviewRepository.findById(review.getId()).orElseThrow(
        () -> new ResourceNotFoundException("Review: " + review.getId() + " not found."));
    review.setReviewDate(LocalDateTime.now());
    review.setPub(foundReview.getPub());
    review.setReviewer(foundReview.getReviewer());
    Review updatedReview = reviewRepository.save(review);
    return Mapper.INSTANCE.entityToDto(updatedReview);
  }

  @Cacheable(value = "getPubRating")
  public RatingDto getPubRating(UUID id)
          throws BadRequestException, ResourceNotFoundException {

    List<Review> reviews = reviewRepository.findAllByPub(getPub(id));

      return RatingDto.builder()
        .pubId(id)
        .rating(calculateAverageRating(reviews, Review::getRating))
        .toiletRating(calculateAverageRating(reviews, Review::getToilets))
        .serviceRating(calculateAverageRating(reviews, Review::getService))
        .volume(calculateAverageVolume(reviews))
      .build();
  }

  @Cacheable(value = "getPubReviews")
  public List<ReviewDto> getPubReviews(UUID id)
          throws ResourceNotFoundException {
    return reviewRepository.findAllByPub(getPub(id))
            .stream()
            .map(Mapper.INSTANCE::entityToDto)
            .toList();
  }

  @Cacheable(value = "getUserReviews")
  public List<ReviewDto> getUserReviews(UUID id)
          throws ResourceNotFoundException {
    User user = userRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("User with id " + id + " was not found"));

    return reviewRepository.findAllByReviewer(user)
            .stream()
            .map(Mapper.INSTANCE::entityToDto)
            .toList();
  }

  ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

  private Pub getPub(UUID id) throws ResourceNotFoundException {
    return pubRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Pub with id " + id + " was not found"));
  }


  private int calculateAverageRating(List<Review> reviews, ToIntFunction<Review> extractor) {
    int[] ratings = reviews.stream()
            .mapToInt(extractor)
            .filter(rating -> rating != 0)
            .toArray();

    return calculateAverage(Arrays.stream(ratings).sum(), ratings.length);
  }

  private Volume calculateAverageVolume(List<Review> reviews) {
    int[] loudness = reviews.stream().filter(r -> r.getVolume() != null)
            .mapToInt(r -> r.getVolume().getOrdinal()).toArray();
    if (loudness.length == 0) return null;

    int value = calculateAverage(Arrays.stream(loudness).sum(), loudness.length);
    return Volume.values()[value];
  }

  private int calculateAverage(int sum, int length) {
    return (int) Math.round((double) sum / length);
  }
}
