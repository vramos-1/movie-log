package com.example.demo.movie.dto;

public record MovieSearchResponseDto(
        String id,
        String title,
        Integer releaseYear,
        String posterUrl
) {
}
