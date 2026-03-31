package com.example.demo.integration;

import com.example.demo.user.repository.UserRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class RatingIntegrationTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    @BeforeEach
    void cleanDatabase() {
        userRepository.deleteAll();
    }

    @Test
    void upsert_newRating_shouldPersistScore() throws Exception {
        String token = tokenForUser("rating1@example.com", "ratinguser1", "password123");

        mockMvc.perform(put("/api/movies/tt1375666/rating")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json(Map.of("score", 5))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.movieId").value("tt1375666"))
                .andExpect(jsonPath("$.data.score").value(5));
    }

    @Test
    void upsert_existingRating_shouldUpdateScore() throws Exception {
        String token = tokenForUser("rating2@example.com", "ratinguser2", "password123");

        mockMvc.perform(put("/api/movies/tt1375666/rating")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json(Map.of("score", 2))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.score").value(2));

        mockMvc.perform(put("/api/movies/tt1375666/rating")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json(Map.of("score", 4))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.score").value(4));

        mockMvc.perform(get("/api/movies/tt1375666/rating/me")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.score").value(4));
    }

    @Test
    void getMine_whenNoRating_shouldReturnNullData() throws Exception {
        String token = tokenForUser("rating3@example.com", "ratinguser3", "password123");

        mockMvc.perform(get("/api/movies/tt0111161/rating/me")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data").isEmpty());
    }

    @Test
    void upsert_withInvalidScore_shouldReturnBadRequest() throws Exception {
        String token = tokenForUser("rating4@example.com", "ratinguser4", "password123");

        mockMvc.perform(put("/api/movies/tt0133093/rating")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json(Map.of("score", 0))))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.error.code").value("VALIDATION_ERROR"));
    }

    @Test
    void ratingEndpoints_withoutToken_shouldReturnForbidden() throws Exception {
        mockMvc.perform(put("/api/movies/tt1375666/rating")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json(Map.of("score", 3))))
                .andExpect(status().isForbidden());

        mockMvc.perform(get("/api/movies/tt1375666/rating/me"))
                .andExpect(status().isForbidden());
    }

    private String tokenForUser(String email, String username, String password) throws Exception {
        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json(Map.of(
                                "email", email,
                                "username", username,
                                "password", password
                        ))))
                .andExpect(status().isCreated());

        MvcResult result = mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json(Map.of(
                                "email", email,
                                "password", password
                        ))))
                .andExpect(status().isOk())
                .andReturn();

        JsonNode root = objectMapper.readTree(result.getResponse().getContentAsString());
        String token = root.path("data").path("accessToken").asText();
        assertThat(token).isNotBlank();
        return token;
    }

    private String json(Object value) throws Exception {
        return objectMapper.writeValueAsString(value);
    }
}
