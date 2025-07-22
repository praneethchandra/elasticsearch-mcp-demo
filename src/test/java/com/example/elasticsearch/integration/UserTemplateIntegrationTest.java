package com.example.elasticsearch.integration;

import com.example.elasticsearch.query.UserTemplateParams;
import com.example.elasticsearch.service.ElasticsearchService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@TestPropertySource(properties = {
    "elasticsearch.host=localhost",
    "elasticsearch.port=9200",
    "app.query.template-path=classpath:query-templates/"
})
public class UserTemplateIntegrationTest {

    @Autowired
    private ElasticsearchService elasticsearchService;

    private ObjectMapper objectMapper = new ObjectMapper();
    private static final String INDEX_NAME = "user";

    @BeforeEach
    void setUp() throws IOException, InterruptedException {
        // Clean up and create index
        try {
            elasticsearchService.executeRawQuery(INDEX_NAME, "DELETE", "", null);
        } catch (Exception e) {
            // Index might not exist, ignore
        }
        
        // Create index with proper mapping
        String indexMapping = """
            {
              "mappings": {
                "properties": {
                  "userId": { "type": "long" },
                  "userName": { "type": "text", "fields": { "keyword": { "type": "keyword" } } },
                  "courses": { "type": "keyword" },
                  "grades": {
                    "type": "nested",
                    "properties": {
                      "course": { "type": "keyword" },
                      "score": { "type": "double" }
                    }
                  }
                }
              }
            }
            """;
        elasticsearchService.executeRawQuery(INDEX_NAME, "PUT", "", indexMapping);
        
        // Insert test data
        insertTestData();
    }

    private void insertTestData() throws IOException, InterruptedException {
        // User 1
        String user1 = """
            {
              "userId": 1,
              "userName": "John Doe",
              "courses": ["Math", "Science"],
              "grades": [
                {"course": "Math", "score": 85.5},
                {"course": "Science", "score": 92.0}
              ]
            }
            """;
        elasticsearchService.create(INDEX_NAME, "1", user1);

        // User 2
        String user2 = """
            {
              "userId": 2,
              "userName": "Jane Smith",
              "courses": ["Math", "History"],
              "grades": [
                {"course": "Math", "score": 78.0},
                {"course": "History", "score": 88.5}
              ]
            }
            """;
        elasticsearchService.create(INDEX_NAME, "2", user2);

        // User 3
        String user3 = """
            {
              "userId": 3,
              "userName": "Bob Johnson",
              "courses": ["Science", "History"],
              "grades": [
                {"course": "Science", "score": 95.0},
                {"course": "History", "score": 82.0}
              ]
            }
            """;
        elasticsearchService.create(INDEX_NAME, "3", user3);

        // Wait for indexing
        Thread.sleep(1000);
    }

    @Test
    void testSearchByUserId() throws IOException {
        UserTemplateParams params = new UserTemplateParams(1L);
        JsonNode result = elasticsearchService.executeTemplate("USER_OPERATIONS", "searchByUserId", params);
        
        assertNotNull(result);
        assertTrue(result.has("hits"));
        assertEquals(1, result.get("hits").get("total").get("value").asInt());
        assertEquals("John Doe", result.get("hits").get("hits").get(0).get("_source").get("userName").asText());
    }

    @Test
    void testSearchByUserName() throws IOException {
        UserTemplateParams params = new UserTemplateParams("Jane Smith");
        JsonNode result = elasticsearchService.executeTemplate("USER_OPERATIONS", "searchByUserName", params);
        
        assertNotNull(result);
        assertTrue(result.has("hits"));
        assertEquals(1, result.get("hits").get("total").get("value").asInt());
        assertEquals(2, result.get("hits").get("hits").get(0).get("_source").get("userId").asInt());
    }

    @Test
    void testSearchByCourse() throws IOException {
        UserTemplateParams params = new UserTemplateParams(Arrays.asList("Math"));
        JsonNode result = elasticsearchService.executeTemplate("USER_OPERATIONS", "searchByCourse", params);
        
        assertNotNull(result);
        assertTrue(result.has("hits"));
        assertEquals(2, result.get("hits").get("total").get("value").asInt());
    }

