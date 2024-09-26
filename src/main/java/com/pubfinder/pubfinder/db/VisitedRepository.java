package com.pubfinder.pubfinder.db;

import com.pubfinder.pubfinder.models.Pub;
import com.pubfinder.pubfinder.models.User;
import com.pubfinder.pubfinder.models.Visited;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface VisitedRepository extends JpaRepository<Visited, UUID> {

  Optional<Visited> findByPubAndVisitor(Pub pub, User user);

  List<Visited> findAllByVisitor(User user);

  void deleteAllByVisitor(User user);
}
