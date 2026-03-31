package com.example.demo.common.api;

public record ApiResponse<T>(
        boolean success,
        T data,
        Meta meta,
        ApiError error
) {
}