    @Test
    void testSearchByGradeRange() throws IOException {
        UserTemplateParams params = new UserTemplateParams("Math", 80.0, 90.0);
        JsonNode result = elasticsearchService.executeTemplate("USER_OPERATIONS", "searchByGradeRange", params);
        
        assertNotNull(result);
        assertTrue(result.has("hits"));
        assertEquals(1, result.get("hits").get("total").get("value").asInt());
    }

    @Test
    void testMultiSearchUsers() throws IOException {
        UserTemplateParams params = new UserTemplateParams(1L, 2L);
        JsonNode result = elasticsearchService.executeTemplate("USER_OPERATIONS", "multiSearchUsers", params);
        
        assertNotNull(result);
        assertTrue(result.has("responses"));
        assertEquals(2, result.get("responses").size());
    }

    @Test
    void testAggregateGradesByCourse() throws IOException {
        UserTemplateParams params = new UserTemplateParams();
        JsonNode result = elasticsearchService.executeTemplate("USER_OPERATIONS", "aggregateGradesByCourse", params);
        
        assertNotNull(result);
        assertTrue(result.has("aggregations"));
        assertTrue(result.get("aggregations").has("courses"));
    }

    @Test
    void testAggregateUsersByCourse() throws IOException {
        UserTemplateParams params = new UserTemplateParams();
        JsonNode result = elasticsearchService.executeTemplate("USER_OPERATIONS", "aggregateUsersByCourse", params);
        
        assertNotNull(result);
        assertTrue(result.has("aggregations"));
        assertTrue(result.get("aggregations").has("course_enrollment"));
    }

    @Test
    void testAggregateGradeDistribution() throws IOException {
        UserTemplateParams params = new UserTemplateParams();
        params.setCourse("Math");
        JsonNode result = elasticsearchService.executeTemplate("USER_OPERATIONS", "aggregateGradeDistribution", params);
        
        assertNotNull(result);
        assertTrue(result.has("aggregations"));
        assertTrue(result.get("aggregations").has("grade_ranges"));
    }

    @Test
    void testCRUDOperations() throws IOException {
        // Test Create
        String newUser = """
            {
              "userId": 4,
              "userName": "Alice Brown",
              "courses": ["Physics"],
              "grades": [
                {"course": "Physics", "score": 90.0}
              ]
            }
            """;
        JsonNode createResult = elasticsearchService.create(INDEX_NAME, "4", newUser);
        assertNotNull(createResult);
        assertEquals("created", createResult.get("result").asText());

        // Test Read
        JsonNode readResult = elasticsearchService.read(INDEX_NAME, "4");
        assertNotNull(readResult);
        assertEquals("Alice Brown", readResult.get("_source").get("userName").asText());

        // Test Update
        String updatedUser = """
            {
              "userId": 4,
              "userName": "Alice Brown",
              "courses": ["Physics", "Chemistry"],
              "grades": [
                {"course": "Physics", "score": 90.0},
                {"course": "Chemistry", "score": 87.5}
              ]
            }
            """;
        JsonNode updateResult = elasticsearchService.update(INDEX_NAME, "4", updatedUser);
        assertNotNull(updateResult);
        assertEquals("updated", updateResult.get("result").asText());

        // Test Partial Update
        String partialUpdate = """
            {
              "userName": "Alice Brown-Smith"
            }
            """;
        JsonNode partialResult = elasticsearchService.partialUpdate(INDEX_NAME, "4", partialUpdate);
        assertNotNull(partialResult);
        assertEquals("updated", partialResult.get("result").asText());

        // Test Delete
        JsonNode deleteResult = elasticsearchService.delete(INDEX_NAME, "4");
        assertNotNull(deleteResult);
        assertEquals("deleted", deleteResult.get("result").asText());
    }
}
