package com.pubfinder.pubfinder.cache;

import com.pubfinder.pubfinder.db.PubRepository;
import com.pubfinder.pubfinder.db.UserRepository;
import com.pubfinder.pubfinder.db.VisitedRepository;
import com.pubfinder.pubfinder.dto.VisitDto;
import com.pubfinder.pubfinder.dto.VisitedDto;
import com.pubfinder.pubfinder.exception.ResourceNotFoundException;
import com.pubfinder.pubfinder.models.Pub;
import com.pubfinder.pubfinder.models.User;
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
    private PubRepository pubRepository;
    @MockBean
    private UserRepository userRepository;
    @Autowired
    private CacheManager cacheManager;

    @Test
    public void getVisitedPubsTest_CacheHit()
            throws BadRequestException {
        User user = TestUtil.generateMockUser();

        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        when(visitedRepository.findAllByVisitor(user)).thenReturn(TestUtil.generateListOfMockVisits(user));

        List<VisitedDto> response1 = visitedService.getVisitedPubs(user.getId());
        List<VisitedDto> response2 = visitedService.getVisitedPubs(user.getId());

        assertEquals(response1, response2);
        verify(visitedRepository, times(1)).findAllByVisitor(user);
    }

    @Test
    public void getVisitedPubsTest_CacheMiss_SavedVisit()
            throws BadRequestException {
        User user = TestUtil.generateMockUser();
        Pub pub = TestUtil.generateMockPub();

        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        when(visitedRepository.findAllByVisitor(user)).thenReturn(TestUtil.generateListOfMockVisits(user));

        visitedService.getVisitedPubs(user.getId());
        mockSaveVisit(user, pub);
        visitedService.getVisitedPubs(user.getId());

        verify(visitedRepository, times(2)).findAllByVisitor(user);
    }

    @Test
    public void getVisitedPubsTest_CacheMiss_DeleteVisit()
            throws ResourceNotFoundException, BadRequestException {
        User user = TestUtil.generateMockUser();
        Pub pub = TestUtil.generateMockPub();

        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        when(visitedRepository.findAllByVisitor(user)).thenReturn(TestUtil.generateListOfMockVisits(user));

        visitedService.getVisitedPubs(user.getId());
        mockDeleteVisit(user, pub);
        visitedService.getVisitedPubs(user.getId());

        verify(visitedRepository, times(2)).findAllByVisitor(user);
    }

    private void mockSaveVisit(User user, Pub pub) {
        VisitDto visitDto = TestUtil.generateVisitDto();
        visitDto.setPubId(pub.getId());
        visitDto.setUserId(user.getId());

        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        when(pubRepository.findById(pub.getId())).thenReturn(Optional.of(pub));
        when(visitedRepository.findByPubAndVisitor(pub, user)).thenReturn(Optional.ofNullable(TestUtil.generateMockVisited(user)));
        visitedService.save(visitDto);
    }

    private void mockDeleteVisit(User user, Pub pub) throws ResourceNotFoundException {
        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        when(pubRepository.findById(pub.getId())).thenReturn(Optional.of(pub));
        when(visitedRepository.findByPubAndVisitor(pub, user)).thenReturn(Optional.ofNullable(TestUtil.generateMockVisited(user)));
        visitedService.delete(user.getId(), pub.getId());
    }
}
