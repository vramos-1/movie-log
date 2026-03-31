package com.example.demo.review.repository;

import com.example.demo.review.model.Review;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Long> {

    List<Review> findByMovieIdOrderByUpdatedAtDesc(String movieId);

    List<Review> findByUserIdOrderByUpdatedAtDesc(Long userId);

    Optional<Review> findByIdAndUserId(Long id, Long userId);
}
