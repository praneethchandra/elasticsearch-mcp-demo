package com.example.elasticsearch.controller;

import com.example.elasticsearch.query.*;
import com.example.elasticsearch.service.ElasticsearchService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.theokanning.openai.service.OpenAiService;
import com.theokanning.openai.completion.chat.ChatCompletionChoice;
import com.theokanning.openai.completion.chat.ChatCompletionRequest;
import com.theokanning.openai.completion.chat.ChatCompletionResult;
import com.theokanning.openai.completion.chat.ChatMessage;
import com.theokanning.openai.completion.chat.ChatFunctionCall;
import com.theokanning.openai.completion.chat.ChatFunction;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

import java.time.Duration;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/mcp")
public class McpController {
  private final OpenAiService openAi;
  private final ElasticsearchService es;
  private final ObjectMapper mapper = new ObjectMapper();

  public McpController(@Value("${OPENAI_API_KEY}") String key,
                       ElasticsearchService es) {
    this.openAi = new OpenAiService(key, Duration.ofSeconds(30));
    this.es     = es;
  }

  @PostMapping("/query")
  public JsonNode handle(@RequestBody Map<String,Object> payload) throws Exception {
    String userQuery = (String) payload.get("query");

    List<ChatFunction> functions = List.of(
      ChatFunction.builder()
        .name("showUsersInCourse")
        .description("List all users enrolled in a given course. Requires a 'course' parameter with the course name.")
        .build(),
      ChatFunction.builder()
        .name("showUsersInMultipleCourses")
        .description("List all users registered for more than one course")
        .build(),
      ChatFunction.builder()
        .name("getClassAverageScore")
        .description("Compute the average score across all students and courses")
        .build(),
      ChatFunction.builder()
        .name("getCourseMaxScore")
        .description("Compute the maximum score for a given course. Requires a 'course' parameter with the course name.")
        .build()
    );

    ChatCompletionRequest req = ChatCompletionRequest.builder()
      .model("gpt-4o-mini")
      .messages(List.of(new ChatMessage("user", userQuery)))
      .functions(functions)
      .build();

    ChatCompletionResult res = openAi.createChatCompletion(req);
    ChatCompletionChoice choice = res.getChoices().get(0);
    ChatMessage msg = choice.getMessage();

    if (msg.getFunctionCall() != null) {
      String fname = msg.getFunctionCall().getName();
      JsonNode args = mapper.readTree(msg.getFunctionCall().getArguments().toString());

      return switch (fname) {
        case "showUsersInCourse" -> {
          JsonNode courseNode = args.get("course");
          if (courseNode == null || courseNode.isNull()) {
            yield mapper.createObjectNode().put("error", "Missing required parameter: course");
          }
          yield es.search("user", "users-in-course.json",
               new UserCourseParams(courseNode.asText()));
        }
        case "showUsersInMultipleCourses" ->
          es.search("user", "users-multiple-courses.json",
               new UserMultipleCoursesParams());
        case "getClassAverageScore" ->
          es.aggregate("user", "class-average.json",
               new ClassAverageParams());
        case "getCourseMaxScore" -> {
          JsonNode courseNode = args.get("course");
          if (courseNode == null || courseNode.isNull()) {
            yield mapper.createObjectNode().put("error", "Missing required parameter: course");
          }
          yield es.aggregate("user", "course-max.json",
               new CourseMaxParams(courseNode.asText()));
        }
        default ->
          throw new IllegalStateException("Unknown function: " + fname);
      };
    }

    return mapper.createObjectNode().put("error","Could not interpret query");
  }
}
