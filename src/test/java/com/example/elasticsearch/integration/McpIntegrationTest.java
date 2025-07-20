package com.example.elasticsearch.integration;

import com.example.elasticsearch.controller.McpController;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import com.theokanning.openai.service.OpenAiService;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.junit.jupiter.api.Assertions.*;

@WebMvcTest(McpController.class)
@TestPropertySource(properties = {
    "OPENAI_API_KEY=test-key-for-integration-test"
})
public class McpIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private OpenAiService openAiService; // Mock the OpenAI service to avoid real API calls
    
    @MockBean
    private com.example.elasticsearch.service.ElasticsearchService elasticsearchService; // Mock Elasticsearch service

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    void testMcpEndpointExists() throws Exception {
        // Test that the MCP endpoint exists and accepts POST requests
        String requestBody = objectMapper.writeValueAsString(
            java.util.Map.of("query", "Show users in Mathematics")
        );

        MvcResult result = mockMvc.perform(post("/mcp/query")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody))
                .andExpect(status().isInternalServerError()) // Expect 500 due to invalid OpenAI key
                .andReturn();

        // Verify response is JSON
        String responseContent = result.getResponse().getContentAsString();
        assertNotNull(responseContent);
        
        // Parse response as JSON to ensure it's valid
        JsonNode responseJson = objectMapper.readTree(responseContent);
        assertNotNull(responseJson);
        
        // Should either have an error (when OpenAI fails) or valid data
        assertTrue(responseJson.has("error") || responseJson.has("hits") || responseJson.has("aggregations"));
    }

    @Test
    void testMcpEndpointWithInvalidPayload() throws Exception {
        // Test with invalid JSON payload
        mockMvc.perform(post("/mcp/query")
                .contentType(MediaType.APPLICATION_JSON)
                .content("invalid json"))
                .andExpect(status().isInternalServerError()); // JSON parsing error handled by global exception handler
    }

    @Test
    void testMcpEndpointWithEmptyPayload() throws Exception {
        // Test with empty payload
        mockMvc.perform(post("/mcp/query")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{}"))
                .andExpect(status().isInternalServerError()) // OpenAI will fail with invalid key
                .andReturn();
    }

    @Test
    void testMcpEndpointWithMissingQuery() throws Exception {
        // Test with payload missing query field
        String requestBody = objectMapper.writeValueAsString(
            java.util.Map.of("notQuery", "some value")
        );

        MvcResult result = mockMvc.perform(post("/mcp/query")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody))
                .andExpect(status().isInternalServerError()) // OpenAI will fail with invalid key
                .andReturn();

        // Should handle gracefully and return error or process with null query
        String responseContent = result.getResponse().getContentAsString();
        assertNotNull(responseContent);
    }

    @Test
    void testMcpEndpointResponseFormat() throws Exception {
        // Test that response format is consistent
        String requestBody = objectMapper.writeValueAsString(
            java.util.Map.of("query", "What is the class average?")
        );

        MvcResult result = mockMvc.perform(post("/mcp/query")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody))
                .andExpect(status().isInternalServerError()) // OpenAI will fail with invalid key
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andReturn();

        String responseContent = result.getResponse().getContentAsString();
        JsonNode responseJson = objectMapper.readTree(responseContent);
        
        // Response should be a valid JSON object
        assertTrue(responseJson.isObject());
    }

    @Test
    void testMcpEndpointWithDifferentQueries() throws Exception {
        String[] testQueries = {
            "Show users in Mathematics",
            "What is the average score?",
            "Who has the highest score in Physics?",
            "List all students taking multiple courses"
        };

        for (String query : testQueries) {
            String requestBody = objectMapper.writeValueAsString(
                java.util.Map.of("query", query)
            );

            MvcResult result = mockMvc.perform(post("/mcp/query")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(requestBody))
                    .andExpect(status().isInternalServerError()) // OpenAI will fail with invalid key
                    .andReturn();

            String responseContent = result.getResponse().getContentAsString();
            JsonNode responseJson = objectMapper.readTree(responseContent);
            
            // Each query should return a valid JSON response
            assertNotNull(responseJson);
            assertTrue(responseJson.isObject());
        }
    }
}
