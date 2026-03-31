package com.example.demo.review.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record ReviewUpdateRequest(
        @NotBlank @Size(min = 5, max = 1000)
        String reviewText,
        boolean containsSpoilers
) {
}
