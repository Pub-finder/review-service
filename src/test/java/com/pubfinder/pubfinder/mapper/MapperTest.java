package com.pubfinder.pubfinder.mapper;


import static org.junit.jupiter.api.Assertions.assertEquals;

import com.pubfinder.pubfinder.dto.ReviewDto;
import com.pubfinder.pubfinder.models.Review;
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
  public void mapPubEntityToDtoTest() {
    Review review = TestUtil.generateMockReview(TestUtil.generateMockUser(), TestUtil.generateMockPub());
    ReviewDto reviewDto = Mapper.INSTANCE.entityToDto(review);
    checkReview(reviewDto, review);
  }

  private void checkReview(ReviewDto dto, Review entity) {
    assertEquals(dto.getId(), entity.getId());
    assertEquals(dto.getPubId(), entity.getPub().getId());
    assertEquals(dto.getUserId(), entity.getReviewer().getId());
    assertEquals(dto.getUsername(), entity.getReviewer().getUsername());
    assertEquals(dto.getReview(), entity.getReview());
    assertEquals(dto.getRating(), entity.getRating());
    assertEquals(dto.getToilets(), entity.getToilets());
    assertEquals(dto.getVolume(), entity.getVolume());
    assertEquals(dto.getService(), entity.getService());
  }

}
