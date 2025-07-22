# Elasticsearch REST Interface Demo

This project provides a comprehensive REST interface for Elasticsearch operations with template-based query support.

## Additional Features

- **Lombok Integration**: Reduced boilerplate code with automatic getter/setter generation
- **Mustache Templating**: Dynamic query generation with variable substitution
- **YAML Configuration**: Human-readable template definitions
- **Comprehensive Testing**: Unit and integration tests with mock data
- **Error Handling**: Global exception handling for robust API responses
- **Index Management**: Create, delete, and manage Elasticsearch indices
- **Bulk Operations**: Support for bulk document operations

## Configuration

The application can be configured via `application.yml`:

```yaml
elasticsearch:
  host: localhost
  port: 9200

app:
  query:
    template-path: classpath:query-templates/
```

## Example Usage

1. **Start the application and Elasticsearch**
2. **Create a user index with sample data**:
   ```bash
   # Create index
   PUT http://localhost:8080/api/elasticsearch/user
   
   # Add sample user
   POST http://localhost:8080/api/elasticsearch/user/document
   {
     "userId": 1,
     "userName": "John Doe",
     "courses": ["Math", "Science"],
     "grades": [
       {"course": "Math", "score": 85.5},
       {"course": "Science", "score": 92.0}
     ]
   }
   ```

3. **Use template-based queries**:
   ```bash
   # Search by user ID
   POST http://localhost:8080/api/elasticsearch/template/USER_OPERATIONS/searchByUserId
   {
     "userId": 1
   }
   
   # Get grade statistics
   POST http://localhost:8080/api/elasticsearch/template/USER_OPERATIONS/aggregateGradesByCourse
   {}
   ```

## Contributing

1. Fork the repository
2. Create a feature branch
3. Add tests for new functionality
4. Ensure all tests pass
5. Submit a pull request

## License

This project is licensed under the MIT License.

- **CRUD Operations**: Create, Read, Update, Delete documents
- **Search Operations**: Full-text search, term queries, range queries
- **Multi-Search**: Execute multiple search queries in a single request
- **Aggregations**: Statistical aggregations and data analysis
- **Template-based Queries**: YAML-configured query templates with variable substitution
- **Raw Query Execution**: Direct Elasticsearch query execution

## Prerequisites

- Java 17+
- Maven
- Elasticsearch 8.x running on localhost:9200

## Getting Started

1. **Start Elasticsearch**:
   ```bash
   # Using Docker
   docker run -d --name elasticsearch -p 9200:9200 -e "discovery.type=single-node" -e "xpack.security.enabled=false" elasticsearch:8.7.0
   ```

2. **Build and Run the Application**:
   ```bash
   mvn clean install
   mvn spring-boot:run
   ```

3. **The application will start on port 8080**

## REST API

### CRUD Operations

#### Create Document
```bash
# Create document with auto-generated ID
POST /api/elasticsearch/{indexName}/document
Content-Type: application/json

{
  "userId": 1,
  "userName": "John Doe",
  "courses": ["Math", "Science"],
  "grades": [
    {"course": "Math", "score": 85.5},
    {"course": "Science", "score": 92.0}
  ]
}

# Create document with specific ID
POST /api/elasticsearch/{indexName}/document/{documentId}
```

#### Read Document
```bash
GET /api/elasticsearch/{indexName}/document/{documentId}
```

#### Update Document
```bash
# Full update
PUT /api/elasticsearch/{indexName}/document/{documentId}
Content-Type: application/json

{
  "userId": 1,
  "userName": "John Smith",
  "courses": ["Math", "Science", "History"],
  "grades": [
    {"course": "Math", "score": 88.0},
    {"course": "Science", "score": 92.0},
    {"course": "History", "score": 85.0}
  ]
}

# Partial update
PATCH /api/elasticsearch/{indexName}/document/{documentId}
Content-Type: application/json

{
  "userName": "John Smith"
}
```

