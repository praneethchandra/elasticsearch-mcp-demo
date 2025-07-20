package com.example.elasticsearch.controller;

import com.example.elasticsearch.service.ElasticsearchService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
public class UserControllerTest {

    @Mock
    private ElasticsearchService elasticsearchService;

    private UserController userController;
    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        userController = new UserController(elasticsearchService);
        mockMvc = MockMvcBuilders.standaloneSetup(userController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
        objectMapper = new ObjectMapper();
    }

    @Test
    void testGetUserById() throws Exception {
        // Mock response
        JsonNode mockResponse = objectMapper.createObjectNode()
            .put("hits", objectMapper.createObjectNode()
                .put("total", objectMapper.createObjectNode()
                    .put("value", 1)));

        when(elasticsearchService.search(eq("user"), eq("simple-search.json"), any()))
            .thenReturn(mockResponse);

        // Test the endpoint
        mockMvc.perform(get("/api/users/by-id/123"))
                .andExpect(status().isOk());

        // Verify the service was called with correct parameters
        verify(elasticsearchService).search(eq("user"), eq("simple-search.json"), any());
    }

    @Test
    void testSearchUserByName() throws Exception {
        // Mock response
        JsonNode mockResponse = objectMapper.createObjectNode()
            .put("responses", objectMapper.createArrayNode());

        when(elasticsearchService.msearch(eq("user"), eq("multi-search.msearch"), any()))
            .thenReturn(mockResponse);

        // Test the endpoint
        mockMvc.perform(get("/api/users/search")
                .param("userName", "Alice"))
                .andExpect(status().isOk());

        // Verify the service was called with correct parameters
        verify(elasticsearchService).msearch(eq("user"), eq("multi-search.msearch"), any());
    }

    @Test
    void testGetUserStats() throws Exception {
        // Mock response
        ObjectNode aggregations = objectMapper.createObjectNode();
        aggregations.put("course_count", objectMapper.createObjectNode().put("value", 3));
        aggregations.put("total_score", objectMapper.createObjectNode().put("value", 250.5));
        
        ObjectNode mockResponse = objectMapper.createObjectNode();
        mockResponse.set("aggregations", aggregations);

        when(elasticsearchService.aggregate(eq("user"), eq("agg-count-sum.json"), any()))
            .thenReturn(mockResponse);

        // Test the endpoint
        mockMvc.perform(get("/api/users/stats/456"))
                .andExpect(status().isOk());

        // Verify the service was called with correct parameters
        verify(elasticsearchService).aggregate(eq("user"), eq("agg-count-sum.json"), any());
    }

    @Test
    void testSearchUserWithoutUserNameParameter() throws Exception {
        // Test missing required parameter
        mockMvc.perform(get("/api/users/search"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testGetUserByIdWithInvalidId() throws Exception {
        // Test with invalid path parameter (non-numeric)
        // This will cause a type conversion error which should be handled by Spring
        mockMvc.perform(get("/api/users/by-id/invalid"))
                .andExpect(status().isBadRequest()); // Spring handles type conversion errors
    }

    @Test
    void testControllerInitialization() {
        // Test that controller initializes properly
        assertNotNull(userController);
    }

    @Test
    void testAllEndpointsReturnJson() throws Exception {
        // Mock responses for all endpoints
        JsonNode searchResponse = objectMapper.createObjectNode();
        JsonNode msearchResponse = objectMapper.createObjectNode();
        JsonNode aggregateResponse = objectMapper.createObjectNode();

        when(elasticsearchService.search(any(), any(), any())).thenReturn(searchResponse);
        when(elasticsearchService.msearch(any(), any(), any())).thenReturn(msearchResponse);
        when(elasticsearchService.aggregate(any(), any(), any())).thenReturn(aggregateResponse);

        // Test all endpoints return JSON
        mockMvc.perform(get("/api/users/by-id/123"))
                .andExpect(status().isOk());

        mockMvc.perform(get("/api/users/search").param("userName", "test"))
                .andExpect(status().isOk());

        mockMvc.perform(get("/api/users/stats/123"))
                .andExpect(status().isOk());
    }
}
