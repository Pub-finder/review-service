package com.pubfinder.pubfinder.controller;

import com.pubfinder.pubfinder.dto.VisitDto;
import com.pubfinder.pubfinder.dto.VisitedDto;
import com.pubfinder.pubfinder.exception.ResourceNotFoundException;
import com.pubfinder.pubfinder.service.VisitedService;

import java.util.List;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/visited")
public class VisitedController {

  @Autowired
  private VisitedService visitedService;

  @PostMapping("/save")
  public ResponseEntity<VisitedDto> save(@RequestBody VisitDto visit) throws ResourceNotFoundException {
    visitedService.save(visit);
    return ResponseEntity.status(HttpStatus.CREATED).build();
  }

  @DeleteMapping("/delete/{userId}/{pubId}")
  public ResponseEntity<Void> delete(@PathVariable("userId") UUID userId, @PathVariable("pubId") UUID pubId) throws ResourceNotFoundException {
    visitedService.delete(userId, pubId);
    return ResponseEntity.noContent().build();
  }

  @GetMapping("/visits/{id}")
  public ResponseEntity<List<VisitedDto>> getVisits(@PathVariable("id") UUID id)
          throws ResourceNotFoundException {
    return ResponseEntity.ok(visitedService.getVisitedPubs(id));
  }
}
