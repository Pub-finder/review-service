package com.pubfinder.pubfinder.mapper;


import static org.junit.jupiter.api.Assertions.assertEquals;

import com.pubfinder.pubfinder.dto.ReviewRequestDto;
import com.pubfinder.pubfinder.dto.ReviewResponseDto;
import com.pubfinder.pubfinder.dto.VisitedDto;
import com.pubfinder.pubfinder.models.Review;
import com.pubfinder.pubfinder.models.Visited;
import com.pubfinder.pubfinder.util.TestUtil;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(properties = {
    "spring.cache.type=none",
    "bucket4j.enabled=false",
    "spring.datasource.url=",
    "spring.jpa.database-platform=",
    "spring.jpa.hibernate.ddl-auto=none"
})
public class MapperTest {


  @Test
  public void mapReviewEntityToDtoTest() {
    Review review = TestUtil.generateMockReview(TestUtil.generateMockUser(), TestUtil.generateMockPub());
    ReviewResponseDto reviewResponseDto = Mapper.INSTANCE.entityToDto(review);
    checkReview(reviewResponseDto, review);
    assertEquals(reviewResponseDto.getId(), review.getId());
    assertEquals(reviewResponseDto.getPubId(), review.getPub().getId());
    assertEquals(reviewResponseDto.getUserId(), review.getReviewer().getId());
    assertEquals(reviewResponseDto.getUsername(), review.getReviewer().getUsername());
  }

  @Test
  public void mapVisitedEntityToDtoTest() {
    Visited visited = TestUtil.generateMockVisited(TestUtil.generateMockUser());
    VisitedDto visitedDto = Mapper.INSTANCE.entityToDto(visited);
    assertEquals(visited.getId(), visitedDto.getId());
    assertEquals(visited.getVisitedDate(), visitedDto.getVisitedDate());
    assertEquals(visited.getPub().getId(), visitedDto.getPubId());
    assertEquals(visited.getVisitor().getId(), visitedDto.getVisitorId());
  }

  private void checkReview(ReviewResponseDto dto, Review entity) {
    assertEquals(dto.getReview(), entity.getReview());
    assertEquals(dto.getRating(), entity.getRating());
    assertEquals(dto.getToilets(), entity.getToilets());
    assertEquals(dto.getVolume(), entity.getVolume());
    assertEquals(dto.getService(), entity.getService());
  }

}
