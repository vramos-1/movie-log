package com.example.demo.movie.dto;

public record MovieDetailsDto(
        String id,
        String title,
        Integer releaseYear,
        String synopsis,
        String posterUrl
) {
}
