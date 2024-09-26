package com.pubfinder.pubfinder.cache;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cache.CacheManager;

@SpringBootTest(properties = {
    "spring.datasource.url=",
    "spring.jpa.database-platform=",
    "spring.jpa.hibernate.ddl-auto=none",
})
public class ReviewServiceCacheTest {

  @Autowired
  private CacheManager cacheManager;

  // Tests if cache activates for similar locations

}
