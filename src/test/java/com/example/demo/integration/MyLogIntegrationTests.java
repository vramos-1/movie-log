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
class MyLogIntegrationTests {

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
    void log_shouldReturnCombinedRatingsAndReviews() throws Exception {
        String token = tokenForUser("log1@example.com", "loguser1", "password123");
        rate(token, "tt1375666", 5);
        review(token, "tt1375666", "Inventive and emotionally satisfying.", false);
        rate(token, "tt0133093", 4);

        mockMvc.perform(get("/api/me/log")
                        .header("Authorization", "Bearer " + token)
                        .param("sort", "recent")
                        .param("page", "1")
                        .param("limit", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.meta.page").value(1))
                .andExpect(jsonPath("$.meta.total").value(2))
                .andExpect(jsonPath("$.data.length()").value(2));
    }

    @Test
    void log_sortedByTitle_shouldReturnAlphabeticalOrder() throws Exception {
        String token = tokenForUser("log2@example.com", "loguser2", "password123");
        rate(token, "tt0133093", 4);
        rate(token, "tt1375666", 5);

        mockMvc.perform(get("/api/me/log")
                        .header("Authorization", "Bearer " + token)
                        .param("sort", "title"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data[0].title").value("Inception"))
                .andExpect(jsonPath("$.data[1].title").value("The Matrix"));
    }

    @Test
    void log_withPagination_shouldReturnRequestedPage() throws Exception {
        String token = tokenForUser("log3@example.com", "loguser3", "password123");
        rate(token, "tt1375666", 5);
        rate(token, "tt0133093", 4);
        rate(token, "tt0111161", 3);

        mockMvc.perform(get("/api/me/log")
                        .header("Authorization", "Bearer " + token)
                        .param("page", "2")
                        .param("limit", "2"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.meta.page").value(2))
                .andExpect(jsonPath("$.meta.limit").value(2))
                .andExpect(jsonPath("$.data.length()").value(1));
    }

    @Test
    void log_withoutToken_shouldReturnForbidden() throws Exception {
        mockMvc.perform(get("/api/me/log"))
                .andExpect(status().isForbidden());
    }

    private void rate(String token, String movieId, int score) throws Exception {
        mockMvc.perform(put("/api/movies/{id}/rating", movieId)
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json(Map.of("score", score))))
                .andExpect(status().isOk());
    }

    private void review(String token, String movieId, String text, boolean spoilers) throws Exception {
        mockMvc.perform(post("/api/movies/{id}/reviews", movieId)
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json(Map.of(
                                "reviewText", text,
                                "containsSpoilers", spoilers
                        ))))
                .andExpect(status().isCreated());
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
