package com.example.elasticsearch.config;

import org.elasticsearch.client.RestClientBuilder;
import org.elasticsearch.client.RestClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.beans.factory.annotation.Value;
import org.apache.http.HttpHost;

@Configuration
public class RestClientConfig {

    @Bean
    public RestClientBuilder builder(@Value("${spring.elasticsearch.rest.uris}") String uri) {
        String[] parts = uri.replace("http://","").split(":");
        return RestClient.builder(new HttpHost(parts[0], Integer.parseInt(parts[1]), "http"));
    }
}
