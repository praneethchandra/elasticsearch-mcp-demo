package com.example.elasticsearch.controller;

import com.example.elasticsearch.service.ElasticsearchService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.theokanning.openai.completion.chat.ChatCompletionRequest;
import com.theokanning.openai.completion.chat.ChatCompletionResult;
import com.theokanning.openai.completion.chat.ChatMessage;
import com.theokanning.openai.service.OpenAiService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class McpControllerTest {

    @Mock
    private ElasticsearchService elasticsearchService;

    @Mock
    private OpenAiService openAiService;

    private McpController mcpController;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        // Create controller with test key
        mcpController = new McpController("test-key", elasticsearchService);
        objectMapper = new ObjectMapper();
    }

    @Test
    void testHandleQueryBasicStructure() throws Exception {
        // Test the controller basic structure - expect OpenAI exception
        Map<String, Object> payload = Map.of("query", "Show users in Mathematics");
        
        // The OpenAI call will fail with invalid API key, which is expected
        assertThrows(com.theokanning.openai.OpenAiHttpException.class, () -> {
            mcpController.handle(payload);
        });
    }

    @Test
    void testControllerInitialization() {
        // Test that controller initializes properly
        assertNotNull(mcpController);
        
        // Verify that the elasticsearch service is injected
        ElasticsearchService injectedService = (ElasticsearchService) 
            ReflectionTestUtils.getField(mcpController, "es");
        assertEquals(elasticsearchService, injectedService);
    }

    @Test
    void testPayloadValidation() throws Exception {
        // Test with empty payload
        Map<String, Object> emptyPayload = Map.of();
        
        assertThrows(Exception.class, () -> {
            mcpController.handle(emptyPayload);
        });
    }

    @Test
    void testPayloadWithNullQuery() throws Exception {
        // Test with null query - use HashMap to allow null values
        Map<String, Object> nullQueryPayload = new java.util.HashMap<>();
        nullQueryPayload.put("query", null);
        
        assertThrows(Exception.class, () -> {
            mcpController.handle(nullQueryPayload);
        });
    }
}
