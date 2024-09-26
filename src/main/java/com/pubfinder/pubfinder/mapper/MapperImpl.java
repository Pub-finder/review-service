package com.pubfinder.pubfinder.mapper;

import com.pubfinder.pubfinder.dto.ReviewDto;
import com.pubfinder.pubfinder.dto.VisitedDto;
import com.pubfinder.pubfinder.models.Review;
import com.pubfinder.pubfinder.models.Visited;

public class MapperImpl implements Mapper {

  @Override
  public ReviewDto entityToDto(Review entity) {
    return ReviewDto.builder()
        .id(entity.getId())
        .pubId(entity.getPub().getId())
        .reviewDate(entity.getReviewDate())
        .userId(entity.getReviewer().getId())
        .username(entity.getReviewer().getUsername())
        .review(entity.getReview())
        .rating(entity.getRating())
        .toilets(entity.getToilets())
        .volume(entity.getVolume())
        .service(entity.getService())
        .build();
  }


  @Override
  public Review dtoToEntity(ReviewDto dto) {
    return Review.builder()
        .review(dto.getReview())
        .rating(dto.getRating())
        .toilets(dto.getToilets())
        .volume(dto.getVolume())
        .service(dto.getService())
        .build();
  }

  @Override
  public VisitedDto entityToDto(Visited entity) {
    return VisitedDto.builder()
            .id(entity.getId())
            .visitedDate(entity.getVisitedDate())
            .pubId(entity.getPub().getId())
            .visitorId(entity.getVisitor().getId())
            .build();
  }
}
