package com.pubfinder.pubfinder.mapper;

import com.pubfinder.pubfinder.dto.ReviewRequestDto;
import com.pubfinder.pubfinder.dto.ReviewResponseDto;
import com.pubfinder.pubfinder.dto.VisitedDto;
import com.pubfinder.pubfinder.models.Review;
import com.pubfinder.pubfinder.models.Visited;
import org.mapstruct.factory.Mappers;

@org.mapstruct.Mapper
public interface Mapper {

  Mapper INSTANCE = Mappers.getMapper(Mapper.class);

  ReviewResponseDto entityToDto(Review entity);

  VisitedDto entityToDto(Visited entity);
}