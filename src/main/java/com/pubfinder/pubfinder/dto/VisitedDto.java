package com.pubfinder.pubfinder.dto;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class VisitedDto implements Serializable {
  private UUID id;
  private LocalDateTime visitedDate;
  private UUID pubId;
  private UUID visitorId;
}
