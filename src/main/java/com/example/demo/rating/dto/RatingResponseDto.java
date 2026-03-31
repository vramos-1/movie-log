package com.example.demo.rating.dto;

public record RatingResponseDto(
        String movieId,
        Long userId,
        Integer score
) {
}
