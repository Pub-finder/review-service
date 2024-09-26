package com.pubfinder.pubfinder.mapper;

import com.pubfinder.pubfinder.dto.ReviewDto;
import com.pubfinder.pubfinder.dto.VisitedDto;
import com.pubfinder.pubfinder.models.Review;
import com.pubfinder.pubfinder.models.Visited;
import org.mapstruct.factory.Mappers;

@org.mapstruct.Mapper
public interface Mapper {

  Mapper INSTANCE = Mappers.getMapper(Mapper.class);

  ReviewDto entityToDto(Review entity);

  Review dtoToEntity(ReviewDto dto);

  VisitedDto entityToDto(Visited entity);
}