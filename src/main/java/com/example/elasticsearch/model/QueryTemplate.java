package com.example.elasticsearch.model;

import java.util.List;
import java.util.Map;

public class QueryTemplate {
    private String operationType;
    private List<Template> templates;
    private String responseProcessor;

    public static class Template {
        private String name;
        private String queryType;
        private Map<String, Object> baseQuery;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getQueryType() {
            return queryType;
        }

        public void setQueryType(String queryType) {
            this.queryType = queryType;
        }

        public Map<String, Object> getBaseQuery() {
            return baseQuery;
        }

        public void setBaseQuery(Map<String, Object> baseQuery) {
            this.baseQuery = baseQuery;
        }
    }

    public String getOperationType() {
        return operationType;
    }

    public void setOperationType(String operationType) {
        this.operationType = operationType;
    }

    public List<Template> getTemplates() {
        return templates;
    }

    public void setTemplates(List<Template> templates) {
        this.templates = templates;
    }

    public String getResponseProcessor() {
        return responseProcessor;
    }

    public void setResponseProcessor(String responseProcessor) {
        this.responseProcessor = responseProcessor;
    }
}
