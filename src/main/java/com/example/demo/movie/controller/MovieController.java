package com.example.demo.movie.controller;

import com.example.demo.common.api.ApiResponse;
import com.example.demo.movie.dto.MovieDetailsDto;
import com.example.demo.movie.dto.MovieSearchResponseDto;
import com.example.demo.movie.service.MovieService;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/movies")
public class MovieController {

    private final MovieService movieService;

    public MovieController(MovieService movieService) {
        this.movieService = movieService;
    }

    @GetMapping("/search")
    public ResponseEntity<ApiResponse<List<MovieSearchResponseDto>>> search(@RequestParam String query) {
        return ResponseEntity.ok(new ApiResponse<>(true, movieService.search(query.trim()), null, null));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<MovieDetailsDto>> getMovie(@PathVariable String id) {
        return ResponseEntity.ok(new ApiResponse<>(true, movieService.getById(id), null, null));
    }
}
