package com.example.elasticsearch.service;

import com.example.elasticsearch.query.QueryParams;
import org.apache.http.entity.BasicHttpEntity;
import org.elasticsearch.client.Request;
import org.elasticsearch.client.Response;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestClientBuilder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import com.fasterxml.jackson.databind.JsonNode;
import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import org.mockito.ArgumentCaptor;

public class ElasticsearchServiceTest {
    private RestClient mockClient;
    private ElasticsearchService svc;
    private QueryTemplateService tpl;
    private RestClientBuilder builder;

    @BeforeEach
    void setUp() {
        mockClient = mock(RestClient.class);
        tpl = mock(QueryTemplateService.class);
        builder = mock(RestClientBuilder.class);
        when(builder.build()).thenReturn(mockClient);
        svc = new ElasticsearchService(builder, tpl);
    }

    private record DummyParams(Object userId) implements QueryParams {}

    @Test
    void search_parsesHits() throws Exception {
        when(tpl.render("user", "simple-search.json", new DummyParams(1234L)))
            .thenReturn("{\"query\":{\"term\":{\"userId\":1234}}}");
        String fake = "{ \"hits\": { \"total\": {\"value\":1}, \"hits\": [ {\"_source\":{\"userId\":1234}} ] } }";
        Response resp = mock(Response.class);
        BasicHttpEntity entity = new BasicHttpEntity();
        entity.setContent(new ByteArrayInputStream(fake.getBytes(StandardCharsets.UTF_8)));
        when(resp.getEntity()).thenReturn(entity);
        when(mockClient.performRequest(any(Request.class))).thenReturn(resp);

        JsonNode result = svc.search("user", "simple-search.json", new DummyParams(1234L));
        assertEquals(1, result.path("hits").path("total").path("value").asInt());
        assertEquals(1234, result.path("hits").path("hits").get(0).path("_source").path("userId").asInt());

        ArgumentCaptor<Request> cap = ArgumentCaptor.forClass(Request.class);
        verify(mockClient).performRequest(cap.capture());
        Request sent = cap.getValue();
        assertEquals("GET", sent.getMethod());
        assertTrue(sent.getEndpoint().endsWith("/user/_search"));
    }
}
