package com.ship.weeklycommits.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Testcontainers
class WeeklyCommitControllerIntegrationTest {

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

    @Autowired
    private ObjectMapper objectMapper;

    private String outcomeId;

    @BeforeEach
    void setUp() throws Exception {
        // Create RallyCry
        MvcResult rcResult = mockMvc.perform(post("/api/v1/rally-cries")
                        .header("X-User-Id", "user-1")
                        .header("X-Org-Id", "org-1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"title": "Q1 Growth", "description": "Focus on growth"}
                                """))
                .andExpect(status().isCreated())
                .andReturn();

        JsonNode rcNode = objectMapper.readTree(rcResult.getResponse().getContentAsString());
        String rallyCryId = rcNode.get("id").asText();

        // Create DefiningObjective
        MvcResult doResult = mockMvc.perform(post("/api/v1/rally-cries/" + rallyCryId + "/objectives")
                        .header("X-User-Id", "user-1")
                        .header("X-Org-Id", "org-1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"title": "Increase Revenue", "description": "Revenue objective"}
                                """))
                .andExpect(status().isCreated())
                .andReturn();

        JsonNode doNode = objectMapper.readTree(doResult.getResponse().getContentAsString());
        String objectiveId = doNode.get("id").asText();

        // Create Outcome
        MvcResult outcomeResult = mockMvc.perform(post("/api/v1/objectives/" + objectiveId + "/outcomes")
                        .header("X-User-Id", "user-1")
                        .header("X-Org-Id", "org-1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"title": "Close 5 deals", "description": "Sales target", "measurableTarget": "5", "currentValue": "0"}
                                """))
                .andExpect(status().isCreated())
                .andReturn();

        JsonNode outcomeNode = objectMapper.readTree(outcomeResult.getResponse().getContentAsString());
        outcomeId = outcomeNode.get("id").asText();
    }

    @Test
    void fullLifecycle_getCurrentWeek_addItem() throws Exception {
        // GET /current returns DRAFT
        mockMvc.perform(get("/api/v1/weekly-commits/current")
                        .header("X-User-Id", "user-1")
                        .header("X-Org-Id", "org-1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("DRAFT"))
                .andExpect(jsonPath("$.userId").value("user-1"))
                .andExpect(jsonPath("$.orgId").value("org-1"))
                .andExpect(jsonPath("$.items").isArray());

        // POST item with outcomeId returns 201
        mockMvc.perform(post("/api/v1/weekly-commits/current")
                        .header("X-User-Id", "user-1")
                        .header("X-Org-Id", "org-1"))
                .andExpect(status().isOk());

        // Get the weekStartDate from the current response
        MvcResult currentResult = mockMvc.perform(get("/api/v1/weekly-commits/current")
                        .header("X-User-Id", "user-1")
                        .header("X-Org-Id", "org-1"))
                .andExpect(status().isOk())
                .andReturn();

        JsonNode currentNode = objectMapper.readTree(currentResult.getResponse().getContentAsString());
        String weekStart = currentNode.get("weekStartDate").asText();

        mockMvc.perform(post("/api/v1/weekly-commits/" + weekStart + "/items")
                        .header("X-User-Id", "user-1")
                        .header("X-Org-Id", "org-1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "title": "Ship weekly commits feature",
                                    "description": "Build the weekly commits MVP",
                                    "outcomeId": "%s",
                                    "urgency": "HIGH",
                                    "importance": "HIGH",
                                    "sortOrder": 1
                                }
                                """.formatted(outcomeId)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.title").value("Ship weekly commits feature"))
                .andExpect(jsonPath("$.outcomeId").value(outcomeId));
    }

    @Test
    void addItem_rejectsWithoutOutcome() throws Exception {
        // Get current week to establish a weekStart
        MvcResult currentResult = mockMvc.perform(get("/api/v1/weekly-commits/current")
                        .header("X-User-Id", "user-2")
                        .header("X-Org-Id", "org-1"))
                .andExpect(status().isOk())
                .andReturn();

        JsonNode currentNode = objectMapper.readTree(currentResult.getResponse().getContentAsString());
        String weekStart = currentNode.get("weekStartDate").asText();

        UUID fakeOutcomeId = UUID.randomUUID();

        mockMvc.perform(post("/api/v1/weekly-commits/" + weekStart + "/items")
                        .header("X-User-Id", "user-2")
                        .header("X-Org-Id", "org-1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "title": "Some task",
                                    "description": "A task",
                                    "outcomeId": "%s",
                                    "urgency": "HIGH",
                                    "importance": "LOW",
                                    "sortOrder": 1
                                }
                                """.formatted(fakeOutcomeId)))
                .andExpect(status().isBadRequest());
    }
}
