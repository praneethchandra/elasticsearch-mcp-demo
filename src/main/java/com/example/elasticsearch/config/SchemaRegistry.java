package com.example.elasticsearch.config;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.networknt.schema.JsonSchema;
import com.networknt.schema.JsonSchemaFactory;
import com.networknt.schema.SpecVersion;
import com.networknt.schema.ValidationMessage;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.stereotype.Component;
import jakarta.annotation.PostConstruct;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@Component
public class SchemaRegistry {
    private final Map<String, JsonSchema> schemas = new HashMap<>();
    private final ObjectMapper mapper = new ObjectMapper();

    @PostConstruct
    public void loadAll() throws IOException {
        JsonSchemaFactory factory = JsonSchemaFactory.getInstance(SpecVersion.VersionFlag.V202012);
        PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
        for (Resource r : resolver.getResources("classpath:schemas/*-schema.json")) {
            String name = r.getFilename().replace("-schema.json", "");
            JsonNode node = mapper.readTree(r.getInputStream());
            schemas.put(name, factory.getSchema(node));
        }
    }

    public void validate(String schemaName, JsonNode instance) {
        Set<ValidationMessage> errors = schemas.get(schemaName).validate(instance);
        if (!errors.isEmpty()) {
            throw new RuntimeException("Schema validation failed: " + errors);
        }
    }
}
