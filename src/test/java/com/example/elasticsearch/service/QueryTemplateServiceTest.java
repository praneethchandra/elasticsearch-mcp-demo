package com.example.elasticsearch.service;

import com.example.elasticsearch.query.QueryParams;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.core.io.DefaultResourceLoader;
import com.fasterxml.jackson.databind.ObjectMapper;
import static org.junit.jupiter.api.Assertions.*;

public class QueryTemplateServiceTest {
    private QueryTemplateService tpl;

    @BeforeEach
    void setUp() {
        tpl = new QueryTemplateService("classpath:es-templates/", new DefaultResourceLoader(), new ObjectMapper());
    }

    private record TestParams(String name) implements QueryParams {}

    @Test
    void renderSimpleTemplate() {
        String rendered = tpl.render("test", "test.json", new TestParams("World"));
        assertEquals("{ \"greeting\": \"Hello, World!\" }", rendered.trim());
    }
}
