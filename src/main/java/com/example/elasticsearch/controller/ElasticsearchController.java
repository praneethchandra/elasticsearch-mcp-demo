package com.example.elasticsearch.controller;

import com.example.elasticsearch.query.QueryParams;
import com.example.elasticsearch.service.ElasticsearchService;
import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.Map;

@RestController
@RequestMapping("/api/elasticsearch")
public class ElasticsearchController {
    private final ElasticsearchService elasticsearchService;

    public ElasticsearchController(ElasticsearchService elasticsearchService) {
        this.elasticsearchService = elasticsearchService;
    }

    // CRUD Operations
    @PostMapping(value = "/{indexName}/document", produces = MediaType.APPLICATION_JSON_VALUE)
    public JsonNode createDocument(@PathVariable String indexName, @RequestBody String document) throws IOException {
        return elasticsearchService.create(indexName, document);
    }

    @PostMapping(value = "/{indexName}/document/{documentId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public JsonNode createDocumentWithId(@PathVariable String indexName, @PathVariable String documentId, @RequestBody String document) throws IOException {
        return elasticsearchService.create(indexName, documentId, document);
    }

    @GetMapping(value = "/{indexName}/document/{documentId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public JsonNode getDocument(@PathVariable String indexName, @PathVariable String documentId) throws IOException {
        return elasticsearchService.read(indexName, documentId);
    }

    @PutMapping(value = "/{indexName}/document/{documentId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public JsonNode updateDocument(@PathVariable String indexName, @PathVariable String documentId, @RequestBody String document) throws IOException {
        return elasticsearchService.update(indexName, documentId, document);
    }

    @PatchMapping(value = "/{indexName}/document/{documentId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public JsonNode partialUpdateDocument(@PathVariable String indexName, @PathVariable String documentId, @RequestBody String partialDocument) throws IOException {
        return elasticsearchService.partialUpdate(indexName, documentId, partialDocument);
    }

    @DeleteMapping(value = "/{indexName}/document/{documentId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public JsonNode deleteDocument(@PathVariable String indexName, @PathVariable String documentId) throws IOException {
        return elasticsearchService.delete(indexName, documentId);
    }

    // Search Operations
    @PostMapping(value = "/{indexName}/search", produces = MediaType.APPLICATION_JSON_VALUE)
    public JsonNode search(@PathVariable String indexName, @RequestBody String query) throws IOException {
        return elasticsearchService.executeRawQuery(indexName, "GET", "/_search", query);
    }

    @PostMapping(value = "/{indexName}/msearch", produces = MediaType.APPLICATION_JSON_VALUE)
    public JsonNode multiSearch(@PathVariable String indexName, @RequestBody String queries) throws IOException {
        return elasticsearchService.executeRawQuery(indexName, "POST", "/_msearch", queries);
    }

    @PostMapping(value = "/{indexName}/aggregate", produces = MediaType.APPLICATION_JSON_VALUE)
    public JsonNode aggregate(@PathVariable String indexName, @RequestBody String aggregationQuery) throws IOException {
        return elasticsearchService.executeRawQuery(indexName, "GET", "/_search", aggregationQuery);
    }

    // Template-based operations
    @PostMapping(value = "/template/{operationType}/{templateName}", produces = MediaType.APPLICATION_JSON_VALUE)
    public JsonNode executeTemplate(@PathVariable String operationType, @PathVariable String templateName, @RequestBody QueryParams params) throws IOException {
        return elasticsearchService.executeTemplate(operationType, templateName, params);
    }

    // Raw query execution
    @PostMapping(value = "/{indexName}/raw", produces = MediaType.APPLICATION_JSON_VALUE)
    public JsonNode executeRawQuery(@PathVariable String indexName, @RequestParam String method, @RequestParam String endpoint, @RequestBody(required = false) String body) throws IOException {
        return elasticsearchService.executeRawQuery(indexName, method, endpoint, body);
    }

    // Bulk operations
    @PostMapping(value = "/{indexName}/bulk", produces = MediaType.APPLICATION_JSON_VALUE)
    public JsonNode bulkOperation(@PathVariable String indexName, @RequestBody String bulkData) throws IOException {
        return elasticsearchService.executeRawQuery(indexName, "POST", "/_bulk", bulkData);
    }

    // Index management
    @PutMapping(value = "/{indexName}", produces = MediaType.APPLICATION_JSON_VALUE)
    public JsonNode createIndex(@PathVariable String indexName, @RequestBody(required = false) String settings) throws IOException {
        return elasticsearchService.executeRawQuery(indexName, "PUT", "", settings);
    }

    @DeleteMapping(value = "/{indexName}", produces = MediaType.APPLICATION_JSON_VALUE)
    public JsonNode deleteIndex(@PathVariable String indexName) throws IOException {
        return elasticsearchService.executeRawQuery(indexName, "DELETE", "", null);
    }

    @GetMapping(value = "/{indexName}", produces = MediaType.APPLICATION_JSON_VALUE)
    public JsonNode getIndexInfo(@PathVariable String indexName) throws IOException {
        return elasticsearchService.executeRawQuery(indexName, "GET", "", null);
    }
}
