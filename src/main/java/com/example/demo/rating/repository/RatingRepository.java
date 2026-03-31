package com.example.demo.rating.repository;

import com.example.demo.rating.model.Rating;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RatingRepository extends JpaRepository<Rating, Long> {

    Optional<Rating> findByUserIdAndMovieId(Long userId, String movieId);

    java.util.List<Rating> findByUserIdOrderByUpdatedAtDesc(Long userId);
}
