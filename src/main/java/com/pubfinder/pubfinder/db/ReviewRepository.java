package com.pubfinder.pubfinder.db;

import com.pubfinder.pubfinder.models.Pub;
import com.pubfinder.pubfinder.models.Review;
import com.pubfinder.pubfinder.models.User;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReviewRepository extends JpaRepository<Review, UUID> {

  Optional<Review> findByPubAndReviewer(Pub pub, User user);

  void deleteAllByReviewer(User user);

  List<Review> findAllByPub(Pub pub);

  List<Review> findAllByReviewer(User user);
}
