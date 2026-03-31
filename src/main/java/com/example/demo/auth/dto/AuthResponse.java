package com.example.demo.auth.dto;

import com.example.demo.user.dto.UserProfileDto;

public record AuthResponse(
        String accessToken,
        String tokenType,
        UserProfileDto user
) {
}
