package com.example.demo.review.controller;

import com.example.demo.common.api.ApiResponse;
import com.example.demo.review.dto.ReviewCreateRequest;
import com.example.demo.review.dto.ReviewResponseDto;
import com.example.demo.review.dto.ReviewUpdateRequest;
import com.example.demo.review.service.ReviewService;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class ReviewController {

    private final ReviewService reviewService;

    public ReviewController(ReviewService reviewService) {
        this.reviewService = reviewService;
    }

    @PostMapping("/movies/{id}/reviews")
    public ResponseEntity<ApiResponse<ReviewResponseDto>> create(
            @PathVariable String id,
            @Valid @RequestBody ReviewCreateRequest request,
            Authentication authentication) {
        String email = extractEmail(authentication);
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(new ApiResponse<>(true, reviewService.create(id, email, request), null, null));
    }

    @GetMapping("/movies/{id}/reviews")
    public ResponseEntity<ApiResponse<List<ReviewResponseDto>>> list(@PathVariable String id) {
        return ResponseEntity.ok(new ApiResponse<>(true, reviewService.listByMovie(id), null, null));
    }

    @PatchMapping("/reviews/{reviewId}")
    public ResponseEntity<ApiResponse<ReviewResponseDto>> update(
            @PathVariable Long reviewId,
            @Valid @RequestBody ReviewUpdateRequest request,
            Authentication authentication) {
        String email = extractEmail(authentication);
        return ResponseEntity.ok(new ApiResponse<>(true, reviewService.update(reviewId, email, request), null, null));
    }

    @DeleteMapping("/reviews/{reviewId}")
    public ResponseEntity<Void> delete(@PathVariable Long reviewId, Authentication authentication) {
        String email = extractEmail(authentication);
        reviewService.delete(reviewId, email);
        return ResponseEntity.noContent().build();
    }

    private String extractEmail(Authentication authentication) {
        Object principal = authentication.getPrincipal();
        if (principal instanceof UserDetails userDetails) {
            return userDetails.getUsername();
        }
        return authentication.getName();
    }
}
