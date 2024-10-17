package com.pubfinder.pubfinder.controller;

import com.pubfinder.pubfinder.dto.RatingDto;
import com.pubfinder.pubfinder.dto.ReviewRequestDto;
import com.pubfinder.pubfinder.dto.ReviewResponseDto;
import com.pubfinder.pubfinder.exception.ResourceNotFoundException;
import com.pubfinder.pubfinder.service.ReviewService;

import java.util.List;
import java.util.UUID;

import org.apache.coyote.BadRequestException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * The type Review controller.
 */
@RestController
@RequestMapping("/review")
public class ReviewController {

    @Autowired
    private ReviewService reviewService;

    @PostMapping("/save")
    public ResponseEntity<ReviewResponseDto> save(@RequestBody ReviewRequestDto review) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(reviewService.save(review));
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Void> delete(@PathVariable("id") UUID id)
            throws ResourceNotFoundException, BadRequestException {
        reviewService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/rating/{id}")
    public ResponseEntity<RatingDto> getPubRating(@PathVariable("id") UUID id)
            throws ResourceNotFoundException, BadRequestException {
        return ResponseEntity.ok(reviewService.getPubRating(id));
    }

    @GetMapping("/reviews/pub/{id}")
    public ResponseEntity<List<ReviewResponseDto>> getPubReviews(@PathVariable("id") UUID id) throws ResourceNotFoundException, BadRequestException {
        return ResponseEntity.ok(reviewService.getPubReviews(id));
    }

    @GetMapping("/reviews/user/{id}")
    public ResponseEntity<List<ReviewResponseDto>> getUserReviews(@PathVariable("id") UUID id) throws BadRequestException, ResourceNotFoundException {
        return ResponseEntity.ok(reviewService.getUserReviews(id));
    }
}
