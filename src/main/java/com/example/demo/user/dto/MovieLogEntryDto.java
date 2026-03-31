package com.example.demo.user.dto;

import java.time.Instant;

public record MovieLogEntryDto(
        String movieId,
        String title,
        Integer releaseYear,
        String posterUrl,
        Integer rating,
        Long reviewId,
        String reviewText,
        boolean containsSpoilers,
        Instant latestActivityAt
) {
}
