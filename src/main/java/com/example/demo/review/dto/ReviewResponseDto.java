package com.example.demo.review.dto;

public record ReviewResponseDto(
        Long id,
        String movieId,
        Long userId,
        String reviewText,
        boolean containsSpoilers
) {
}
