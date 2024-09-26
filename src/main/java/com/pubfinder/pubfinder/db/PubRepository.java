package com.pubfinder.pubfinder.db;

import com.pubfinder.pubfinder.models.Pub;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PubRepository extends JpaRepository<Pub, UUID> {

}
