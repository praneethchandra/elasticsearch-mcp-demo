# Elasticsearch MCP Demo

A Spring Boot application that demonstrates integration between Elasticsearch and OpenAI's Model Context Protocol (MCP) for intelligent querying of student data. This application provides both traditional REST APIs and an AI-powered natural language query interface.

## What is this application?

This application is a demonstration of how to combine Elasticsearch's powerful search capabilities with OpenAI's function calling to create an intelligent data querying system. It manages student data including user information, course enrollments, and grades, providing multiple ways to interact with the data:

1. **Traditional REST APIs** - Direct endpoints for specific queries
2. **AI-Powered Natural Language Interface** - Uses OpenAI GPT-4o-mini to interpret natural language queries and execute appropriate Elasticsearch operations

### Key Features

- **Template-based Elasticsearch queries** using Mustache templating
- **JSON Schema validation** for data integrity
- **OpenAI function calling** for natural language query interpretation
- **Comprehensive test coverage** including integration tests with Testcontainers
- **Auto-generated POJOs** from JSON schemas

### Data Model

The application works with user data structured as follows:
- **userId**: Unique identifier for each user
- **userName**: User's name
- **courses**: Array of course names the user is enrolled in
- **grades**: Array of grade objects containing course name and score

## Prerequisites

- Java 17 or higher
- Maven 3.6+
- Elasticsearch 8.7+ (running locally or accessible via network)
- OpenAI API key (for MCP functionality)

## How to Build

1. **Clone the repository**
   ```bash
   git clone <repository-url>
   cd elasticsearch-mcp-demo
   ```

2. **Generate POJOs from JSON schemas**
   ```bash
   mvn generate-sources
   ```

3. **Compile the application**
   ```bash
   mvn compile
   ```

4. **Build the complete application**
   ```bash
   mvn clean package
   ```

5. **Run tests**
   ```bash
   mvn test
   ```

## Configuration

### Environment Variables

Set the following environment variables before running the application:

```bash
# Elasticsearch connection (optional, defaults to localhost:9200)
export SPRING_ELASTICSEARCH_REST_URIS=http://localhost:9200

# OpenAI API key (required for MCP functionality)
export OPENAI_API_KEY=your-openai-api-key-here
```

### Application Configuration

The application uses `src/main/resources/application.yml` for configuration:

```yaml
spring:
  elasticsearch:
    rest:
      uris: ${SPRING_ELASTICSEARCH_REST_URIS:http://localhost:9200}
app:
  query:
    template-path: classpath:es-templates/
```

## How to Run

### Start Elasticsearch

Make sure Elasticsearch is running. You can use Docker:

```bash
docker run -d \
  --name elasticsearch \
  -p 9200:9200 \
  -p 9300:9300 \
  -e "discovery.type=single-node" \
  -e "xpack.security.enabled=false" \
  elasticsearch:8.7.0
```

### Run the Application

1. **Using Maven**
   ```bash
   mvn spring-boot:run
   ```

2. **Using the JAR file**
   ```bash
   java -jar target/elasticsearch-mcp-demo-0.0.1-SNAPSHOT.jar
   ```

The application will start on `http://localhost:8080`

## API Endpoints

### Traditional REST APIs (`/api/users`)

#### 1. Get User by ID
```http
GET /api/users/by-id/{userId}
```
**Description**: Retrieve a specific user by their ID
**Example**: `GET /api/users/by-id/123`

#### 2. Search Users by Name
```http
GET /api/users/search?userName={userName}
```
**Description**: Search for users by name using multi-search
**Example**: `GET /api/users/search?userName=John`

#### 3. Get User Statistics
```http
GET /api/users/stats/{userId}
```
**Description**: Get aggregated statistics for a specific user
**Example**: `GET /api/users/stats/123`

### AI-Powered MCP Interface (`/mcp`)

#### Natural Language Query Endpoint
```http
POST /mcp/query
Content-Type: application/json

{
  "query": "Show me all users in the Mathematics course"
}
```

**Supported Natural Language Queries**:

1. **List users in a course**
   ```json
   {"query": "Show me all users enrolled in Physics"}
   {"query": "Who is taking Mathematics?"}
   ```

2. **Find users in multiple courses**
   ```json
   {"query": "Show users registered for more than one course"}
   {"query": "Which students are taking multiple classes?"}
   ```

3. **Get class average score**
   ```json
   {"query": "What's the average score across all students?"}
   {"query": "Calculate the class average"}
   ```

4. **Get maximum score for a course**
   ```json
   {"query": "What's the highest score in Chemistry?"}
   {"query": "Show me the maximum grade for Biology"}
   ```

## Query Templates

The application uses Mustache templates for Elasticsearch queries located in `src/main/resources/es-templates/user/`:

- `simple-search.json` - Basic user lookup by ID
- `multi-search.msearch` - Multi-search for user names
- `agg-count-sum.json` - User statistics aggregation
- `users-in-course.json` - Find users enrolled in a specific course
- `users-multiple-courses.json` - Find users in multiple courses
- `class-average.json` - Calculate average scores
- `course-max.json` - Find maximum score for a course

## Development

### Project Structure

```
src/
├── main/
│   ├── java/com/example/elasticsearch/
│   │   ├── controller/          # REST controllers
│   │   ├── service/            # Business logic
│   │   ├── config/             # Configuration classes
│   │   └── query/              # Query parameter classes
│   └── resources/
│       ├── es-templates/       # Elasticsearch query templates
│       └── schemas/            # JSON schemas
└── test/                       # Test classes
```

### Adding New Query Templates

1. Create a new Mustache template in `src/main/resources/es-templates/user/`
2. Create corresponding parameter class in `com.example.elasticsearch.query`
3. Add endpoint in appropriate controller
4. Add function definition in `McpController` if needed for AI interface

### Running Integration Tests

The project includes integration tests using Testcontainers:

```bash
mvn test -Dtest=*IntegrationTest
```

## Troubleshooting

### Common Issues

1. **Elasticsearch Connection Failed**
   - Ensure Elasticsearch is running on the configured port
   - Check the `SPRING_ELASTICSEARCH_REST_URIS` environment variable

2. **OpenAI API Errors**
   - Verify your `OPENAI_API_KEY` is set correctly
   - Check your OpenAI account has sufficient credits

3. **Schema Generation Issues**
   - Run `mvn generate-sources` to regenerate POJOs from schemas
   - Ensure JSON schemas in `src/main/resources/schemas/` are valid

### Logs

Enable debug logging by adding to `application.yml`:
```yaml
logging:
  level:
    com.example.elasticsearch: DEBUG
    org.elasticsearch: DEBUG
```

## Contributing

1. Fork the repository
2. Create a feature branch
3. Add tests for new functionality
4. Ensure all tests pass
5. Submit a pull request

## License

This project is a demonstration application for educational purposes.
