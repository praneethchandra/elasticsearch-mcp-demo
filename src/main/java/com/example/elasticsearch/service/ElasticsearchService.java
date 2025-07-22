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

    // CRUD Operations
    public JsonNode create(String indexName, String documentId, String document) throws IOException {
        Request req = new Request("POST", "/" + indexName + "/_doc/" + documentId);
        req.setJsonEntity(document);
        return parse(es.performRequest(req));
    }

    public JsonNode create(String indexName, String document) throws IOException {
        Request req = new Request("POST", "/" + indexName + "/_doc");
        req.setJsonEntity(document);
        return parse(es.performRequest(req));
    }

    public JsonNode read(String indexName, String documentId) throws IOException {
        Request req = new Request("GET", "/" + indexName + "/_doc/" + documentId);
        return parse(es.performRequest(req));
    }

    public JsonNode update(String indexName, String documentId, String document) throws IOException {
        Request req = new Request("PUT", "/" + indexName + "/_doc/" + documentId);
        req.setJsonEntity(document);
        return parse(es.performRequest(req));
    }

    public JsonNode partialUpdate(String indexName, String documentId, String partialDocument) throws IOException {
        Request req = new Request("POST", "/" + indexName + "/_update/" + documentId);
        req.setJsonEntity("{\"doc\":" + partialDocument + "}");
        return parse(es.performRequest(req));
    }

    public JsonNode delete(String indexName, String documentId) throws IOException {
        Request req = new Request("DELETE", "/" + indexName + "/_doc/" + documentId);
        return parse(es.performRequest(req));
    }

    // Search Operations
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

    // Template-based operations using YAML configuration
    public JsonNode executeTemplate(String operationType, String templateName, QueryParams params) throws IOException {
        return tpl.executeTemplate(operationType, templateName, params, this);
    }

    // Raw query execution
    public JsonNode executeRawQuery(String indexName, String method, String endpoint, String body) throws IOException {
        Request req = new Request(method, "/" + indexName + endpoint);
        if (body != null && !body.isEmpty()) {
            req.setJsonEntity(body);
        }
        return parse(es.performRequest(req));
    }

    private JsonNode parse(Response resp) throws IOException {
        try (InputStream in = resp.getEntity().getContent()) {
            return mapper.readTree(in);
        }
    }
}
