package com.example.elasticsearch.service;

import com.example.elasticsearch.model.QueryTemplate;
import com.example.elasticsearch.query.QueryParams;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.mustachejava.DefaultMustacheFactory;
import com.github.mustachejava.Mustache;
import com.github.mustachejava.MustacheFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Component;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.Constructor;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class QueryTemplateService {
    private final MustacheFactory mustacheFactory;
    private final ResourceLoader loader;
    private final String basePath;
    private final ObjectMapper mapper;
    private final Yaml yaml;
    private final Map<String, QueryTemplate> templateCache = new ConcurrentHashMap<>();

    public QueryTemplateService(@Value("${app.query.template-path}") String basePath,
                                ResourceLoader loader,
                                ObjectMapper mapper) {
        this.mustacheFactory = new DefaultMustacheFactory();
        this.loader = loader;
        this.basePath = basePath;
        this.mapper = mapper;
        this.yaml = new Yaml(new Constructor(QueryTemplate.class));
    }

    public String render(String schemaName, String templateName, QueryParams params) {
        Resource res = loader.getResource(basePath + schemaName + "/" + templateName);
        Map<String, Object> ctx = mapper.convertValue(params, new TypeReference<>() {});
        try (Reader reader = new InputStreamReader(res.getInputStream(), StandardCharsets.UTF_8)) {
            Mustache mustache = mustacheFactory.compile(reader, templateName);
            return mustache.execute(new StringWriter(), ctx).toString();
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    public JsonNode executeTemplate(String operationType, String templateName, QueryParams params, ElasticsearchService esService) throws IOException {
        QueryTemplate queryTemplate = loadTemplate(operationType);
        
        QueryTemplate.Template template = queryTemplate.getTemplates().stream()
                .filter(t -> templateName.equals(t.getName()))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Template not found: " + templateName));

        // Convert params to map for template variable substitution
        Map<String, Object> paramMap = mapper.convertValue(params, new TypeReference<>() {});
        
        // Process the base query with mustache templating
        String queryJson = processTemplate(mapper.writeValueAsString(template.getBaseQuery()), paramMap);
        
        // Execute based on query type
        String queryType = template.getQueryType();
        if (queryType == null) queryType = "search";
        
        switch (queryType.toLowerCase()) {
            case "search":
                return esService.executeRawQuery(getIndexFromOperationType(operationType), "GET", "/_search", queryJson);
            case "agg":
            case "aggregation":
                return esService.executeRawQuery(getIndexFromOperationType(operationType), "GET", "/_search", queryJson);
            case "msearch":
                return esService.executeRawQuery(getIndexFromOperationType(operationType), "POST", "/_msearch", queryJson);
            default:
                throw new IllegalArgumentException("Unsupported query type: " + queryType);
        }
    }

    private QueryTemplate loadTemplate(String operationType) throws IOException {
        return templateCache.computeIfAbsent(operationType, key -> {
            try {
                Resource res = loader.getResource("classpath:query-templates/" + key + ".yml");
                try (Reader reader = new InputStreamReader(res.getInputStream(), StandardCharsets.UTF_8)) {
                    return yaml.load(reader);
                }
            } catch (IOException e) {
                throw new UncheckedIOException(e);
            }
        });
    }

    private String processTemplate(String template, Map<String, Object> params) {
        try (StringReader reader = new StringReader(template)) {
            Mustache mustache = mustacheFactory.compile(reader, "template");
            StringWriter writer = new StringWriter();
            mustache.execute(writer, params);
            return writer.toString();
        }
    }

    private String getIndexFromOperationType(String operationType) {
        // Extract index name from operation type
        // For example: TRANSACTION_SUMMARY -> transaction
        return operationType.toLowerCase().split("_")[0];
    }
}
