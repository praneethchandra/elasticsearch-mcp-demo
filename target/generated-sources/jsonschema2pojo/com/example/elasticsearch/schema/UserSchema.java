
package com.example.elasticsearch.schema;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.processing.Generated;
import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;


/**
 * User
 * <p>
 * 
 * 
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "userId",
    "userName",
    "courses",
    "grades"
})
@Generated("jsonschema2pojo")
public class UserSchema {

    /**
     * 
     * (Required)
     * 
     */
    @JsonProperty("userId")
    private Integer userId;
    /**
     * 
     * (Required)
     * 
     */
    @JsonProperty("userName")
    private String userName;
    /**
     * 
     * (Required)
     * 
     */
    @JsonProperty("courses")
    private List<String> courses = new ArrayList<String>();
    /**
     * 
     * (Required)
     * 
     */
    @JsonProperty("grades")
    private List<Grade> grades = new ArrayList<Grade>();
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    /**
     * 
     * (Required)
     * 
     */
    @JsonProperty("userId")
    public Integer getUserId() {
        return userId;
    }

    /**
     * 
     * (Required)
     * 
     */
    @JsonProperty("userId")
    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public UserSchema withUserId(Integer userId) {
        this.userId = userId;
        return this;
    }

    /**
     * 
     * (Required)
     * 
     */
    @JsonProperty("userName")
    public String getUserName() {
        return userName;
    }

    /**
     * 
     * (Required)
     * 
     */
    @JsonProperty("userName")
    public void setUserName(String userName) {
        this.userName = userName;
    }

    public UserSchema withUserName(String userName) {
        this.userName = userName;
        return this;
    }

    /**
     * 
     * (Required)
     * 
     */
    @JsonProperty("courses")
    public List<String> getCourses() {
        return courses;
    }

    /**
     * 
     * (Required)
     * 
     */
    @JsonProperty("courses")
    public void setCourses(List<String> courses) {
        this.courses = courses;
    }

    public UserSchema withCourses(List<String> courses) {
        this.courses = courses;
        return this;
    }

    /**
     * 
     * (Required)
     * 
     */
    @JsonProperty("grades")
    public List<Grade> getGrades() {
        return grades;
    }

    /**
     * 
     * (Required)
     * 
     */
    @JsonProperty("grades")
    public void setGrades(List<Grade> grades) {
        this.grades = grades;
    }

    public UserSchema withGrades(List<Grade> grades) {
        this.grades = grades;
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

    public UserSchema withAdditionalProperty(String name, Object value) {
        this.additionalProperties.put(name, value);
        return this;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(UserSchema.class.getName()).append('@').append(Integer.toHexString(System.identityHashCode(this))).append('[');
        sb.append("userId");
        sb.append('=');
        sb.append(((this.userId == null)?"<null>":this.userId));
        sb.append(',');
        sb.append("userName");
        sb.append('=');
        sb.append(((this.userName == null)?"<null>":this.userName));
        sb.append(',');
        sb.append("courses");
        sb.append('=');
        sb.append(((this.courses == null)?"<null>":this.courses));
        sb.append(',');
        sb.append("grades");
        sb.append('=');
        sb.append(((this.grades == null)?"<null>":this.grades));
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
        result = ((result* 31)+((this.courses == null)? 0 :this.courses.hashCode()));
        result = ((result* 31)+((this.grades == null)? 0 :this.grades.hashCode()));
        result = ((result* 31)+((this.additionalProperties == null)? 0 :this.additionalProperties.hashCode()));
        result = ((result* 31)+((this.userName == null)? 0 :this.userName.hashCode()));
        result = ((result* 31)+((this.userId == null)? 0 :this.userId.hashCode()));
        return result;
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        if ((other instanceof UserSchema) == false) {
            return false;
        }
        UserSchema rhs = ((UserSchema) other);
        return ((((((this.courses == rhs.courses)||((this.courses!= null)&&this.courses.equals(rhs.courses)))&&((this.grades == rhs.grades)||((this.grades!= null)&&this.grades.equals(rhs.grades))))&&((this.additionalProperties == rhs.additionalProperties)||((this.additionalProperties!= null)&&this.additionalProperties.equals(rhs.additionalProperties))))&&((this.userName == rhs.userName)||((this.userName!= null)&&this.userName.equals(rhs.userName))))&&((this.userId == rhs.userId)||((this.userId!= null)&&this.userId.equals(rhs.userId))));
    }

}
