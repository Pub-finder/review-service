package com.pubfinder.pubfinder.service;

import com.pubfinder.pubfinder.db.PubRepository;
import com.pubfinder.pubfinder.db.UserRepository;
import com.pubfinder.pubfinder.db.VisitedRepository;
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
   * @param pubId    the pub id
   * @param userId   the visitors id
   * @throws ResourceNotFoundException user or pub not found exception
   */
  public void save(UUID pubId, UUID userId) throws ResourceNotFoundException {
    Optional<User> user = userRepository.findById(userId);
    if (user.isEmpty()) {
      // TODO: Make call to check if the user exist in the user db
      throw new ResourceNotFoundException("User: " + userId + " does not exist");
    }

    Optional<Pub> pub = pubRepository.findById(pubId);
    if (pub.isEmpty()) {
      // TODO: Make call to check if the pub exist in the pub db
      throw new ResourceNotFoundException("Pub: " + pubId + " does not exist");
    }

    Optional<Visited> uvp = visitedRepository.findByPubAndVisitor(pub.get(), user.get());
    Visited visited;
    if (uvp.isPresent()) {
      visited = uvp.get();
      visited.setVisitedDate(LocalDateTime.now());
    } else {
      visited = Visited.builder().visitor(user.get()).pub(pub.get()).visitedDate(LocalDateTime.now()).build();
    }
    visitedRepository.save(visited);
  }

  /**
   * Delete visit.
   *
   * @param id the id
   * @throws ResourceNotFoundException the resource not found exception
   */
  public void delete(UUID id) throws ResourceNotFoundException {
    Visited visited = visitedRepository.findById(id).orElseThrow(
        () -> new ResourceNotFoundException(
            "Visited with id: " + id + " not found"));
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
