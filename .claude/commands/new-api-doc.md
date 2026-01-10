# API Documentation Generator

**Purpose**: Generate comprehensive API documentation with sequence diagrams, pseudocode, and detailed processing rules.

**Architecture**: Microservices with Edge layer authentication and optional Kafka-based audit logging.

---

## Instructions for Claude

When this command is invoked:
1. Ask the user questions Q1-Q10 (see below)
2. Use the user's answers to fill in the FULL TEMPLATE (see bottom of this file)
3. Write the complete documentation to the path specified in Q10
4. DO NOT reference external files - everything is self-contained here

---

## Questions to Ask the User

### Q1: Audit Support
**Should this API include audit logging?**

Options:
- **Yes**: Full audit architecture (Edge Gateway → Kafka → Audit Consumer)
- **No**: Simple architecture without audit (suitable for read-only or non-critical APIs)

### Q2: API Name
**What is the API name?**

Examples:
- "Create Agent Profile API"
- "Get Agent Profile API"
- "Update Agent Status API"

### Q3: Microservice
**Which microservice does this belong to?**

Examples:
- "Agent Registry"
- "Agent Policy"
- "Agent Order Service"

### Q4: Endpoint
**HTTP method and endpoint path?**

Examples:
- `POST /api/v1/agents`
- `GET /api/v1/agents/{agentId}/profile`
- `PATCH /api/v1/agents/{agentId}/status`

### Q5: Purpose
**One-line purpose?**

Example: "Retrieve a single agent profile by agent ID"

**Detailed description (optional, 1-3 sentences)?**

Example: "This API retrieves a basic agent profile by its unique identifier, including core metadata such as version, ownership information, and status."

### Q6: Processing Steps
**List all processing steps with details:**

For each step provide:
- Step number and name
- Component responsible
- Actions performed
- Error conditions

Example format:
```
Step 1: Validate Request (AgentController)
- Validate request schema
- Validate required fields
Errors: 400 Bad Request

Step 2: Business Logic (AgentService)
- Check name uniqueness
- Query database
Errors: 409 AGENT_ALREADY_EXISTS

Step 3: Database Operation (AgentRepository)
- INSERT/UPDATE/DELETE operation
Errors: 500 DATABASE_ERROR

Step 4: Build Response (AgentController)
- Map to DTO
- Return 200/201
```

### Q7: Validation Rules
**List validation rules with error codes:**

Format: `Rule Name | Description | Error Code (HTTP Status)`

Example:
```
Valid UUID | agentId must be valid UUID | INVALID_AGENT_ID_FORMAT (400)
Agent Exists | Agent must exist in database | AGENT_NOT_FOUND (404)
```

### Q8: Database Operations
**What database operations?**

Provide:
1. Tables affected
2. SQL operations
3. Transaction scope (if any)

Example:
```
Tables: agents

Operations:
SELECT * FROM agents WHERE agent_id = ?

Transaction: None (read-only)
```

### Q9: Timing Expectations
**Expected timings per step?**

Format: `Step Name | Typical Duration | Timeout`

Example:
```
Request Validation | 10-20ms | 2s
Database Query | 30-80ms | 5s
Response Building | 10-20ms | 1s
Total | 50-120ms | 10s
```

### Q10: Output Location
**Where to save? (full absolute path)**

Example: `/Users/vinhpham/Documents/Personal/microagent/docs/agent-registry/[V1] Get Agent Profile API/README.md`

---

## FULL TEMPLATE STRUCTURE

Use this complete template structure when generating documentation. Fill in based on user answers.

---

## === TEMPLATE START (WITH AUDIT) ===

Use this template if Q1 = "Yes" (audit enabled)

````markdown
# [V1] {{API_NAME}}

## Overview

**Endpoint**: `{{HTTP_METHOD}} {{ENDPOINT_PATH}}`
**Purpose**: {{PURPOSE}}
**Version**: 1.0.0
**Last Updated**: {{CURRENT_DATE}}

{{DETAILED_DESCRIPTION}}

---

## Processing Rule - Sequence Diagram

### Full Request Flow

