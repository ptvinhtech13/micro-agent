# REST API Design & Conventions

This document defines **MANDATORY** REST API standards for all CRUD operations across the MicroAgent platform.

---

## Table of Contents

1. [Resource Naming Conventions](#resource-naming-conventions)
2. [HTTP Methods for CRUD Operations](#http-methods-for-crud-operations)
3. [URL Structure & Patterns](#url-structure--patterns)
4. [Request & Response Conventions](#request--response-conventions)
5. [HTTP Status Codes](#http-status-codes)
6. [Error Response Structure](#error-response-structure)
7. [Pagination, Filtering, and Sorting](#pagination-filtering-and-sorting)
8. [Resource Relationships & Nested Resources](#resource-relationships--nested-resources)
9. [Special Operations & Custom Actions](#special-operations--custom-actions)

---

## Resource Naming Conventions

### General Rules
- ✅ **ALWAYS** use **plural nouns** for resource names (e.g., `/agents`, `/tasks`, `/capabilities`)
- ✅ **ALWAYS** use **kebab-case** for multi-word resources (e.g., `/agent-profiles`, `/task-executions`)
- ✅ **ALWAYS** use **lowercase** for URLs
- ❌ **NEVER** use verbs in URLs (e.g., `/createAgent`, `/getAgents`)
- ❌ **NEVER** use file extensions (e.g., `.json`, `.xml`)
- ❌ **NEVER** use underscores in URLs (use kebab-case instead)

```java
// ✅ Good
GET    /api/v1/agents
GET    /api/v1/agent-profiles
POST   /api/v1/task-executions

// ❌ Bad
GET    /api/v1/agent          // Singular
GET    /api/v1/getAgents      // Verb in URL
GET    /api/v1/agent_profiles // Underscore
GET    /api/v1/agents.json    // File extension
```

---

## HTTP Methods for CRUD Operations

### Standard CRUD Mapping

| Operation | HTTP Method | URL Pattern                  | Description                    |
|-----------|-------------|------------------------------|--------------------------------|
| Create    | POST        | `/api/v1/agents`             | Create a new agent             |
| Read One  | GET         | `/api/v1/agents/{id}`        | Get a single agent by ID       |
| Read All  | GET         | `/api/v1/agents`             | Get all agents (with pagination) |
| Update    | PUT         | `/api/v1/agents/{id}`        | Full update (replace entire resource) |
| Update    | PATCH       | `/api/v1/agents/{id}`        | Partial update (update specific fields) |
| Delete    | DELETE      | `/api/v1/agents/{id}`        | Delete an agent                |

### Method Usage Rules

- ✅ **ALWAYS** use **POST** for creating new resources
- ✅ **ALWAYS** use **GET** for retrieving resources (read-only, no side effects)
- ✅ **ALWAYS** use **PUT** for complete resource replacement
- ✅ **ALWAYS** use **PATCH** for partial updates
- ✅ **ALWAYS** use **DELETE** for removing resources
- ❌ **NEVER** use GET for operations that modify state
- ❌ **NEVER** use POST for updates (use PUT or PATCH)

```java
// ✅ Good - Proper HTTP method usage

public interface AgentApi {
    @PostMapping("/api/v1/agents")
    @ResponseStatus(HttpStatus.CREATED)
    AgentResponse createAgent(@Valid @RequestBody CreateAgentRequest request);

    @GetMapping("/api/v1/agents/{agentId}")
    @ResponseStatus(HttpStatus.OK)
    AgentResponse getAgent(@PathVariable String agentId);

    @GetMapping("/api/v1/agents")
    @ResponseStatus(HttpStatus.OK)
    PageResponse<AgentResponse> queryAgents(
          @RequestParam(defaultValue = "0") int page,
          @RequestParam(defaultValue = "50") int size,
          @RequestParam(defaultValue = "id") String[] sort,
          @RequestParam(defaultValue = "desc") String direction,
          @RequestParam("filter.byStatuses") List<Status> filterByStatuses,
          @RequestParam("filter.fromDateTime") Long filterFromDateTime,
          @RequestParam("filter.toDateTime") Long filterToDateTime);

    @PutMapping("/api/v1/agents/{agentId}")
    @ResponseStatus(HttpStatus.OK)
    AgentResponse updateAgent(
            @PathVariable String agentId,
            @Valid @RequestBody UpdateAgentRequest request);

    @PatchMapping("/api/v1/agents/{agentId}")
    @ResponseStatus(HttpStatus.OK)
    AgentResponse patchAgent(
            @PathVariable String agentId,
            @Valid @RequestBody PatchAgentRequest request);

    @DeleteMapping("/api/v1/agents/{agentId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    void deleteAgent(@PathVariable String agentId);
}

// ❌ Bad - Wrong HTTP methods
@GetMapping("/delete/{id}")  // BAD! Use DELETE method
public void deleteAgent(@PathVariable String id) { }

@PostMapping("/update/{id}")  // BAD! Use PUT or PATCH
public Agent updateAgent(@PathVariable String id) { }
```

---

## URL Structure & Patterns

### Base URL Structure
```
https://{domain}/api/{version}/{resource}/{id}/{sub-resource}/{sub-id}
```

### Versioning
- ✅ **ALWAYS** include API version in URL (e.g., `/api/v1/`)
- ✅ **ALWAYS** use `v{number}` format (e.g., `v1`, `v2`, `v3`)
- ✅ **ALWAYS** maintain backward compatibility within the same major version

```java
// ✅ Good - Versioned APIs
@RequestMapping("/api/v1/agents")
@RequestMapping("/api/v2/agents")  // New version with breaking changes

// ❌ Bad - No versioning
@RequestMapping("/api/agents")
```

### Path Parameters vs Query Parameters

- ✅ **ALWAYS** use **path parameters** for resource identifiers
- ✅ **ALWAYS** use **query parameters** for filtering, sorting, pagination, and search

```java
// ✅ Good - Proper parameter usage
GET /api/v1/agents/{agentId}                          // Path param for ID
GET /api/v1/agents?status=ACTIVE                      // Query param for filtering
GET /api/v1/agents?page=0&size=20&sort=createdAt,desc // Query params for pagination
GET /api/v1/agents/{agentId}/tasks/{taskId}           // Nested resources

// ❌ Bad - Misused parameters
GET /api/v1/agents?agentId=123                        // Should be path param
GET /api/v1/agents/status/ACTIVE                      // Should be query param
```

---

## Request & Response Conventions

### Request Body Standards

- ✅ **ALWAYS** use JSON as the default content type
- ✅ **ALWAYS** validate request bodies with `@Valid`
- ✅ **ALWAYS** use descriptive field names in camelCase
- ✅ **ALWAYS** include only necessary fields in requests
- ❌ **NEVER** include ID in creation requests (server generates IDs)
- ❌ **NEVER** include computed fields in requests

```java
// ✅ Good - Create Request
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateAgentRequest {
    @NotBlank(message = "Name is required")
    @Size(min = 3, max = 100)
    private String name;

    @NotBlank(message = "Type is required")
    private String type;

    @NotNull
    private List<String> capabilities;

    // No ID field - server generates it
}

// ✅ Good - Update Request (PUT - full update)
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateAgentRequest {
    @NotBlank
    private String name;

    @NotBlank
    private String type;

    @NotNull
    private List<String> capabilities;

    @NotNull
    private String status;

    // All fields required for full update
}

// ✅ Good - Patch Request (PATCH - partial update)
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PatchAgentRequest {
    // All fields optional for partial update
    private String name;
    private String type;
    private List<String> capabilities;
    private String status;
}

// ❌ Bad - Including ID in create request
public class CreateAgentRequest {
    private String id;  // BAD! Server should generate
    private String name;
}
```

### Response Body Standards

- ✅ **ALWAYS** return consistent response structure
- ✅ **ALWAYS** include resource ID in responses
- ✅ **ALWAYS** include timestamps (createdAt, updatedAt)
- ✅ **ALWAYS** use camelCase for field names

```java
// ✅ Good - Response DTO
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AgentResponse {
    private String agentId;
    private String name;
    private String type;
    private String status;
    private List<String> capabilities;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String createdBy;
    private String updatedBy;
}
```

---

## HTTP Status Codes

### Standard Status Codes for CRUD Operations

| Operation | Success Status | Description                           |
|-----------|----------------|---------------------------------------|
| POST      | 201 Created    | Resource created successfully         |
| GET       | 200 OK         | Resource(s) retrieved successfully    |
| PUT       | 200 OK         | Resource updated successfully         |
| PATCH     | 200 OK         | Resource partially updated            |
| DELETE    | 204 No Content | Resource deleted successfully         |

### Error Status Codes

| Status Code | Usage                                    |
|-------------|------------------------------------------|
| 400         | Bad Request (validation errors)          |
| 401         | Unauthorized (authentication required)   |
| 403         | Forbidden (insufficient permissions)     |
| 404         | Not Found (resource doesn't exist)       |
| 409         | Conflict (duplicate resource)            |
| 422         | Unprocessable Entity (business rule violation) |
| 500         | Internal Server Error                    |

### Usage Rules

- ✅ **ALWAYS** use `@ResponseStatus` annotation to specify HTTP status explicitly
- ✅ **ALWAYS** return 201 Created for POST operations
- ✅ **ALWAYS** return 204 No Content for successful DELETE operations
- ✅ **ALWAYS** return 404 Not Found when resource doesn't exist
- ✅ **ALWAYS** return 409 Conflict for duplicate resources
- ❌ **NEVER** return 200 OK for create operations (use 201)

```java
// ✅ Good - Explicit status codes
@PostMapping
@ResponseStatus(HttpStatus.CREATED)
public AgentResponse createAgent(@Valid @RequestBody CreateAgentRequest request) { }

@DeleteMapping("/{id}")
@ResponseStatus(HttpStatus.NO_CONTENT)
public void deleteAgent(@PathVariable String id) { }

// ❌ Bad - Missing status annotation
@PostMapping
public AgentResponse createAgent(@Valid @RequestBody CreateAgentRequest request) { }
```

---

## Error Response Structure

### Consistent Error Format

- ✅ **ALWAYS** return consistent error structure
- ✅ **ALWAYS** include error code, message, and timestamp
- ✅ **ALWAYS** include field-level errors for validation failures
- ❌ **NEVER** expose sensitive information (stack traces, database details)

```java
// ✅ Good - Standard Error Response
@Data
@Builder
public class ErrorResponse {
    private String errorCode;
    private String message;
    private LocalDateTime timestamp;
    private String path;  // Optional: request path
}

// Example response:
{
  "errorCode": "AGENT_NOT_FOUND",
  "message": "Agent not found with ID: 550e8400-e29b-41d4-a716-446655440000",
  "timestamp": "2026-01-11T10:30:00Z",
  "path": "/api/v1/agents/550e8400-e29b-41d4-a716-446655440000"
}

// ✅ Good - Validation Error Response
@Data
@Builder
public class ValidationErrorResponse {
    private String errorCode;
    private String message;
    private List<FieldError> fieldErrors;
    private LocalDateTime timestamp;
    private String path;
}

@Data
@Builder
public class FieldError {
    private String field;
    private String message;
}

// Example response:
{
  "errorCode": "VALIDATION_ERROR",
  "message": "Request validation failed",
  "fieldErrors": [
    {
      "field": "name",
      "message": "Agent name is required"
    },
    {
      "field": "capabilities",
      "message": "At least one capability is required"
    }
  ],
  "timestamp": "2026-01-11T10:30:00Z",
  "path": "/api/v1/agents"
}

// ❌ Bad - Inconsistent error responses
public String handleError(Exception e) {
    return e.getMessage();  // Just a string, not structured
}

public ResponseEntity<Map<String, Object>> handleError(Exception e) {
    return ResponseEntity.ok(Map.of("error", e.toString()));  // Exposes stack trace
}
```

---

## Pagination, Filtering, and Sorting

### Pagination Standards

- ✅ **ALWAYS** use pagination for list endpoints
- ✅ **ALWAYS** use `page` (0-based), `size`, `sort` (array of field names), and `direction` (asc/desc)
- ✅ **ALWAYS** provide sensible defaults (page=0, size=20 or size=50)
- ✅ **ALWAYS** return `PageResponse` object with metadata

```java
// ✅ Good - Pagination implementation
@GetMapping
@ResponseStatus(HttpStatus.OK)
public PageResponse<AgentResponse> queryAgents(
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "20") int size,
        @RequestParam(defaultValue = "createdAt") String[] sort,
        @RequestParam(defaultValue = "desc") String direction,
        @RequestParam(required = false) String status,
        @RequestParam(required = false) String domain,
        @RequestParam(required = false) String ownerId) {
    // Implementation
}

// Example requests:
GET /api/v1/agents?page=0&size=20
GET /api/v1/agents?page=0&size=20&sort=createdAt&direction=desc
GET /api/v1/agents?page=1&size=50&sort=name&direction=asc
GET /api/v1/agents?status=ACTIVE&domain=ecommerce&page=0&size=20

// ✅ Good - PageResponse structure
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PageResponse<T> {
    private List<T> content;        // List of items in current page
    private long totalElements;     // Total number of items across all pages
    private int currentPage;        // Current page number (0-indexed)
    private int totalPages;         // Total number of pages
}

// Example response:
{
  "content": [
    {
      "agentId": "550e8400-e29b-41d4-a716-446655440000",
      "name": "customer-support-bot",
      "status": "ACTIVE"
    },
    {
      "agentId": "660e8400-e29b-41d4-a716-446655440001",
      "name": "banking-assistant",
      "status": "ACTIVE"
    }
  ],
  "totalElements": 45,
  "currentPage": 0,
  "totalPages": 3
}

// ❌ Bad - No pagination
@GetMapping
public List<AgentResponse> getAllAgents() {
    return agentService.findAll();  // Could return millions of records
}
```

### Filtering Standards

- ✅ **ALWAYS** use query parameters for filtering
- ✅ **ALWAYS** prefix filter parameters with `filter.by{FieldName}`
- ✅ **ALWAYS** use plural form for array filters (e.g., `filter.byStatuses`, `filter.byNames`)
- ✅ **ALWAYS** use camelCase after the `filter.by` prefix
- ✅ **ALWAYS** support multiple filter combinations
- ✅ **ALWAYS** validate filter values

#### Filter Parameter Naming Convention

**Pattern**: `filter.by{FieldName}` where `{FieldName}` is the field being filtered

**Rules**:
- Single value filters: `filter.byStatus`, `filter.byName`, `filter.byDomain`
- Array/multiple value filters: Use plural form - `filter.byStatuses`, `filter.byNames`, `filter.byTags`
- Nested field filters: `filter.byOwnerType`, `filter.byCreatedBy`
- Date range filters: `filter.byFromDate`, `filter.byToDate`

```java
// ✅ Good - Filtering with standardized parameters

// Single value filters
GET /api/v1/agents?filter.byStatus=ACTIVE
GET /api/v1/agents?filter.byDomain=ecommerce
GET /api/v1/agents?filter.byOwnerId=user-123

// Array filters (plural form)
GET /api/v1/agents?filter.byStatuses=ACTIVE,INACTIVE
GET /api/v1/agents?filter.byTags=tag-uuid-1,tag-uuid-2
GET /api/v1/agents?filter.byDomains=ecommerce,banking

// Combined filters
GET /api/v1/agents?filter.byStatuses=ACTIVE,INACTIVE&filter.byDomain=ecommerce
GET /api/v1/agents?filter.byOwnerId=user-123&filter.byStatuses=ACTIVE

// Date range filters
GET /api/v1/agents?filter.byFromDate=2026-01-01&filter.byToDate=2026-01-31
GET /api/v1/tasks?filter.byCreatedFrom=2026-01-01T00:00:00Z&filter.byCreatedTo=2026-01-31T23:59:59Z

// Pagination + Filtering + Sorting (complete example)
GET /api/v1/agents?page=0&size=20&sort=createdAt&direction=desc&filter.byStatuses=ACTIVE,INACTIVE&filter.byDomain=ecommerce

// ❌ Bad - Incorrect filter parameter naming
GET /api/v1/agents?status=ACTIVE              // Missing "filter.by" prefix
GET /api/v1/agents?filterStatus=ACTIVE        // Wrong format
GET /api/v1/agents?filter.status=ACTIVE       // Missing "by" after "filter"
GET /api/v1/agents?filter.byStatus=ACTIVE,INACTIVE  // Should be "byStatuses" (plural) for multiple values
```

#### Controller Implementation Example

```java
// ✅ Good - Controller with standardized filter parameters
@GetMapping("/api/v1/agents")
@ResponseStatus(HttpStatus.OK)
public PageResponse<AgentResponse> queryAgents(
        // Pagination parameters
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "20") int size,

        // Sorting parameters
        @RequestParam(defaultValue = "createdAt") String[] sort,
        @RequestParam(defaultValue = "desc") String direction,

        // Filter parameters (using "filter.by" prefix)
        @RequestParam(value = "filter.byStatuses", required = false) List<AgentStatus> filterByStatuses,
        @RequestParam(value = "filter.byDomain", required = false) String filterByDomain,
        @RequestParam(value = "filter.byOwnerId", required = false) String filterByOwnerId,
        @RequestParam(value = "filter.byTags", required = false) List<String> filterByTags,
        @RequestParam(value = "filter.byFromDate", required = false) LocalDate filterByFromDate,
        @RequestParam(value = "filter.byToDate", required = false) LocalDate filterByToDate) {

    // Build query and execute
    AgentQueryParams queryParams = AgentQueryParams.builder()
            .page(page)
            .size(size)
            .sort(sort)
            .direction(direction)
            .statuses(filterByStatuses)
            .domain(filterByDomain)
            .ownerId(filterByOwnerId)
            .tags(filterByTags)
            .fromDate(filterByFromDate)
            .toDate(filterByToDate)
            .build();

    return agentQueryService.queryAgents(queryParams);
}

// ❌ Bad - Inconsistent filter parameter naming
@GetMapping("/api/v1/agents")
public PageResponse<AgentResponse> queryAgents(
        @RequestParam(required = false) String status,           // Missing prefix
        @RequestParam(required = false) String domain,           // Missing prefix
        @RequestParam(required = false) List<String> tags) {    // Missing prefix
    // ...
}
```

### Sorting Standards

- ✅ **ALWAYS** support sorting on common fields (createdAt, updatedAt, name)
- ✅ **ALWAYS** use `sort` parameter for field name
- ✅ **ALWAYS** use `direction` parameter (asc/desc)
- ✅ **ALWAYS** provide default sorting

```java
// ✅ Good - Sorting examples
GET /api/v1/agents?sort=createdAt&direction=desc
GET /api/v1/agents?sort=name&direction=asc
GET /api/v1/agents?sort=updatedAt&direction=desc
```

---

## Resource Relationships & Nested Resources

### Parent-Child Relationship Patterns

- ✅ **ALWAYS** use nested URLs for parent-child relationships
- ✅ **ALWAYS** limit nesting to 2-3 levels maximum
- ✅ **ALWAYS** validate parent resource exists before accessing children

```java
// ✅ Good - Nested resource endpoints
public interface AgentTaskApi {
    // Get specific task of an agent
    @GetMapping("/api/v1/agents/{agentId}/tasks/{taskId}")
    @ResponseStatus(HttpStatus.OK)
    TaskResponse getAgentTask(
            @PathVariable String agentId,
            @PathVariable String taskId);

    // Create task for an agent
    @PostMapping("/api/v1/agents/{agentId}/tasks")
    @ResponseStatus(HttpStatus.CREATED)
    TaskResponse createAgentTask(
            @PathVariable String agentId,
            @Valid @RequestBody CreateTaskRequest request);

    // Update agent task
    @PutMapping("/api/v1/agents/{agentId}/tasks/{taskId}")
    @ResponseStatus(HttpStatus.OK)
    TaskResponse updateAgentTask(
            @PathVariable String agentId,
            @PathVariable String taskId,
            @Valid @RequestBody UpdateTaskRequest request);

    // Delete agent task
    @DeleteMapping("/api/v1/agents/{agentId}/tasks/{taskId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    void deleteAgentTask(
            @PathVariable String agentId,
            @PathVariable String taskId);

    // List all tasks for an agent (with pagination)
    @GetMapping("/api/v1/agents/{agentId}/tasks")
    @ResponseStatus(HttpStatus.OK)
    PageResponse<TaskResponse> queryAgentTasks(
            @PathVariable String agentId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "createdAt") String[] sort,
            @RequestParam(defaultValue = "desc") String direction);
}

// ❌ Bad - Too deep nesting (more than 3 levels)
GET /api/v1/agents/{agentId}/tasks/{taskId}/executions/{executionId}/logs/{logId}/details/{detailId}
// Instead, use:
GET /api/v1/task-executions/{executionId}/logs/{logId}/details/{detailId}

// ❌ Bad - Not validating parent exists
@GetMapping("/api/v1/agents/{agentId}/tasks/{taskId}")
public TaskResponse getTask(@PathVariable String agentId, @PathVariable String taskId) {
    // Only checking if task exists, not if it belongs to the agent
    return taskService.findById(taskId);
}
```

### Many-to-Many Relationships

- ✅ **ALWAYS** represent many-to-many relationships with dedicated endpoints
- ✅ **ALWAYS** use verb phrases for relationship operations (e.g., `assign`, `remove`)

```java
// ✅ Good - Many-to-many relationship management
public interface AgentCapabilityApi {
    // Get all capabilities for an agent
    @GetMapping("/api/v1/agents/{agentId}/capabilities")
    @ResponseStatus(HttpStatus.OK)
    List<CapabilityResponse> getAgentCapabilities(@PathVariable String agentId);

    // Assign capabilities to an agent
    @PostMapping("/api/v1/agents/{agentId}/capabilities")
    @ResponseStatus(HttpStatus.OK)
    void assignCapabilities(
            @PathVariable String agentId,
            @Valid @RequestBody AssignCapabilitiesRequest request);

    // Remove capability from an agent
    @DeleteMapping("/api/v1/agents/{agentId}/capabilities/{capabilityId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    void removeCapability(
            @PathVariable String agentId,
            @PathVariable String capabilityId);
}

// ✅ Good - Request DTO for assignment
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AssignCapabilitiesRequest {
    @NotNull
    @Size(min = 1, message = "At least one capability must be assigned")
    private List<String> capabilityIds;
}
```

---

## Special Operations & Custom Actions

### Custom Actions Beyond CRUD

- ✅ **ALWAYS** use POST for custom actions that change state
- ✅ **ALWAYS** use clear verb names for action endpoints
- ✅ **ALWAYS** place action endpoints under the resource: `/{resource}/{id}/{action}`
- ✅ **ALWAYS** use `@ResponseStatus` to specify HTTP status code

```java
// ✅ Good - Custom action endpoints
public interface AgentActionApi {
    // Activate an agent
    @PostMapping("/api/v1/agents/{agentId}/activate")
    @ResponseStatus(HttpStatus.OK)
    AgentResponse activateAgent(@PathVariable String agentId);

    // Deactivate an agent
    @PostMapping("/api/v1/agents/{agentId}/deactivate")
    @ResponseStatus(HttpStatus.OK)
    AgentResponse deactivateAgent(@PathVariable String agentId);

    // Execute a task on an agent
    @PostMapping("/api/v1/agents/{agentId}/execute")
    @ResponseStatus(HttpStatus.OK)
    TaskExecutionResponse executeTask(
            @PathVariable String agentId,
            @Valid @RequestBody ExecuteTaskRequest request);

    // Bulk activate agents
    @PostMapping("/api/v1/agents/bulk-activate")
    @ResponseStatus(HttpStatus.OK)
    BulkOperationResponse bulkActivate(
            @Valid @RequestBody BulkActivateRequest request);
}

// ❌ Bad - Using GET for state-changing operations
@GetMapping("/{id}/activate")  // BAD! GET should not change state
public Agent activate(@PathVariable String id) { }

// ❌ Bad - Unclear action naming
@PostMapping("/{id}/do-something")  // BAD! Not descriptive
public Agent doSomething(@PathVariable String id) { }
```

---

## API Design Checklist

When designing a new API, ensure you:

### Resource Design
- [ ] Use plural nouns for resource names
- [ ] Use kebab-case for multi-word resources
- [ ] Use lowercase URLs
- [ ] Include API versioning (e.g., `/api/v1/`)

### HTTP Methods
- [ ] Use POST for creating resources (return 201 Created)
- [ ] Use GET for reading resources (return 200 OK)
- [ ] Use PUT for full updates (return 200 OK)
- [ ] Use PATCH for partial updates (return 200 OK)
- [ ] Use DELETE for removing resources (return 204 No Content)

### Request/Response
- [ ] Validate all request bodies with `@Valid`
- [ ] Use camelCase for field names
- [ ] Don't include ID in create requests
- [ ] Include timestamps in responses (createdAt, updatedAt)

### Pagination & Filtering
- [ ] Implement pagination for list endpoints
- [ ] Use `page`, `size`, `sort`, `direction` parameters
- [ ] Return PageResponse with metadata
- [ ] Provide sensible defaults (page=0, size=20)
- [ ] Use `filter.by{FieldName}` prefix for all filter parameters
- [ ] Use plural form for array filters (filter.byStatuses, filter.byTags)
- [ ] Use singular form for single value filters (filter.byStatus, filter.byDomain)

### Error Handling
- [ ] Return consistent error response structure
- [ ] Include error code, message, timestamp
- [ ] Use appropriate HTTP status codes
- [ ] Don't expose sensitive information

### Status Codes
- [ ] Use `@ResponseStatus` annotation explicitly
- [ ] Return 201 for POST operations
- [ ] Return 204 for DELETE operations
- [ ] Return 404 when resource not found
- [ ] Return 409 for conflicts

### Documentation
- [ ] Document all endpoints in API interface
- [ ] Include Javadoc for complex operations
- [ ] Provide example requests/responses
- [ ] Document error scenarios

---

**Last Updated**: 2026-01-11
**Based on**: MicroAgent platform standards
