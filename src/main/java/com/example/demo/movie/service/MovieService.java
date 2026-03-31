package com.example.demo.movie.service;

import com.example.demo.common.exception.ResourceNotFoundException;
import com.example.demo.movie.dto.MovieDetailsDto;
import com.example.demo.movie.dto.MovieSearchResponseDto;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import org.springframework.stereotype.Service;

@Service
public class MovieService {

    private final Map<String, MovieDetailsDto> catalog;

    public MovieService() {
        this.catalog = seedCatalog();
    }

    public List<MovieSearchResponseDto> search(String query) {
        String normalized = normalizeQuery(query);

        return catalog.values().stream()
                .filter(movie -> movie.title().toLowerCase(Locale.ROOT).contains(normalized))
                .map(movie -> new MovieSearchResponseDto(
                        movie.id(),
                        movie.title(),
                        movie.releaseYear(),
                        movie.posterUrl()
                ))
                .toList();
    }

    public MovieDetailsDto getById(String id) {
        MovieDetailsDto movie = catalog.get(id);
        if (movie == null) {
            throw new ResourceNotFoundException("Movie not found for id: " + id);
        }
        return movie;
    }

    private String normalizeQuery(String query) {
        if (query == null) {
            throw new IllegalArgumentException("Query is required");
        }
        String normalized = query.trim().toLowerCase(Locale.ROOT);
        if (normalized.length() < 2) {
            throw new IllegalArgumentException("Query must be at least 2 characters");
        }
        return normalized;
    }

    private Map<String, MovieDetailsDto> seedCatalog() {
        Map<String, MovieDetailsDto> data = new LinkedHashMap<>();
        data.put("tt1375666", new MovieDetailsDto(
                "tt1375666",
                "Inception",
                2010,
                "A skilled thief enters dreams to extract and plant ideas in high-stakes missions.",
                "https://image.tmdb.org/t/p/w500/8IB2e4r4oVhHnANbnm7O3Tj6tF8.jpg"
        ));
        data.put("tt0111161", new MovieDetailsDto(
                "tt0111161",
                "The Shawshank Redemption",
                1994,
                "Two imprisoned men form a lasting friendship while finding hope behind prison walls.",
                "https://image.tmdb.org/t/p/w500/q6y0Go1tsGEsmtFryDOJo3dEmqu.jpg"
        ));
        data.put("tt0133093", new MovieDetailsDto(
                "tt0133093",
                "The Matrix",
                1999,
                "A hacker discovers reality is a simulation and joins a rebellion against machine rule.",
                "https://image.tmdb.org/t/p/w500/f89U3ADr1oiB1s9GkdPOEpXUk5H.jpg"
        ));
        data.put("tt6751668", new MovieDetailsDto(
                "tt6751668",
                "Parasite",
                2019,
                "A low-income family infiltrates a wealthy household in a suspenseful social satire.",
                "https://image.tmdb.org/t/p/w500/7IiTTgloJzvGI1TAYymCfbfl3vT.jpg"
        ));
        return data;
    }
}
