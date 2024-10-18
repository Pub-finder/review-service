package com.pubfinder.pubfinder.mapper;

import com.pubfinder.pubfinder.dto.ReviewResponseDto;
import com.pubfinder.pubfinder.dto.VisitedDto;
import com.pubfinder.pubfinder.models.Review;
import com.pubfinder.pubfinder.models.Visited;

public class MapperImpl implements Mapper {

  @Override
  public ReviewResponseDto entityToDto(Review entity) {
    return ReviewResponseDto.builder()
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
  public VisitedDto entityToDto(Visited entity) {
    return VisitedDto.builder()
            .id(entity.getId())
            .visitedDate(entity.getVisitedDate())
            .pubId(entity.getPub().getId())
            .visitorId(entity.getVisitor().getId())
            .build();
  }
}
