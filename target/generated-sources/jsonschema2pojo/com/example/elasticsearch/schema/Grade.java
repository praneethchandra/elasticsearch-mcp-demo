
package com.example.elasticsearch.schema;

import java.util.HashMap;
import java.util.Map;
import javax.annotation.processing.Generated;
import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "course",
    "score"
})
@Generated("jsonschema2pojo")
public class Grade {

    /**
     * 
     * (Required)
     * 
     */
    @JsonProperty("course")
    private String course;
    /**
     * 
     * (Required)
     * 
     */
    @JsonProperty("score")
    private Double score;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    /**
     * 
     * (Required)
     * 
     */
    @JsonProperty("course")
    public String getCourse() {
        return course;
    }

    /**
     * 
     * (Required)
     * 
     */
    @JsonProperty("course")
    public void setCourse(String course) {
        this.course = course;
    }

    public Grade withCourse(String course) {
        this.course = course;
        return this;
    }

    /**
     * 
     * (Required)
     * 
     */
    @JsonProperty("score")
    public Double getScore() {
        return score;
    }

    /**
     * 
     * (Required)
     * 
     */
    @JsonProperty("score")
    public void setScore(Double score) {
        this.score = score;
    }

    public Grade withScore(Double score) {
        this.score = score;
        return this;
    }

    @JsonAnyGetter
    public Map<String, Object> getAdditionalProperties() {
        return this.additionalProperties;
    }

    @JsonAnySetter
    public void setAdditionalProperty(String name, Object value) {
        this.additionalProperties.put(name, value);
    }

    public Grade withAdditionalProperty(String name, Object value) {
        this.additionalProperties.put(name, value);
        return this;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(Grade.class.getName()).append('@').append(Integer.toHexString(System.identityHashCode(this))).append('[');
        sb.append("course");
        sb.append('=');
        sb.append(((this.course == null)?"<null>":this.course));
        sb.append(',');
        sb.append("score");
        sb.append('=');
        sb.append(((this.score == null)?"<null>":this.score));
        sb.append(',');
        sb.append("additionalProperties");
        sb.append('=');
        sb.append(((this.additionalProperties == null)?"<null>":this.additionalProperties));
        sb.append(',');
        if (sb.charAt((sb.length()- 1)) == ',') {
            sb.setCharAt((sb.length()- 1), ']');
        } else {
            sb.append(']');
        }
        return sb.toString();
    }

    @Override
    public int hashCode() {
        int result = 1;
        result = ((result* 31)+((this.course == null)? 0 :this.course.hashCode()));
        result = ((result* 31)+((this.score == null)? 0 :this.score.hashCode()));
        result = ((result* 31)+((this.additionalProperties == null)? 0 :this.additionalProperties.hashCode()));
        return result;
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        if ((other instanceof Grade) == false) {
            return false;
        }
        Grade rhs = ((Grade) other);
        return ((((this.course == rhs.course)||((this.course!= null)&&this.course.equals(rhs.course)))&&((this.score == rhs.score)||((this.score!= null)&&this.score.equals(rhs.score))))&&((this.additionalProperties == rhs.additionalProperties)||((this.additionalProperties!= null)&&this.additionalProperties.equals(rhs.additionalProperties))));
    }

}
