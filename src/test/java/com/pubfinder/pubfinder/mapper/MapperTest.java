package com.pubfinder.pubfinder.mapper;


import static org.junit.jupiter.api.Assertions.assertEquals;

import com.pubfinder.pubfinder.dto.ReviewDto;
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
  public void mapReviewDtoToEntityTest() {
    ReviewDto reviewDto = TestUtil.generateMockReviewDTO();
    Review review = Mapper.INSTANCE.dtoToEntity(reviewDto);
    checkReview(reviewDto, review);
  }

  @Test
  public void mapReviewEntityToDtoTest() {
    Review review = TestUtil.generateMockReview(TestUtil.generateMockUser(), TestUtil.generateMockPub());
    ReviewDto reviewDto = Mapper.INSTANCE.entityToDto(review);
    checkReview(reviewDto, review);
    assertEquals(reviewDto.getId(), review.getId());
    assertEquals(reviewDto.getPubId(), review.getPub().getId());
    assertEquals(reviewDto.getUserId(), review.getReviewer().getId());
    assertEquals(reviewDto.getUsername(), review.getReviewer().getUsername());
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

  private void checkReview(ReviewDto dto, Review entity) {
    assertEquals(dto.getReview(), entity.getReview());
    assertEquals(dto.getRating(), entity.getRating());
    assertEquals(dto.getToilets(), entity.getToilets());
    assertEquals(dto.getVolume(), entity.getVolume());
    assertEquals(dto.getService(), entity.getService());
  }

}
