package com.example.demo.movie.repository;

import com.example.demo.movie.model.Movie;
import java.util.Optional;

public interface MovieRepository {

    Optional<Movie> findByExternalMovieId(String externalMovieId);
}
