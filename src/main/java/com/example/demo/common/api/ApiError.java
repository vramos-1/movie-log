package com.example.demo.common.api;

public record ApiError(
        String code,
        String message,
        Object details
) {
}
