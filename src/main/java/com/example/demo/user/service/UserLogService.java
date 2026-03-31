package com.example.demo.user.service;

import com.example.demo.common.api.Meta;
import com.example.demo.movie.dto.MovieDetailsDto;
import com.example.demo.movie.service.MovieService;
import com.example.demo.rating.model.Rating;
import com.example.demo.rating.repository.RatingRepository;
import com.example.demo.review.model.Review;
import com.example.demo.review.repository.ReviewRepository;
import com.example.demo.user.dto.MovieLogEntryDto;
import com.example.demo.user.model.User;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.springframework.stereotype.Service;

@Service
public class UserLogService {

    private final UserService userService;
    private final RatingRepository ratingRepository;
    private final ReviewRepository reviewRepository;
    private final MovieService movieService;

    public UserLogService(
            UserService userService,
            RatingRepository ratingRepository,
            ReviewRepository reviewRepository,
            MovieService movieService
    ) {
        this.userService = userService;
        this.ratingRepository = ratingRepository;
        this.reviewRepository = reviewRepository;
        this.movieService = movieService;
    }

    public UserLogResult getLog(String email, String sort, int page, int limit) {
        User user = userService.getCurrentUser(email);
        Map<String, Aggregate> aggregates = new LinkedHashMap<>();

        for (Rating rating : ratingRepository.findByUserIdOrderByUpdatedAtDesc(user.getId())) {
            Aggregate aggregate = aggregates.computeIfAbsent(rating.getMovieId(), Aggregate::new);
            aggregate.rating = rating.getScore();
            aggregate.latestActivityAt = maxInstant(aggregate.latestActivityAt, rating.getUpdatedAt());
        }

        for (Review review : reviewRepository.findByUserIdOrderByUpdatedAtDesc(user.getId())) {
            Aggregate aggregate = aggregates.computeIfAbsent(review.getMovieId(), Aggregate::new);
            if (aggregate.reviewId == null) {
                aggregate.reviewId = review.getId();
                aggregate.reviewText = review.getReviewText();
                aggregate.containsSpoilers = review.isContainsSpoilers();
            }
            aggregate.latestActivityAt = maxInstant(aggregate.latestActivityAt, review.getUpdatedAt());
        }

        List<MovieLogEntryDto> entries = new ArrayList<>();
        for (Aggregate aggregate : aggregates.values()) {
            MovieDetailsDto movie = movieService.getById(aggregate.movieId);
            entries.add(new MovieLogEntryDto(
                    aggregate.movieId,
                    movie.title(),
                    movie.releaseYear(),
                    movie.posterUrl(),
                    aggregate.rating,
                    aggregate.reviewId,
                    aggregate.reviewText,
                    aggregate.containsSpoilers,
                    aggregate.latestActivityAt
            ));
        }

        Comparator<MovieLogEntryDto> comparator = switch (sort == null ? "recent" : sort) {
            case "title" -> Comparator.comparing(MovieLogEntryDto::title, String.CASE_INSENSITIVE_ORDER);
            default -> Comparator.comparing(MovieLogEntryDto::latestActivityAt, Comparator.nullsLast(Comparator.reverseOrder()));
        };
        entries = entries.stream().sorted(comparator).toList();

        int safePage = Math.max(page, 1);
        int safeLimit = Math.max(limit, 1);
        int fromIndex = Math.min((safePage - 1) * safeLimit, entries.size());
        int toIndex = Math.min(fromIndex + safeLimit, entries.size());
        List<MovieLogEntryDto> paged = entries.subList(fromIndex, toIndex);

        return new UserLogResult(paged, new Meta(safePage, safeLimit, entries.size()));
    }

    private Instant maxInstant(Instant left, Instant right) {
        if (left == null) {
            return right;
        }
        if (right == null) {
            return left;
        }
        return left.isAfter(right) ? left : right;
    }

    public record UserLogResult(List<MovieLogEntryDto> entries, Meta meta) {
    }

    private static final class Aggregate {
        private final String movieId;
        private Integer rating;
        private Long reviewId;
        private String reviewText;
        private boolean containsSpoilers;
        private Instant latestActivityAt;

        private Aggregate(String movieId) {
            this.movieId = movieId;
        }
    }
}
