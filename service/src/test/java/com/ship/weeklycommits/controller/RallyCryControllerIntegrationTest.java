package com.ship.weeklycommits.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Testcontainers
class RallyCryControllerIntegrationTest {

    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16-alpine");

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
    }

    @Autowired
    private MockMvc mockMvc;

    @Test
    void createAndListRallyCries() throws Exception {
        // Create a rally cry
        mockMvc.perform(post("/api/v1/rally-cries")
                        .header("X-User-Id", "user-1")
                        .header("X-Org-Id", "org-1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"title": "Q1 Growth", "description": "Focus on growth"}
                                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.title").value("Q1 Growth"))
                .andExpect(jsonPath("$.orgId").value("org-1"))
                .andExpect(jsonPath("$.active").value(true));

        // List rally cries
        mockMvc.perform(get("/api/v1/rally-cries")
                        .header("X-User-Id", "user-1")
                        .header("X-Org-Id", "org-1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].title").value("Q1 Growth"))
                .andExpect(jsonPath("$[0].orgId").value("org-1"));
    }

    @Test
    void rejectsRequestWithoutAuthHeaders() throws Exception {
        mockMvc.perform(get("/api/v1/rally-cries"))
                .andExpect(status().isUnauthorized());
    }
}
