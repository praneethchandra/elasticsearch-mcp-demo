package com.example.elasticsearch.service;

import com.example.elasticsearch.query.QueryParams;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.elasticsearch.client.Request;
import org.elasticsearch.client.Response;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestClientBuilder;
import org.springframework.stereotype.Service;
import java.io.IOException;
import java.io.InputStream;

@Service
public class ElasticsearchService {
    private final RestClient es;
    private final QueryTemplateService tpl;
    private final ObjectMapper mapper = new ObjectMapper();

    public ElasticsearchService(RestClientBuilder builder, QueryTemplateService tpl) {
        this.es = builder.build();
        this.tpl = tpl;
    }

    public JsonNode search(String schemaName, String templateName, QueryParams params) throws IOException {
        String body = tpl.render(schemaName, templateName, params);
        Request req = new Request("GET", "/" + schemaName + "/_search");
        req.setJsonEntity(body);
        return parse(es.performRequest(req));
    }

    public JsonNode msearch(String schemaName, String templateName, QueryParams params) throws IOException {
        String body = tpl.render(schemaName, templateName, params);
        Request req = new Request("POST", "/" + schemaName + "/_msearch");
        req.setJsonEntity(body);
        return parse(es.performRequest(req));
    }

    public JsonNode aggregate(String schemaName, String templateName, QueryParams params) throws IOException {
        String body = tpl.render(schemaName, templateName, params);
        Request req = new Request("GET", "/" + schemaName + "/_search");
        req.setJsonEntity(body);
        return parse(es.performRequest(req));
    }

    private JsonNode parse(Response resp) throws IOException {
        try (InputStream in = resp.getEntity().getContent()) {
            return mapper.readTree(in);
        }
    }
}
