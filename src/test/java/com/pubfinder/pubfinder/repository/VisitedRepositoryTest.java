package com.pubfinder.pubfinder.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.pubfinder.pubfinder.db.PubRepository;
import com.pubfinder.pubfinder.db.UserRepository;
import com.pubfinder.pubfinder.db.VisitedRepository;
import com.pubfinder.pubfinder.models.Pub;
import com.pubfinder.pubfinder.models.User;
import com.pubfinder.pubfinder.models.Visited;
import com.pubfinder.pubfinder.util.TestUtil;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.TestPropertySource;

@DataJpaTest
@TestPropertySource(properties = {
    "spring.test.database.replace=none",
    "spring.datasource.url=jdbc:tc:postgresql:16-alpine:///db"
})
public class VisitedRepositoryTest {

  @Autowired
  private UserRepository userRepository;

  @Autowired
  private PubRepository pubRepository;

  @Autowired
  private VisitedRepository visitedRepository;

  @Test
  public void saveAndGetTest() {
    Visited savedUVP = visitedRepository.save(uvp);
    Optional<Visited> foundUVP = visitedRepository.findById(savedUVP.getId());

    assertTrue(foundUVP.isPresent());
    assertEquals(foundUVP.get(), savedUVP);
  }

  @Test
  public void saveAndGetByPubAndUserTest() {
    User user = userRepository.save(uvp.getVisitor());
    Pub pub = pubRepository.save(uvp.getPub());
    uvp.setVisitor(user);
    uvp.setPub(pub);

    Visited savedUVP = visitedRepository.save(uvp);
    Optional<Visited> foundUVP = visitedRepository.findByPubAndVisitor(pub, user);

    assertTrue(foundUVP.isPresent());
    assertEquals(foundUVP.get(), savedUVP);
  }

  @Test
  public void deleteAllByUserTest() {
    User user = userRepository.save(uvp.getVisitor());
    Pub pub1 = pubRepository.save(uvp.getPub());
    Pub pub2 = pubRepository.save(uvp.getPub());
    Pub pub3 = pubRepository.save(uvp.getPub());
    Visited uvp1 = Visited.builder()
        .visitor(user)
        .pub(pub1)
        .visitedDate(LocalDateTime.now())
        .build();

    Visited uvp2 = Visited.builder()
        .visitor(user)
        .pub(pub2)
        .visitedDate(LocalDateTime.now())
        .build();

    Visited uvp3 = Visited.builder()
        .visitor(user)
        .pub(pub3)
        .visitedDate(LocalDateTime.now())
        .build();

    visitedRepository.saveAll(Arrays.asList(uvp1, uvp2, uvp3));

    List<Visited> uvps1 = visitedRepository.findAllByVisitor(user);
    assertEquals(3, uvps1.size());

    visitedRepository.deleteAllByVisitor(user);

    List<Visited> uvps2 = visitedRepository.findAllByVisitor(user);
    assertEquals(0, uvps2.size());
  }

  @Test
  public void deleteTest() {
    Visited savedUVP = visitedRepository.save(uvp);
    visitedRepository.delete(savedUVP);
    Optional<Visited> foundUVP = visitedRepository.findById(savedUVP.getId());
    assertTrue(foundUVP.isEmpty());
  }

  @Test
  public void editTest() {
    Visited savedUVP = visitedRepository.save(uvp);
    savedUVP.setVisitedDate(LocalDateTime.now());
    Visited editedUVP = visitedRepository.save(uvp);
    assertEquals(savedUVP, editedUVP);
  }

  private final Visited uvp = TestUtil.generateUserVisitedPub();
}
