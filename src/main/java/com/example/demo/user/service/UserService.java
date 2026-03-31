package com.example.demo.user.service;

import com.example.demo.common.exception.ResourceNotFoundException;
import com.example.demo.user.dto.UserProfileDto;
import com.example.demo.user.model.User;
import com.example.demo.user.repository.UserRepository;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public UserProfileDto getCurrentUserProfile(String email) {
        User user = getCurrentUser(email);
        return toProfile(user);
    }

    public User getCurrentUser(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User profile not found"));
    }

    public UserProfileDto toProfile(User user) {
        return new UserProfileDto(user.getId(), user.getEmail(), user.getUsername());
    }
}
