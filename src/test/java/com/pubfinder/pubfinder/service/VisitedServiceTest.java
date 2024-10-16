package com.pubfinder.pubfinder.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.pubfinder.pubfinder.db.PubRepository;
import com.pubfinder.pubfinder.db.UserRepository;
import com.pubfinder.pubfinder.db.VisitedRepository;
import com.pubfinder.pubfinder.dto.VisitDto;
import com.pubfinder.pubfinder.dto.VisitedDto;
import com.pubfinder.pubfinder.exception.ResourceNotFoundException;
import com.pubfinder.pubfinder.models.Pub;
import com.pubfinder.pubfinder.models.User;
import com.pubfinder.pubfinder.models.Visited;
import com.pubfinder.pubfinder.util.TestUtil;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

@SpringBootTest(properties = {
    "spring.cache.type=none",
    "bucket4j.enabled=false",
    "spring.datasource.url=",
    "spring.jpa.database-platform=",
    "spring.jpa.hibernate.ddl-auto=none"
})
public class VisitedServiceTest {

  @Autowired
  private VisitedService visitedService;

  @MockBean
  private VisitedRepository visitedRepository;

  @MockBean
  private UserRepository userRepository;

  @MockBean
  private PubRepository pubRepository;

  @Test
  public void saveTest() throws ResourceNotFoundException {
    User user = TestUtil.generateMockUser();
    Pub pub = TestUtil.generateMockPub();
    Visited visited = TestUtil.generateMockVisited(user);

    when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
    when(pubRepository.findById(pub.getId())).thenReturn(Optional.of(pub));
    when(visitedRepository.findByPubAndVisitor(pub, user)).thenReturn(Optional.of(visited));

    visitedService.save(VisitDto.builder().pubId(pub.getId()).userId(user.getId()).username(user.getUsername()).build());

    verify(visitedRepository, times(1)).findByPubAndVisitor(pub, user);
    verify(visitedRepository, times(1)).save(any(Visited.class));
  }

  @Test
  public void saveVisitTest_UserNotFound() {
    User user = TestUtil.generateMockUser();
    Pub pub = TestUtil.generateMockPub();
    Visited visited = TestUtil.generateMockVisited(user);

    when(userRepository.findById(user.getId())).thenReturn(Optional.empty());
    when(userRepository.save(user)).thenReturn(user);
    when(pubRepository.findById(pub.getId())).thenReturn(Optional.of(pub));
    when(visitedRepository.findByPubAndVisitor(pub, user)).thenReturn(Optional.of(visited));

    visitedService.save(VisitDto.builder().pubId(pub.getId()).userId(user.getId()).username(user.getUsername()).build());

    verify(userRepository, times(1)).save(user);
    verify(visitedRepository, times(1)).findByPubAndVisitor(pub, user);
    verify(visitedRepository, times(1)).save(any(Visited.class));
  }

  @Test
  public void saveReviewTest_PubNotFound() {
    User user = TestUtil.generateMockUser();
    Pub pub = TestUtil.generateMockPub();
    Visited visited = TestUtil.generateMockVisited(user);

    when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
    when(pubRepository.findById(pub.getId())).thenReturn(Optional.empty());
    when(pubRepository.save(pub)).thenReturn(pub);
    when(visitedRepository.findByPubAndVisitor(pub, user)).thenReturn(Optional.of(visited));

    visitedService.save(VisitDto.builder().pubId(pub.getId()).userId(user.getId()).username(user.getUsername()).build());

    verify(pubRepository, times(1)).save(pub);
    verify(visitedRepository, times(1)).findByPubAndVisitor(pub, user);
    verify(visitedRepository, times(1)).save(any(Visited.class));
  }

  @Test
  public void deleteTest() throws ResourceNotFoundException {
    Visited visited = TestUtil.generateMockVisited(TestUtil.generateMockUser());
    when(userRepository.findById(visited.getVisitor().getId())).thenReturn(Optional.ofNullable(visited.getVisitor()));
    when(pubRepository.findById(visited.getPub().getId())).thenReturn(Optional.of(visited.getPub()));
    when(visitedRepository.findByPubAndVisitor(visited.getPub(), visited.getVisitor())).thenReturn(Optional.of(visited));
    visitedService.delete(visited.getVisitor().getId(), visited.getPub().getId());
    verify(visitedRepository, times(1)).delete(visited);
  }

  @Test
  public void deleteTest_PubNotFound() {
    when(pubRepository.findById(any())).thenReturn(Optional.empty());
    assertThrows(ResourceNotFoundException.class, () -> visitedService.delete(UUID.randomUUID(), UUID.randomUUID()));
  }


  @Test
  public void deleteTest_UserNotFound() {
    Visited visited = TestUtil.generateMockVisited(TestUtil.generateMockUser());
    when(userRepository.findById(visited.getVisitor().getId())).thenReturn(Optional.ofNullable(visited.getVisitor()));
    when(userRepository.findById(any())).thenReturn(Optional.empty());
    assertThrows(ResourceNotFoundException.class, () -> visitedService.delete(UUID.randomUUID(), UUID.randomUUID()));
  }

  @Test
  public void deleteTest_NotFound() {
    Visited visited = TestUtil.generateMockVisited(TestUtil.generateMockUser());
    when(userRepository.findById(visited.getVisitor().getId())).thenReturn(Optional.ofNullable(visited.getVisitor()));
    when(pubRepository.findById(visited.getPub().getId())).thenReturn(Optional.of(visited.getPub()));
    when(visitedRepository.findByPubAndVisitor(visited.getPub(), visited.getVisitor())).thenReturn(Optional.empty());
    assertThrows(ResourceNotFoundException.class, () -> visitedService.delete(UUID.randomUUID(), UUID.randomUUID()));
  }

  @Test
  public void getVisitedPubsTest() throws ResourceNotFoundException {
    User user = TestUtil.generateMockUser();
    List<Visited> visits = TestUtil.generateListOfMockVisits(user);

    when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
    when(visitedRepository.findAllByVisitor(user)).thenReturn(visits);

    List<VisitedDto> result = visitedService.getVisitedPubs(user.getId());

    assertEquals(visits.size(), result.size());
  }

  @Test
  public void getVisitedPubsTest_NotFound() {
    User user = TestUtil.generateMockUser();
    when(userRepository.findById(user.getId())).thenReturn(Optional.empty());
    assertThrows(ResourceNotFoundException.class, () -> visitedService.getVisitedPubs(user.getId()));
  }
}
