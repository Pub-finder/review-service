package com.pubfinder.pubfinder.service;

import com.pubfinder.pubfinder.db.PubRepository;
import com.pubfinder.pubfinder.db.UserRepository;
import com.pubfinder.pubfinder.db.VisitedRepository;
import com.pubfinder.pubfinder.dto.VisitDto;
import com.pubfinder.pubfinder.dto.VisitedDto;
import com.pubfinder.pubfinder.exception.ResourceNotFoundException;
import com.pubfinder.pubfinder.mapper.Mapper;
import com.pubfinder.pubfinder.models.Pub;
import com.pubfinder.pubfinder.models.User;
import com.pubfinder.pubfinder.models.Visited;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

/**
 * The type Visited service.
 */
@Service
public class VisitedService {

  @Autowired
  private VisitedRepository visitedRepository;

  @Autowired
  private UserRepository userRepository;

  @Autowired
  private PubRepository pubRepository;

  /**
   * Save visit.
   *
   * @param visit    the visit containg the visitors userId and username and the pubs Id
   */
  public void save(VisitDto visit) {
    User user = userRepository.findById(visit.getUserId())
            .orElseGet(() -> userRepository.save(User.builder().id(visit.getUserId()).username(visit.getUsername()).build()));

    Pub pub = pubRepository.findById(visit.getPubId())
            .orElseGet(() -> pubRepository.save(Pub.builder().id(visit.getPubId()).build()));

    Optional<Visited> uvp = visitedRepository.findByPubAndVisitor(pub, user);

    Visited visited;
    if (uvp.isPresent()) {
      visited = uvp.get();
      visited.setVisitedDate(LocalDateTime.now());
    } else {
      visited = Visited.builder().visitor(user).pub(pub).visitedDate(LocalDateTime.now()).build();
    }
    visitedRepository.save(visited);
  }

  /**
   * Delete visit.
   *
   * @param userId the visitors id
   * @param pubId the pubs id
   * @throws ResourceNotFoundException the resource not found exception
   */
  public void delete(UUID userId, UUID pubId) throws ResourceNotFoundException {
    Pub pub = pubRepository.findById(pubId)
            .orElseThrow(() -> new ResourceNotFoundException("Pub with id " + pubId + " was not found"));

    User user = userRepository.findById(userId)
            .orElseThrow(() -> new ResourceNotFoundException("User with id " + userId + " was not found"));

    Visited visited = visitedRepository.findByPubAndVisitor(pub, user).orElseThrow(
        () -> new ResourceNotFoundException(
            "Visited with userId: " + userId + " and pubId: " + pubId + " was not found"));
    visitedRepository.delete(visited);
  }

  @Cacheable(value = "getVisitedPubs")
  public List<VisitedDto> getVisitedPubs(UUID id) throws ResourceNotFoundException {
    User user = userRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("User with id " + id + " was not found"));

    return visitedRepository.findAllByVisitor(user)
            .stream()
            .map(Mapper.INSTANCE::entityToDto)
            .toList();
  }
}