This diagram illustrates the complete processing flow for {{API_NAME_LOWER}}, including all validation steps, business logic, and data persistence operations.

```mermaid
sequenceDiagram
    participant Client
    participant Edge as Edge Gateway<br/>(Spring Filter)
    participant Kafka as Kafka Topic<br/>(audit-events)
    participant {{CONTROLLER}} as {{CONTROLLER_DISPLAY}}
    participant {{SERVICE}} as {{SERVICE_DISPLAY}}
    participant {{REPOSITORY}} as {{REPOSITORY_DISPLAY}}
    participant Database as PostgreSQL DB
    participant AuditConsumer as Audit Service<br/>(Kafka Consumer)

    Client->>Edge: {{HTTP_METHOD}} {{ENDPOINT_PATH}}<br/>{{REQUEST_DATA}}

    Note over Edge: Auth & authz handled<br/>at Edge layer
    Edge->>Edge: Authenticate request
    Edge->>Edge: Authorize user
    Edge->>Edge: Inject userContext

    rect rgba(0, 0, 0, 0.5)
    Note over {{CONTROLLER}},{{REPOSITORY}}: {{MICROSERVICE}}

    Edge->>{{CONTROLLER}}: {{HTTP_METHOD}} {{ENDPOINT_PATH}}<br/>{userContext}

    {{SEQUENCE_STEPS}}

    end

    Note over Edge: Step N: Audit Event Publishing
    Edge->>Edge: Capture request/response
    Edge->>Kafka: Publish audit event<br/>(async, fire-and-forget)
    Edge-->>Client: {{SUCCESS_CODE}} {{SUCCESS_MESSAGE}}<br/>{{RESPONSE_DATA}}

    Note over Kafka,AuditConsumer: Asynchronous Audit Processing
    Kafka->>AuditConsumer: Consume audit event

    rect rgba(0, 0, 0, 0.5)
    Note over AuditConsumer: Audit Service
    AuditConsumer->>AuditConsumer: Parse audit event
    AuditConsumer->>AuditConsumer: Enrich with metadata
    AuditConsumer->>Database: INSERT INTO audit_log<br/>(event, timestamp, userId, etc.)
    Database-->>AuditConsumer: Logged
    end
```

---

## Processing Steps Summary (Pseudocode)

### Edge Layer - Spring Filter (Audit Event Publishing)

```pseudocode
CLASS AuditFilter EXTENDS OncePerRequestFilter:

    FUNCTION doFilterInternal(request, response, filterChain):
        // Wrap request and response to capture data
        wrappedRequest = new ContentCachingRequestWrapper(request)
        wrappedResponse = new ContentCachingResponseWrapper(response)

        startTime = getCurrentTimestamp()

        TRY:
            // Continue with the filter chain
            filterChain.doFilter(wrappedRequest, wrappedResponse)
        FINALLY:
            // After request is processed, publish audit event
            endTime = getCurrentTimestamp()

            auditEvent = {
                eventId: generateUUID(),
                eventType: "API_REQUEST",
                timestamp: startTime,
                duration: endTime - startTime,
                method: request.getMethod(),
                endpoint: request.getRequestURI(),
                statusCode: response.getStatus(),
                userId: extractUserIdFromContext(),
                tenantId: extractTenantIdFromContext(),
                requestBody: wrappedRequest.getContentAsString(),
                responseBody: wrappedResponse.getContentAsString(),
                ipAddress: request.getRemoteAddr(),
                userAgent: request.getHeader("User-Agent")
            }

            // Publish to Kafka asynchronously (fire-and-forget)
            kafkaTemplate.send("audit-events", auditEvent)

            // Copy response back to original response
            wrappedResponse.copyBodyToResponse()
        END TRY
    END FUNCTION

END CLASS
```

### {{MICROSERVICE}} - Main Processing Logic

```pseudocode
FUNCTION {{FUNCTION_NAME}}({{FUNCTION_PARAMS}}):
    // Note: Authentication & Authorization handled at Edge layer
    // userContext is pre-validated and injected by Edge Gateway
    // Audit logging handled asynchronously by Edge Filter -> Kafka -> Audit Consumer

    {{PSEUDOCODE_STEPS}}

END FUNCTION
```

