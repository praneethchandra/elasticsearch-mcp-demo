package com.example.elasticsearch.service;

import com.example.elasticsearch.query.QueryParams;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.mustachejava.DefaultMustacheFactory;
import com.github.mustachejava.Mustache;
import com.github.mustachejava.MustacheFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Component;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.util.Map;

@Component
public class QueryTemplateService {
    private final MustacheFactory mustacheFactory;
    private final ResourceLoader loader;
    private final String basePath;
    private final ObjectMapper mapper;

    public QueryTemplateService(@Value("${app.query.template-path}") String basePath,
                                ResourceLoader loader,
                                ObjectMapper mapper) {
        this.mustacheFactory = new DefaultMustacheFactory();
        this.loader = loader;
        this.basePath = basePath;
        this.mapper = mapper;
    }

    public String render(String schemaName, String templateName, QueryParams params) {
        Resource res = loader.getResource(basePath + schemaName + "/" + templateName);
        Map<String, Object> ctx = mapper.convertValue(params, new TypeReference<>() {});
        try (Reader reader = new InputStreamReader(res.getInputStream(), StandardCharsets.UTF_8)) {
            Mustache mustache = mustacheFactory.compile(reader, templateName);
            return mustache.execute(new java.io.StringWriter(), ctx).toString();
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }
}
