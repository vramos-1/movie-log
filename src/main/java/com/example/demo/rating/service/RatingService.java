package com.example.demo.rating.service;

import com.example.demo.common.exception.ResourceNotFoundException;
import com.example.demo.rating.dto.RatingRequest;
import com.example.demo.rating.dto.RatingResponseDto;
import com.example.demo.rating.model.Rating;
import com.example.demo.rating.repository.RatingRepository;
import com.example.demo.user.model.User;
import com.example.demo.user.repository.UserRepository;
import java.util.Optional;
import org.springframework.stereotype.Service;

@Service
public class RatingService {

    private final RatingRepository ratingRepository;
    private final UserRepository userRepository;

    public RatingService(RatingRepository ratingRepository, UserRepository userRepository) {
        this.ratingRepository = ratingRepository;
        this.userRepository = userRepository;
    }

    public RatingResponseDto upsert(String movieId, String email, RatingRequest request) {
        Long userId = resolveUserId(email);

        Rating rating = ratingRepository.findByUserIdAndMovieId(userId, movieId)
                .orElseGet(() -> {
                    Rating created = new Rating();
                    created.setUserId(userId);
                    created.setMovieId(movieId);
                    return created;
                });

        rating.setScore(request.score());
        Rating saved = ratingRepository.save(rating);
        return new RatingResponseDto(saved.getMovieId(), saved.getUserId(), saved.getScore());
    }

    public RatingResponseDto getMyRating(String movieId, String email) {
        Long userId = resolveUserId(email);

        Optional<Rating> rating = ratingRepository.findByUserIdAndMovieId(userId, movieId);
        if (rating.isEmpty()) {
            return null;
        }
        Rating found = rating.get();
        return new RatingResponseDto(found.getMovieId(), found.getUserId(), found.getScore());
    }

    private Long resolveUserId(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found for current token"));
        return user.getId();
    }
}
