package com.pubfinder.pubfinder.dto;

import com.pubfinder.pubfinder.models.enums.Volume;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ReviewResponseDto implements Serializable {

    private UUID id;
    private UUID pubId;
    private UUID userId;
    private String username;
    private LocalDateTime reviewDate;
    private String review;
    private int rating;
    private int toilets;
    private Volume volume;
    private int service;
}
