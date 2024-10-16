package com.pubfinder.pubfinder.models;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.io.Serializable;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * The type Pub.
 */
@Entity(name = "Pub")
@Table(name = "pub")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Pub implements Serializable {

  @Id
  @Column(unique = true, nullable = false)
  private UUID id;
}