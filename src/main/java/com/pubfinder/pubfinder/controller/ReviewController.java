package com.pubfinder.pubfinder.controller;

import com.pubfinder.pubfinder.dto.RatingDto;
import com.pubfinder.pubfinder.dto.ReviewDto;
import com.pubfinder.pubfinder.exception.ResourceNotFoundException;
import com.pubfinder.pubfinder.exception.ReviewAlreadyExistsException;
import com.pubfinder.pubfinder.mapper.Mapper;
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

    @PostMapping("/save/{pubId}/{userId}")
    public ResponseEntity<ReviewDto> save(@RequestBody ReviewDto review, @PathVariable("pubId") UUID pudId, @PathVariable("userId") UUID userId)
            throws ReviewAlreadyExistsException, ResourceNotFoundException, BadRequestException {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(reviewService.save(Mapper.INSTANCE.dtoToEntity(review), pudId, userId));
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Void> delete(@PathVariable("id") UUID id)
            throws ResourceNotFoundException, BadRequestException {
        reviewService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/edit")
    public ResponseEntity<ReviewDto> edit(@RequestBody ReviewDto review)
            throws ResourceNotFoundException {
        return ResponseEntity.ok(reviewService.edit(Mapper.INSTANCE.dtoToEntity(review)));
    }

    @GetMapping("/rating/{id}")
    public ResponseEntity<RatingDto> getPubRating(@PathVariable("id") UUID id)
            throws ResourceNotFoundException, BadRequestException {
        return ResponseEntity.ok(reviewService.getPubRating(id));
    }

    @GetMapping("/reviews/pub/{id}")
    public ResponseEntity<List<ReviewDto>> getPubReviews(@PathVariable("id") UUID id) throws ResourceNotFoundException {
        return ResponseEntity.ok(reviewService.getPubReviews(id));
    }

    @GetMapping("/reviews/user/{id}")
    public ResponseEntity<List<ReviewDto>> getUserReviews(@PathVariable("id") UUID id) throws BadRequestException, ResourceNotFoundException {
        return ResponseEntity.ok(reviewService.getUserReviews(id));
    }
}
