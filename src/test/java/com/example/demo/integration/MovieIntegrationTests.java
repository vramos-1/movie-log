package com.example.demo.integration;

import com.example.demo.user.repository.UserRepository;
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
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class MovieIntegrationTests {

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
    void search_withValidQueryAndToken_shouldReturnResults() throws Exception {
        String token = tokenForUser("moviesearch@example.com", "movieuser", "password123");

        mockMvc.perform(get("/api/movies/search")
                        .header("Authorization", "Bearer " + token)
                        .param("query", "in")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.length()").value(1))
                .andExpect(jsonPath("$.data[0].id").value("tt1375666"))
                .andExpect(jsonPath("$.data[0].title").value("Inception"));
    }

    @Test
    void search_withShortQuery_shouldReturnBadRequest() throws Exception {
        String token = tokenForUser("shortquery@example.com", "shortquery", "password123");

        mockMvc.perform(get("/api/movies/search")
                        .header("Authorization", "Bearer " + token)
                        .param("query", "a")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.error.code").value("BAD_REQUEST"));
    }

    @Test
    void details_withKnownMovieId_shouldReturnMovie() throws Exception {
        String token = tokenForUser("details@example.com", "detailuser", "password123");

        mockMvc.perform(get("/api/movies/tt0133093")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.id").value("tt0133093"))
                .andExpect(jsonPath("$.data.title").value("The Matrix"));
    }

    @Test
    void details_withUnknownMovieId_shouldReturnNotFound() throws Exception {
        String token = tokenForUser("missing@example.com", "missinguser", "password123");

        mockMvc.perform(get("/api/movies/tt0000000")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.error.code").value("NOT_FOUND"));
    }

    @Test
    void search_withoutToken_shouldReturnForbidden() throws Exception {
        mockMvc.perform(get("/api/movies/search")
                        .param("query", "in"))
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

        String token = objectMapper.readTree(result.getResponse().getContentAsString())
                .path("data")
                .path("accessToken")
                .asText();
        assertThat(token).isNotBlank();
        return token;
    }

    private String json(Object value) throws Exception {
        return objectMapper.writeValueAsString(value);
    }
}
