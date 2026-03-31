package com.example.demo.common.exception;

import com.example.demo.common.api.ApiError;
import com.example.demo.common.api.ApiResponse;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ApiResponse<Void>> handleNotFound(ResourceNotFoundException ex) {
        ApiError error = new ApiError("NOT_FOUND", ex.getMessage(), null);
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(new ApiResponse<>(false, null, null, error));
    }

    @ExceptionHandler(UnauthorizedException.class)
    public ResponseEntity<ApiResponse<Void>> handleUnauthorized(UnauthorizedException ex) {
        ApiError error = new ApiError("UNAUTHORIZED", ex.getMessage(), null);
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(new ApiResponse<>(false, null, null, error));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<Void>> handleValidation(MethodArgumentNotValidException ex) {
        Map<String, String> details = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .collect(Collectors.toMap(
                        err -> err.getField(),
                        err -> err.getDefaultMessage() == null ? "Invalid value" : err.getDefaultMessage(),
                        (a, b) -> a
                ));
        ApiError error = new ApiError("VALIDATION_ERROR", "Invalid input data", details);
        return ResponseEntity.badRequest().body(new ApiResponse<>(false, null, null, error));
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiResponse<Void>> handleBadRequest(IllegalArgumentException ex) {
        ApiError error = new ApiError("BAD_REQUEST", ex.getMessage(), null);
        return ResponseEntity.badRequest().body(new ApiResponse<>(false, null, null, error));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Void>> handleGeneric(Exception ex) {
        ApiError error = new ApiError("INTERNAL_ERROR", "Unexpected server error", null);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ApiResponse<>(false, null, null, error));
    }
}
