package com.pubfinder.pubfinder.cache;

import com.pubfinder.pubfinder.db.PubRepository;
import com.pubfinder.pubfinder.db.ReviewRepository;
import com.pubfinder.pubfinder.db.UserRepository;
import com.pubfinder.pubfinder.db.VisitedRepository;
import com.pubfinder.pubfinder.dto.RatingDto;
import com.pubfinder.pubfinder.dto.VisitedDto;
import com.pubfinder.pubfinder.exception.ResourceNotFoundException;
import com.pubfinder.pubfinder.models.Pub;
import com.pubfinder.pubfinder.models.User;
import com.pubfinder.pubfinder.service.ReviewService;
import com.pubfinder.pubfinder.service.VisitedService;
import com.pubfinder.pubfinder.util.TestUtil;
import org.apache.coyote.BadRequestException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.cache.CacheManager;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@SpringBootTest(properties = {
        "spring.datasource.url=",
        "spring.jpa.database-platform=",
        "spring.jpa.hibernate.ddl-auto=none",
})
public class VisitedServiceCacheTest {
    @Autowired
    private VisitedService visitedService;
    @MockBean
    private VisitedRepository visitedRepository;
    @MockBean
    private UserRepository userRepository;
    @Autowired
    private CacheManager cacheManager;

    @Test
    public void getVisitedPubsTest_CacheHit()
            throws ResourceNotFoundException {
        User user = TestUtil.generateMockUser();

        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        when(visitedRepository.findAllByVisitor(user)).thenReturn(TestUtil.generateListOfMockVisits(user));

        List<VisitedDto> response1 = visitedService.getVisitedPubs(user.getId());
        List<VisitedDto> response2 = visitedService.getVisitedPubs(user.getId());

        assertEquals(response1, response2);
        verify(visitedRepository, times(1)).findAllByVisitor(user);
    }
}
