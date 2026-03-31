package com.example.demo.review.service;

import com.example.demo.common.exception.ResourceNotFoundException;
import com.example.demo.common.exception.UnauthorizedException;
import com.example.demo.review.dto.ReviewCreateRequest;
import com.example.demo.review.dto.ReviewResponseDto;
import com.example.demo.review.dto.ReviewUpdateRequest;
import com.example.demo.review.model.Review;
import com.example.demo.review.repository.ReviewRepository;
import java.util.List;
import com.example.demo.user.model.User;
import com.example.demo.user.repository.UserRepository;
import org.springframework.stereotype.Service;

@Service
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final UserRepository userRepository;

    public ReviewService(ReviewRepository reviewRepository, UserRepository userRepository) {
        this.reviewRepository = reviewRepository;
        this.userRepository = userRepository;
    }

    public ReviewResponseDto create(String movieId, String email, ReviewCreateRequest request) {
        Long userId = resolveUserId(email);

        Review review = new Review();
        review.setMovieId(movieId);
        review.setUserId(userId);
        review.setReviewText(request.reviewText());
        review.setContainsSpoilers(request.containsSpoilers());

        return toDto(reviewRepository.save(review));
    }

    public List<ReviewResponseDto> listByMovie(String movieId) {
        return reviewRepository.findByMovieIdOrderByUpdatedAtDesc(movieId)
                .stream()
                .map(this::toDto)
                .toList();
    }

    public ReviewResponseDto update(Long reviewId, String email, ReviewUpdateRequest request) {
        Long userId = resolveUserId(email);
        Review review = reviewRepository.findByIdAndUserId(reviewId, userId)
                .orElseThrow(() -> reviewRepository.findById(reviewId).isPresent()
                        ? new UnauthorizedException("You can only edit your own reviews")
                        : new ResourceNotFoundException("Review not found"));

        review.setReviewText(request.reviewText());
        review.setContainsSpoilers(request.containsSpoilers());
        return toDto(reviewRepository.save(review));
    }

    public void delete(Long reviewId, String email) {
        Long userId = resolveUserId(email);
        Review review = reviewRepository.findByIdAndUserId(reviewId, userId)
                .orElseThrow(() -> reviewRepository.findById(reviewId).isPresent()
                        ? new UnauthorizedException("You can only delete your own reviews")
                        : new ResourceNotFoundException("Review not found"));
        reviewRepository.delete(review);
    }

    private Long resolveUserId(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found for current token"));
        return user.getId();
    }

    private ReviewResponseDto toDto(Review review) {
        return new ReviewResponseDto(
                review.getId(),
                review.getMovieId(),
                review.getUserId(),
                review.getReviewText(),
                review.isContainsSpoilers()
        );
    }
}