### Audit Service - Kafka Consumer

```pseudocode
CLASS AuditEventConsumer:

    @KafkaListener(topics = "audit-events", groupId = "audit-service-group")
    FUNCTION consumeAuditEvent(auditEvent):
        // Step N: Asynchronous Audit Processing

        TRY:
            // Parse and enrich audit event
            enrichedEvent = {
                ...auditEvent,
                processedAt: getCurrentTimestamp(),
                environment: getEnvironment(),
                region: getRegion()
            }

            // Extract business event details if applicable
            IF enrichedEvent.endpoint MATCHES "{{ENDPOINT_PATH}}" AND enrichedEvent.method == "{{HTTP_METHOD}}":
                enrichedEvent.businessEventType = "{{BUSINESS_EVENT_TYPE}}"
                {{EXTRACT_BUSINESS_DETAILS}}

            // Persist to audit log database
            database.insert("audit_log", enrichedEvent)

            LOG("Audit event processed successfully: " + enrichedEvent.eventId)

        CATCH Exception as error:
            // Log error and send to dead letter queue for retry
            LOG_ERROR("Failed to process audit event: " + error)
            kafkaTemplate.send("audit-events-dlq", auditEvent)
        END TRY

    END FUNCTION

END CLASS
```

---

## Key Validation Rules

| Rule | Description | Error Code |
|------|-------------|------------|
{{VALIDATION_RULES}}

---

## Database Transactions

{{TRANSACTION_DESCRIPTION}}

```sql
{{SQL_OPERATIONS}}
```

{{FAILURE_HANDLING}}

---

## Timing Expectations

### Synchronous Request Flow

| Step | Typical Duration | Timeout |
|------|------------------|---------|
| Edge Layer Processing (Auth/Authz) | 20-50ms | 5s |
{{TIMING_STEPS}}
| Edge Filter - Kafka Publish | 5-10ms | 1s |
| **Total Request** | **{{TOTAL_MIN}}-{{TOTAL_MAX}}ms** | **{{TOTAL_TIMEOUT}}s** |

### Asynchronous Audit Processing

| Step | Typical Duration | Notes |
|------|------------------|-------|
| Kafka Event Delivery | 10-50ms | Async, does not block request |
| Audit Consumer Processing | 50-100ms | Independent from request flow |
| Database Audit Log Insert | 20-50ms | Separate transaction |
| **Total Audit Processing** | **80-200ms** | Fire-and-forget from client perspective |

---

## Audit Architecture Pattern

### Event-Driven Audit Logging

This API implements an event-driven audit pattern using Kafka for asynchronous, non-blocking audit logging:

**Benefits:**
- **Decoupled**: Audit logging is completely decoupled from business logic
- **Non-blocking**: Audit failures don't impact API response times or availability
- **Centralized**: All API requests are audited consistently at the Edge layer
- **Scalable**: Kafka provides horizontal scalability for high-volume audit events
- **Resilient**: Dead letter queue (DLQ) handles failed audit events for retry

**Architecture Components:**

1. **Edge Gateway - Spring Filter** (`AuditFilter`)
   - Extends `OncePerRequestFilter` in Spring Boot
   - Wraps request/response using `ContentCachingRequestWrapper` and `ContentCachingResponseWrapper`
   - Captures full request/response payload, headers, and metadata
   - Publishes audit events to Kafka topic `audit-events` asynchronously
   - Fire-and-forget pattern - doesn't wait for Kafka confirmation

2. **Kafka Topic** (`audit-events`)
   - Message broker for audit events
   - Provides durability, ordering, and replay capabilities
   - Supports multiple consumers for different audit use cases
   - Configured with appropriate retention policy

3. **Audit Service - Kafka Consumer** (`AuditEventConsumer`)
   - Consumes events from `audit-events` topic
   - Enriches events with additional metadata (environment, region, etc.)
   - Extracts business event details from request/response payloads
   - Persists to `audit_log` database table
   - Sends failed events to `audit-events-dlq` for retry

**Event Schema:**

