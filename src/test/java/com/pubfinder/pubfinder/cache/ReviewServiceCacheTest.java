package com.pubfinder.pubfinder.cache;

import com.pubfinder.pubfinder.db.PubRepository;
import com.pubfinder.pubfinder.db.ReviewRepository;
import com.pubfinder.pubfinder.db.UserRepository;
import com.pubfinder.pubfinder.dto.RatingDto;
import com.pubfinder.pubfinder.dto.ReviewRequestDto;
import com.pubfinder.pubfinder.dto.ReviewResponseDto;
import com.pubfinder.pubfinder.exception.ResourceNotFoundException;
import com.pubfinder.pubfinder.models.Pub;
import com.pubfinder.pubfinder.models.Review;
import com.pubfinder.pubfinder.models.User;
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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@SpringBootTest(properties = {
        "spring.datasource.url=",
        "spring.jpa.database-platform=",
        "spring.jpa.hibernate.ddl-auto=none",
})
public class ReviewServiceCacheTest {
    @Autowired
    private ReviewService reviewService;
    @MockBean
    private UserRepository userRepository;
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
    public void getPubRatingTest_CacheMiss_SaveReview()
            throws BadRequestException, ResourceNotFoundException {
        Pub pub = TestUtil.generateMockPub();
        User user = TestUtil.generateMockUser();

        when(pubRepository.findById(pub.getId())).thenReturn(Optional.of(pub));
        when(reviewRepository.findAllByPub(pub)).thenReturn(TestUtil.generateListOfMockReviews(pub));

       reviewService.getPubRating(pub.getId());
        mockSaveReview(user, pub);
        reviewService.getPubRating(pub.getId());

        verify(reviewRepository, times(2)).findAllByPub(pub);
    }

    @Test
    public void getPubRatingTest_CacheMiss_DeleteReview()
            throws BadRequestException, ResourceNotFoundException {
        Pub pub = TestUtil.generateMockPub();
        User user = TestUtil.generateMockUser();

        when(pubRepository.findById(pub.getId())).thenReturn(Optional.of(pub));
        when(reviewRepository.findAllByPub(pub)).thenReturn(TestUtil.generateListOfMockReviews(pub));

        reviewService.getPubRating(pub.getId());
        mockDeleteReview(user, pub);
        reviewService.getPubRating(pub.getId());

        verify(reviewRepository, times(2)).findAllByPub(pub);
    }

    @Test
    public void getPubReviews_CacheHit()
            throws BadRequestException {
        Pub pub = TestUtil.generateMockPub();

        when(pubRepository.findById(pub.getId())).thenReturn(Optional.of(pub));
        when(reviewRepository.findAllByPub(pub)).thenReturn(TestUtil.generateListOfMockReviews(pub));

        List<ReviewResponseDto> response1 = reviewService.getPubReviews(pub.getId());
        List<ReviewResponseDto> response2 = reviewService.getPubReviews(pub.getId());

        assertEquals(response1, response2);
        verify(reviewRepository, times(1)).findAllByPub(pub);
    }

    @Test
    public void getPubReviews_CacheMiss_SaveReview()
            throws BadRequestException {
        Pub pub = TestUtil.generateMockPub();
        User user = TestUtil.generateMockUser();

        when(pubRepository.findById(pub.getId())).thenReturn(Optional.of(pub));
        when(reviewRepository.findAllByPub(pub)).thenReturn(TestUtil.generateListOfMockReviews(pub));

        reviewService.getPubReviews(pub.getId());
        mockSaveReview(user, pub);
        reviewService.getPubReviews(pub.getId());

        verify(reviewRepository, times(2)).findAllByPub(pub);
    }

    @Test
    public void getPubReviews_CacheMiss_DeleteReview()
            throws BadRequestException, ResourceNotFoundException {
        Pub pub = TestUtil.generateMockPub();
        User user = TestUtil.generateMockUser();

        when(pubRepository.findById(pub.getId())).thenReturn(Optional.of(pub));
        when(reviewRepository.findAllByPub(pub)).thenReturn(TestUtil.generateListOfMockReviews(pub));

        reviewService.getPubReviews(pub.getId());
        mockDeleteReview(user, pub);
        reviewService.getPubReviews(pub.getId());

        verify(reviewRepository, times(2)).findAllByPub(pub);
    }

    @Test
    public void getUserReviews_CacheHit()
            throws BadRequestException {
        Pub pub = TestUtil.generateMockPub();
        User user = TestUtil.generateMockUser();

        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        when(reviewRepository.findAllByReviewer(user)).thenReturn(TestUtil.generateListOfMockReviews(pub));

        List<ReviewResponseDto> response1 = reviewService.getUserReviews(user.getId());
        List<ReviewResponseDto> response2 = reviewService.getUserReviews(user.getId());

        assertEquals(response1, response2);
        verify(reviewRepository, times(1)).findAllByReviewer(user);
    }

    @Test
    public void getUserReviews_CacheMiss_SaveReview()
            throws BadRequestException {
        Pub pub = TestUtil.generateMockPub();
        User user = TestUtil.generateMockUser();

        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        when(reviewRepository.findAllByReviewer(user)).thenReturn(TestUtil.generateListOfMockReviews(pub));

        reviewService.getUserReviews(user.getId());
        mockSaveReview(user, pub);
        reviewService.getUserReviews(user.getId());

        verify(reviewRepository, times(2)).findAllByReviewer(user);
    }

    @Test
    public void getUserReviews_CacheMiss_DeleteReview()
            throws BadRequestException, ResourceNotFoundException {
        Pub pub = TestUtil.generateMockPub();
        User user = TestUtil.generateMockUser();

        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        when(reviewRepository.findAllByReviewer(user)).thenReturn(TestUtil.generateListOfMockReviews(pub));

        reviewService.getUserReviews(user.getId());
        mockDeleteReview(user, pub);
        reviewService.getUserReviews(user.getId());

        verify(reviewRepository, times(2)).findAllByReviewer(user);
    }

    private void mockSaveReview(User user, Pub pub) {
        ReviewRequestDto reviewRequestDto = TestUtil.generateMockReviewRequestDTO();
        reviewRequestDto.setUserId(user.getId());
        reviewRequestDto.setPubId(pub.getId());

        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        when(pubRepository.findById(reviewRequestDto.getPubId())).thenReturn(Optional.of(pub));
        when(reviewRepository.findByPubAndReviewer(pub, user)).thenReturn(Optional.empty());
        when(reviewRepository.save(any())).thenReturn(TestUtil.generateMockReview(user, pub));
        reviewService.save(reviewRequestDto);
    }

    private void mockDeleteReview(User user, Pub pub) throws BadRequestException, ResourceNotFoundException {
        Review review = TestUtil.generateMockReview(user, pub);
        when(reviewRepository.findById(review.getId())).thenReturn(Optional.of(review));
        doNothing().when(reviewRepository).delete(review);
        reviewService.delete(review.getId());
    }
}
