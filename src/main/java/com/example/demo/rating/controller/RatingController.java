package com.example.demo.rating.controller;

import com.example.demo.common.api.ApiResponse;
import com.example.demo.rating.dto.RatingRequest;
import com.example.demo.rating.dto.RatingResponseDto;
import com.example.demo.rating.service.RatingService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/movies/{id}")
public class RatingController {

    private final RatingService ratingService;

    public RatingController(RatingService ratingService) {
        this.ratingService = ratingService;
    }

    @PutMapping("/rating")
    public ResponseEntity<ApiResponse<RatingResponseDto>> upsert(
            @PathVariable String id,
            @Valid @RequestBody RatingRequest request,
            Authentication authentication) {
        String email = extractEmail(authentication);
        return ResponseEntity.ok(new ApiResponse<>(true, ratingService.upsert(id, email, request), null, null));
    }

    @GetMapping("/rating/me")
    public ResponseEntity<ApiResponse<RatingResponseDto>> getMine(
            @PathVariable String id,
            Authentication authentication) {
        String email = extractEmail(authentication);
        return ResponseEntity.ok(new ApiResponse<>(true, ratingService.getMyRating(id, email), null, null));
    }

    private String extractEmail(Authentication authentication) {
        Object principal = authentication.getPrincipal();
        if (principal instanceof UserDetails userDetails) {
            return userDetails.getUsername();
        }
        return authentication.getName();
    }
}