```json
{
  "eventId": "uuid",
  "eventType": "API_REQUEST",
  "timestamp": "{{CURRENT_DATE}}T12:00:00Z",
  "duration": {{EXAMPLE_DURATION}},
  "method": "{{HTTP_METHOD}}",
  "endpoint": "{{ENDPOINT_PATH}}",
  "statusCode": {{SUCCESS_CODE}},
  "userId": "user-123",
  "tenantId": "tenant-456",
  "requestBody": "{...}",
  "responseBody": "{...}",
  "ipAddress": "192.168.1.1",
  "userAgent": "Mozilla/5.0...",
  "businessEventType": "{{BUSINESS_EVENT_TYPE}}",
  {{BUSINESS_EVENT_FIELDS}}
}
```

---

{{REQUEST_RESPONSE_SCHEMAS}}

---

**Last Updated**: {{CURRENT_DATE}}
**Version**: 1.0.0
````

## === TEMPLATE END (WITH AUDIT) ===

---

## === TEMPLATE START (WITHOUT AUDIT) ===

Use this template if Q1 = "No" (audit disabled)

````markdown
# [V1] {{API_NAME}}

## Overview

**Endpoint**: `{{HTTP_METHOD}} {{ENDPOINT_PATH}}`
**Purpose**: {{PURPOSE}}
**Version**: 1.0.0
**Last Updated**: {{CURRENT_DATE}}

{{DETAILED_DESCRIPTION}}

---

## Processing Rule - Sequence Diagram

### Full Request Flow

This diagram illustrates the complete processing flow for {{API_NAME_LOWER}}, including validation steps and data operations.

```mermaid
sequenceDiagram
    participant Client
    participant {{CONTROLLER}} as {{CONTROLLER_DISPLAY}}
    participant {{SERVICE}} as {{SERVICE_DISPLAY}}
    participant {{REPOSITORY}} as {{REPOSITORY_DISPLAY}}
    participant Database as PostgreSQL DB

    Client->>{{CONTROLLER}}: {{HTTP_METHOD}} {{ENDPOINT_PATH}}<br/>{{REQUEST_DATA}}

    rect rgba(0, 0, 0, 0.5)
    Note over {{CONTROLLER}},{{REPOSITORY}}: {{MICROSERVICE}}
    Note right of {{CONTROLLER}}: Auth & authz handled<br/>at Edge layer

    {{SEQUENCE_STEPS}}

    end
```

---

## Processing Steps Summary (Pseudocode)

```pseudocode
FUNCTION {{FUNCTION_NAME}}({{FUNCTION_PARAMS}}):
    // Note: Authentication & Authorization handled at Edge layer
    // userContext is pre-validated and injected by Edge Gateway

    {{PSEUDOCODE_STEPS}}

END FUNCTION
```

---

## Key Validation Rules

| Rule | Description | Error Code |
|------|-------------|------------|
{{VALIDATION_RULES}}

---

## Database Transactions

{{TRANSACTION_DESCRIPTION}}

```sql
{{SQL_OPERATIONS}}
```

{{FAILURE_HANDLING}}

---

## Timing Expectations

| Step | Typical Duration | Timeout |
|------|------------------|---------|
{{TIMING_STEPS}}
| **Total Request** | **{{TOTAL_MIN}}-{{TOTAL_MAX}}ms** | **{{TOTAL_TIMEOUT}}s** |

---

{{REQUEST_RESPONSE_SCHEMAS}}

---

**Last Updated**: {{CURRENT_DATE}}
**Version**: 1.0.0
````

## === TEMPLATE END (WITHOUT AUDIT) ===

---

## Template Features

✅ **Self-Contained**: No external file dependencies
✅ **Microservices Architecture**: Service boundaries with visual grouping
✅ **Edge Gateway Pattern**: Auth/authz at Edge layer
✅ **Flexible Audit**: Optional Kafka-based audit logging
✅ **Mermaid Diagrams**: Interactive sequence diagrams
✅ **Pseudocode**: Implementation-ready logic
✅ **Error Handling**: Comprehensive error scenarios
✅ **Transaction Management**: Clear database flows
✅ **Performance Metrics**: Timing expectations for monitoring
