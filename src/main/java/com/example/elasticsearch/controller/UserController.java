package com.example.elasticsearch.controller;

import com.example.elasticsearch.query.UserAggParams;
import com.example.elasticsearch.query.UserMultiSearchParams;
import com.example.elasticsearch.query.UserSearchParams;
import com.example.elasticsearch.service.ElasticsearchService;
import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import java.io.IOException;

@RestController
@RequestMapping("/api/users")
public class UserController {
    private final ElasticsearchService es;

    public UserController(ElasticsearchService es) {
        this.es = es;
    }

    @GetMapping(value = "/by-id/{userId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public JsonNode byId(@PathVariable Long userId) throws IOException {
        return es.search("user", "simple-search.json", new UserSearchParams(userId));
    }

    @GetMapping(value = "/search", produces = MediaType.APPLICATION_JSON_VALUE)
    public JsonNode byName(@RequestParam String userName) throws IOException {
        return es.msearch("user", "multi-search.msearch", new UserMultiSearchParams(userName));
    }

    @GetMapping(value = "/stats/{userId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public JsonNode stats(@PathVariable Long userId) throws IOException {
        return es.aggregate("user", "agg-count-sum.json", new UserAggParams(userId));
    }
}
