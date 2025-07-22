package com.example.elasticsearch.query;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserTemplateParams implements QueryParams {
    private Long userId;
    private String userName;
    private List<String> courses;
    private String course;
    private Double minScore;
    private Double maxScore;
    private Long userId1;
    private Long userId2;

    public UserTemplateParams(Long userId) {
        this.userId = userId;
    }

    public UserTemplateParams(String userName) {
        this.userName = userName;
    }

    public UserTemplateParams(List<String> courses) {
        this.courses = courses;
    }

    public UserTemplateParams(String course, Double minScore, Double maxScore) {
        this.course = course;
        this.minScore = minScore;
        this.maxScore = maxScore;
    }

    public UserTemplateParams(Long userId1, Long userId2) {
        this.userId1 = userId1;
        this.userId2 = userId2;
    }
}
