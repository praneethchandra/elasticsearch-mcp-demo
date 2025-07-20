package com.example.elasticsearch.integration;

import com.example.elasticsearch.service.ElasticsearchService;
import com.example.elasticsearch.config.RestClientConfig;
import com.example.elasticsearch.query.UserSearchParams;
import com.example.elasticsearch.query.UserAggParams;
import com.example.elasticsearch.query.ClassAverageParams;
import com.example.elasticsearch.query.CourseMaxParams;
import com.example.elasticsearch.query.UserMultipleCoursesParams;
import com.example.elasticsearch.query.UserCourseParams;
import com.fasterxml.jackson.databind.JsonNode;
import org.elasticsearch.client.Request;
import org.elasticsearch.client.RestClient;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfSystemProperty;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.testcontainers.elasticsearch.ElasticsearchContainer;
import org.testcontainers.utility.DockerImageName;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@EnabledIfSystemProperty(named = "docker.available", matches = "true", disabledReason = "Docker not available")
public class ElasticsearchIntegrationTest {
    private static ElasticsearchContainer esContainer;

    @Autowired
    private ElasticsearchService esService;

    @Autowired
    private RestClientConfig restConfig;

    @BeforeAll
    static void setup() {
        esContainer = new ElasticsearchContainer(DockerImageName.parse("docker.elastic.co/elasticsearch/elasticsearch:8.7.0"))
            .withEnv("discovery.type", "single-node");
        esContainer.start();
        System.setProperty("spring.elasticsearch.rest.uris", esContainer.getHttpHostAddress());
    }

    @AfterAll
    static void tearDown() {
        esContainer.stop();
    }

    @Test
    void testAllQueries() throws Exception {
        RestClient client = restConfig.builder(esContainer.getHttpHostAddress()).build();
        // index a document
        String doc = "{ \"userId\":1234, \"userName\":\"Alice\", \"courses\":[\"english\",\"math\",\"science\"], "
                     + "\"grades\":[{\"course\":\"math\",\"score\":85},{\"course\":\"science\",\"score\":90}] }";
        Request indexRequest = new Request("POST", "/user/_doc");
        indexRequest.setJsonEntity(doc);
        client.performRequest(indexRequest);
        
        Request refreshRequest = new Request("POST", "/user/_refresh");
        client.performRequest(refreshRequest);

        // users in course
        JsonNode inMath = esService.search("user", "users-in-course.json", new UserCourseParams("math"));
        assertTrue(inMath.path("hits").path("total").path("value").asInt() >= 1);

        // multiple courses
        JsonNode multi = esService.search("user", "users-multiple-courses.json", new UserMultipleCoursesParams());
        assertTrue(multi.path("hits").path("total").path("value").asInt() >= 1);

        // class average
        JsonNode avg = esService.aggregate("user", "class-average.json", new ClassAverageParams());
        assertTrue(avg.path("aggregations").path("all_grades").path("avg_score").path("value").asDouble() > 0);

        // course max
        JsonNode max = esService.aggregate("user", "course-max.json", new CourseMaxParams("science"));
        assertEquals(90.0, max.path("aggregations").path("max_score")
                                  .path("filter_course").path("max_score").path("value").asDouble());
    }
}
