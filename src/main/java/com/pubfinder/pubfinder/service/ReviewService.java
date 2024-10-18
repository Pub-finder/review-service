package com.pubfinder.pubfinder.service;

import com.pubfinder.pubfinder.db.PubRepository;
import com.pubfinder.pubfinder.db.ReviewRepository;
import com.pubfinder.pubfinder.db.UserRepository;
import com.pubfinder.pubfinder.dto.RatingDto;
import com.pubfinder.pubfinder.dto.ReviewRequestDto;
import com.pubfinder.pubfinder.dto.ReviewResponseDto;
import com.pubfinder.pubfinder.exception.ResourceNotFoundException;
import com.pubfinder.pubfinder.mapper.Mapper;
import com.pubfinder.pubfinder.models.Pub;
import com.pubfinder.pubfinder.models.Review;
import com.pubfinder.pubfinder.models.User;
import com.pubfinder.pubfinder.models.enums.Volume;
import org.apache.coyote.BadRequestException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.function.ToIntFunction;

/**
 * The type Review service.
 */
@Service
public class ReviewService {
    @Autowired
    private ApplicationContext applicationContext;
    @Autowired
    private ReviewRepository reviewRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PubRepository pubRepository;

    /**
     * Saves user review
     *
     * @param review the review
     * @return the review dto
     */
    @Caching(evict = {
            @CacheEvict(value = "getPubRating", key = "#review.pubId"),
            @CacheEvict(value = "getPubReviews", key = "#review.pubId"),
            @CacheEvict(value = "getUserReviews", key = "#review.userId")
    })
    public ReviewResponseDto save(ReviewRequestDto review) {
        User user = userRepository.findById(review.getUserId()).orElseGet(() -> userRepository.save(User.builder().id(review.getUserId()).username(review.getUsername()).build()));
        Pub pub = pubRepository.findById(review.getPubId()).orElseGet(() -> pubRepository.save(Pub.builder().id(review.getPubId()).build()));

        Review savedReview = reviewRepository.findByPubAndReviewer(pub, user).map(existingReview -> updateOldReview(existingReview, review)).orElseGet(() -> createNewReview(user, pub, review));
        return Mapper.INSTANCE.entityToDto(savedReview);
    }

    /**
     * Delete review.
     *
     * @param id the id
     * @throws ResourceNotFoundException the review not found exception
     */
    public void delete(UUID id) throws ResourceNotFoundException, BadRequestException {
        Review review = reviewRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Review: " + id + " not found."));
        // this is split up into a separate function so the caching will work
        applicationContext.getBean(ReviewService.class).deleteWithCacheEviction(review);
    }

    @Caching(evict = {@CacheEvict(value = "getPubRating", key = "#review.pub.id"), @CacheEvict(value = "getPubReviews", key = "#review.pub.id"), @CacheEvict(value = "getUserReviews", key = "#review.reviewer.id")})
    public void deleteWithCacheEviction(Review review) {
        reviewRepository.delete(review);
    }

    @Cacheable(value = "getPubRating")
    public RatingDto getPubRating(UUID id) throws BadRequestException, ResourceNotFoundException {
        if (id == null) throw new BadRequestException();

        Pub pub = pubRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Pub with id: " + id + " was not found"));
        List<Review> reviews = reviewRepository.findAllByPub(pub);

        return RatingDto.builder()
                .pubId(id)
                .rating(calculateAverageRating(reviews, Review::getRating))
                .toiletRating(calculateAverageRating(reviews, Review::getToilets))
                .serviceRating(calculateAverageRating(reviews, Review::getService))
                .volume(calculateAverageVolume(reviews))
                .build();
    }

    @Cacheable(value = "getPubReviews")
    public List<ReviewResponseDto> getPubReviews(UUID id) throws BadRequestException {
        if (id == null) throw new BadRequestException();

        Optional<Pub> pub = pubRepository.findById(id);
        return pub.map(value -> reviewRepository.findAllByPub(value)
                .stream()
                .map(Mapper.INSTANCE::entityToDto).toList())
                .orElseGet(List::of);

    }

    @Cacheable(value = "getUserReviews")
    public List<ReviewResponseDto> getUserReviews(UUID id) throws BadRequestException {
        if (id == null) throw new BadRequestException();

        Optional<User> user = userRepository.findById(id);
        return user.map(value -> reviewRepository.findAllByReviewer(value)
                .stream()
                .map(Mapper.INSTANCE::entityToDto).toList())
                .orElseGet(List::of);

    }

    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    private Review updateOldReview(Review existingReview, ReviewRequestDto reviewRequestDto) {
        existingReview.setRating(reviewRequestDto.getRating());
        existingReview.setService(reviewRequestDto.getService());
        existingReview.setToilets(reviewRequestDto.getToilets());
        existingReview.setReview(reviewRequestDto.getReview());
        existingReview.setVolume(Volume.fromValue(reviewRequestDto.getVolume()));
        existingReview.setReviewDate(LocalDateTime.now());
        return reviewRepository.save(existingReview);
    }

    private Review createNewReview(User user, Pub pub, ReviewRequestDto reviewRequestDto) {
        return reviewRepository.save(Review.builder().reviewer(user).pub(pub).rating(reviewRequestDto.getRating()).service(reviewRequestDto.getService()).toilets(reviewRequestDto.getToilets()).review(reviewRequestDto.getReview()).volume(Volume.fromValue((reviewRequestDto.getVolume()))).reviewDate(LocalDateTime.now()).build());
    }

    private int calculateAverageRating(List<Review> reviews, ToIntFunction<Review> extractor) {
        int[] ratings = reviews.stream().mapToInt(extractor).filter(rating -> rating != 0).toArray();

        return calculateAverage(Arrays.stream(ratings).sum(), ratings.length);
    }

    private Volume calculateAverageVolume(List<Review> reviews) {
        int[] loudness = reviews.stream().filter(r -> r.getVolume() != null).mapToInt(r -> r.getVolume().getOrdinal()).toArray();
        if (loudness.length == 0) return null;

        int value = calculateAverage(Arrays.stream(loudness).sum(), loudness.length);
        return Volume.values()[value];
    }

    private int calculateAverage(int sum, int length) {
        return (int) Math.round((double) sum / length);
    }
}
