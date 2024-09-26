package com.pubfinder.pubfinder.dto;

import com.pubfinder.pubfinder.models.enums.Volume;
import java.io.Serializable;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RatingDto implements Serializable {

  private UUID pubId;
  private int rating;
  private int toiletRating;
  private int serviceRating;
  private Volume volume;
}