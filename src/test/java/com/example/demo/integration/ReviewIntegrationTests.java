package com.example.demo.integration;

import com.example.demo.rating.repository.RatingRepository;
import com.example.demo.review.repository.ReviewRepository;
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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class ReviewIntegrationTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ReviewRepository reviewRepository;

    @Autowired
    private RatingRepository ratingRepository;

    @BeforeEach
    void cleanDatabase() {
        reviewRepository.deleteAll();
        ratingRepository.deleteAll();
        userRepository.deleteAll();
    }

    @Test
    void createThenList_shouldReturnReview() throws Exception {
        String token = tokenForUser("review1@example.com", "reviewuser1", "password123");

        mockMvc.perform(post("/api/movies/tt1375666/reviews")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json(Map.of(
                                "reviewText", "A brilliant puzzle-box movie with incredible visuals.",
                                "containsSpoilers", false
                        ))))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.movieId").value("tt1375666"));

        mockMvc.perform(get("/api/movies/tt1375666/reviews")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.length()").value(1));
    }

    @Test
    void updateOwnReview_shouldSucceed() throws Exception {
        String token = tokenForUser("review2@example.com", "reviewuser2", "password123");
        long reviewId = createReview(token, "tt0133093", "Great sci-fi classic.", false);

        mockMvc.perform(patch("/api/reviews/{id}", reviewId)
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json(Map.of(
                                "reviewText", "Great sci-fi classic with timeless action.",
                                "containsSpoilers", true
                        ))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.containsSpoilers").value(true))
                .andExpect(jsonPath("$.data.reviewText").value("Great sci-fi classic with timeless action."));
    }

    @Test
    void updateOtherUsersReview_shouldReturnUnauthorized() throws Exception {
        String ownerToken = tokenForUser("review3@example.com", "reviewuser3", "password123");
        String otherToken = tokenForUser("review4@example.com", "reviewuser4", "password123");
        long reviewId = createReview(ownerToken, "tt0111161", "Powerful and hopeful story.", false);

        mockMvc.perform(patch("/api/reviews/{id}", reviewId)
                        .header("Authorization", "Bearer " + otherToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json(Map.of(
                                "reviewText", "Trying to edit someone else's review.",
                                "containsSpoilers", false
                        ))))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.error.code").value("UNAUTHORIZED"));
    }

    @Test
    void deleteOwnReview_shouldSucceed() throws Exception {
        String token = tokenForUser("review5@example.com", "reviewuser5", "password123");
        long reviewId = createReview(token, "tt6751668", "Sharp social commentary.", false);

        mockMvc.perform(delete("/api/reviews/{id}", reviewId)
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isNoContent());

        mockMvc.perform(get("/api/movies/tt6751668/reviews")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.length()").value(0));
    }

    @Test
    void reviewEndpoints_withoutToken_shouldReturnForbidden() throws Exception {
        mockMvc.perform(post("/api/movies/tt1375666/reviews")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json(Map.of(
                                "reviewText", "No auth token included.",
                                "containsSpoilers", false
                        ))))
                .andExpect(status().isForbidden());
    }

    private long createReview(String token, String movieId, String text, boolean containsSpoilers) throws Exception {
        MvcResult result = mockMvc.perform(post("/api/movies/{id}/reviews", movieId)
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json(Map.of(
                                "reviewText", text,
                                "containsSpoilers", containsSpoilers
                        ))))
                .andExpect(status().isCreated())
                .andReturn();

        JsonNode root = objectMapper.readTree(result.getResponse().getContentAsString());
        return root.path("data").path("id").asLong();
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
