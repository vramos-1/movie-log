package com.example.demo.user.controller;

import com.example.demo.common.api.ApiResponse;
import com.example.demo.user.dto.MovieLogEntryDto;
import com.example.demo.user.service.UserLogService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/me")
public class UserLogController {

    private final UserLogService userLogService;

    public UserLogController(UserLogService userLogService) {
        this.userLogService = userLogService;
    }

    @GetMapping("/log")
    public ResponseEntity<ApiResponse<List<MovieLogEntryDto>>> log(
            Authentication authentication,
            @RequestParam(defaultValue = "recent") String sort,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int limit) {
        String email = extractEmail(authentication);
        UserLogService.UserLogResult result = userLogService.getLog(email, sort, page, limit);
        return ResponseEntity.ok(new ApiResponse<>(true, result.entries(), result.meta(), null));
    }

    private String extractEmail(Authentication authentication) {
        Object principal = authentication.getPrincipal();
        if (principal instanceof UserDetails userDetails) {
            return userDetails.getUsername();
        }
        return authentication.getName();
    }
}