#### Delete Document
```bash
DELETE /api/elasticsearch/{indexName}/document/{documentId}
```

### Search Operations

#### Basic Search
```bash
POST /api/elasticsearch/{indexName}/search
Content-Type: application/json

{
  "query": {
    "match": {
      "userName": "John"
    }
  }
}
```

#### Multi-Search
```bash
POST /api/elasticsearch/{indexName}/msearch
Content-Type: application/x-ndjson

{"index": "user"}
{"query": {"term": {"userId": 1}}}
{"index": "user"}
{"query": {"term": {"userId": 2}}}
```

#### Aggregations
```bash
POST /api/elasticsearch/{indexName}/aggregate
Content-Type: application/json

{
  "size": 0,
  "aggs": {
    "course_enrollment": {
      "terms": {
        "field": "courses.keyword"
      }
    }
  }
}
```

### Template-based Operations

#### Execute Template
```bash
POST /api/elasticsearch/template/{operationType}/{templateName}
Content-Type: application/json

{
  "userId": 1
}
```

### Available Templates (USER_OPERATIONS)

1. **searchByUserId** - Search by user ID
2. **searchByUserName** - Search by user name
3. **searchByCourse** - Search users enrolled in specific courses
4. **searchByGradeRange** - Search users with grades in a specific range
5. **multiSearchUsers** - Multi-search for multiple users
6. **aggregateGradesByCourse** - Aggregate grades by course
7. **aggregateUsersByCourse** - Aggregate user enrollment by course
8. **aggregateGradeDistribution** - Grade distribution for a specific course

### Template Examples

#### Search by User ID
```bash
POST /api/elasticsearch/template/USER_OPERATIONS/searchByUserId
Content-Type: application/json

{
  "userId": 1
}
```

#### Search by Grade Range
```bash
POST /api/elasticsearch/template/USER_OPERATIONS/searchByGradeRange
Content-Type: application/json

{
  "course": "Math",
  "minScore": 80.0,
  "maxScore": 90.0
}
```

#### Aggregate Grades by Course
```bash
POST /api/elasticsearch/template/USER_OPERATIONS/aggregateGradesByCourse
Content-Type: application/json

{}
```

## Template Configuration

Templates are defined in YAML files under `src/main/resources/query-templates/`. Each template supports:

- **Variable substitution** using Mustache syntax (`${variableName}`)
- **Multiple query types**: search, agg, msearch
- **Flexible parameter mapping**

### Example Template Structure

```yaml
operationType: USER_OPERATIONS
templates:
  - name: searchByUserId
    queryType: search
    baseQuery:
      query:
        term:
          userId: "${userId}"
  
  - name: aggregateGradesByCourse
    queryType: agg
    baseQuery:
      size: 0
      aggs:
        courses:
          nested:
            path: grades
          aggs:
            course_stats:
              terms:
                field: grades.course.keyword
```

## Running Tests

```bash
# Run all tests
mvn test

# Run integration tests specifically
mvn test -Dtest=UserTemplateIntegrationTest
```

## Project Structure

```
src/
├── main/
│   ├── java/
│   │   └── com/example/elasticsearch/
│   │       ├── controller/          # REST controllers
│   │       ├── service/             # Business logic
│   │       ├── query/               # Query parameter classes
│   │       ├── model/               # Data models
│   │       └── config/              # Configuration
│   └── resources/
│       ├── query-templates/         # YAML query templates
│       └── schemas/                 # JSON schemas
└── test/
    └── java/
        └── com/example/elasticsearch/
            ├── integration/         # Integration tests
            ├── controller/          # Controller tests
            └── service/             # Service tests
```

## Key Components

- **ElasticsearchController**: Main REST API endpoints
- **ElasticsearchService**: Core Elasticsearch operations
- **QueryTemplateService**: Template processing and execution
- **UserTemplateParams**: Parameter class with Lombok annotations
- **QueryTemplate**: YAML template model

## Features
