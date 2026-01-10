# Feature Implementation Plan: [Feature Name]

**Status**: Draft | In Progress | Under Review | Completed
**Created**: [YYYY-MM-DD]
**Owner**: [Name or "Claude AI"]
**Completed**: [YYYY-MM-DD or N/A]

---

## 1. Overview

### Feature Description
[Brief description of the feature - what it does and why it's needed]

### Business Requirements
- [ ] Requirement 1
- [ ] Requirement 2
- [ ] Requirement 3

### Success Criteria
- [ ] Criterion 1
- [ ] Criterion 2
- [ ] Criterion 3

### Related Documentation
- [Link to user story/ticket]
- [Link to design docs]
- [Link to API specs]

---

## 2. Technical Analysis

### Affected Modules
- [ ] `service-name-api` - [What changes]
- [ ] `service-name-app` - [What changes]
- [ ] `service-name-core` - [What changes]
- [ ] `service-name-data-access` - [What changes]
- [ ] `service-name-shared` - [What changes]
- [ ] `service-name-test` - [What changes]

### Architecture Patterns Used
- [ ] Package-by-Feature
- [ ] CQRS (Command/Query Separation)
- [ ] Repository Pattern
- [ ] DTO Pattern
- [ ] [Other patterns]

### Dependencies
- Internal dependencies: [List microservices/modules]
- External dependencies: [List libraries/APIs]
- Database changes: [Yes/No - describe if yes]

### Database Schema Changes
```sql
-- If applicable, include DDL statements
-- CREATE TABLE, ALTER TABLE, etc.
```

---

## 3. Implementation Plan

### Phase 1: Shared Layer
- [ ] Create request DTOs in `shared/http/features/[feature-name]/request/`
  - [ ] `Create[Feature]Request.java`
  - [ ] `Update[Feature]Request.java`
  - [ ] Other request DTOs
- [ ] Create response DTOs in `shared/http/features/[feature-name]/response/`
  - [ ] `[Feature]Response.java`
  - [ ] Other response DTOs
- [ ] Create API interface in `shared/http/apis/`
  - [ ] `[Feature]Api.java` with all endpoint definitions
- [ ] Create custom exceptions in `shared/exceptions/`
  - [ ] `[Feature]NotFoundException.java`
  - [ ] `[Feature]AlreadyExistsException.java`
  - [ ] Other exceptions
- [ ] Create enums in `shared/enums/` (if needed)
- [ ] Add validation annotations to all DTOs

### Phase 2: Core Layer (Business Logic)
- [ ] Create domain entities in `core/features/[feature-name]/entities/`
  - [ ] `[Feature].java` (domain model)
- [ ] Create CQRS Commands in `core/features/[feature-name]/request/`
  - [ ] `Create[Feature]Command.java`
  - [ ] `Update[Feature]Command.java`
  - [ ] `Delete[Feature]Command.java`
- [ ] Create CQRS Queries in `core/features/[feature-name]/request/`
  - [ ] `Get[Feature]ByIdQuery.java`
  - [ ] `Find[Feature]sQuery.java`
- [ ] Create repository interface
  - [ ] `[Feature]Repository.java` (interface only, no implementation)
- [ ] Create Command Service
  - [ ] `[Feature]CommandService.java` (interface)
  - [ ] `[Feature]CommandServiceImpl.java` (implementation with @Transactional)
- [ ] Create Query Service
  - [ ] `[Feature]QueryService.java` (interface)
  - [ ] `[Feature]QueryServiceImpl.java` (implementation with @Transactional(readOnly=true))
- [ ] Create mappers
  - [ ] `[Feature]CoreMapper.java` (MapStruct)
- [ ] Create validators/utilities (if needed)
  - [ ] `[Feature]Validator.java`
- [ ] Implement business logic and validation rules

### Phase 3: Data Access Layer
- [ ] Create JPA entities in `dataaccess/relational/[feature-name]/entities/`
  - [ ] `[Feature]JpaEntity.java` with JPA annotations
- [ ] Create Spring Data repository
  - [ ] `[Feature]JpaRepository.java` extends JpaRepository
- [ ] Create repository implementation
  - [ ] `[Feature]RepositoryImpl.java` implements `[Feature]Repository`
- [ ] Create JPA mapper
  - [ ] `[Feature]JpaMapper.java` (MapStruct - JPA Entity ↔ Domain Entity)
- [ ] Add database indexes for performance
- [ ] Configure fetch strategies (LAZY/EAGER)

### Phase 4: API Layer
- [ ] Create controller in `api/features/[feature-name]/`
  - [ ] `[Feature]Controller.java` implements `[Feature]Api`
  - [ ] Add @RestController annotation
  - [ ] Implement all API interface methods
- [ ] Create API mapper
  - [ ] `[Feature]ApiMapper.java` (MapStruct - DTO ↔ Command/Query)
- [ ] Add validation with @Valid
- [ ] Return DTOs with proper HTTP status codes (@ResponseStatus)
- [ ] Add request/response logging

### Phase 5: Exception Handling
- [ ] Add @ExceptionHandler methods or use global handler
- [ ] Return consistent error responses
- [ ] Map business exceptions to HTTP status codes
  - [ ] 400 for validation errors
  - [ ] 404 for not found
  - [ ] 409 for conflicts
  - [ ] 422 for business rule violations

### Phase 6: Testing
- [ ] Unit Tests for Core Layer
  - [ ] `[Feature]CommandServiceImplTest.java`
  - [ ] `[Feature]QueryServiceImplTest.java`
  - [ ] Test all business logic paths
  - [ ] Test exception scenarios
  - [ ] Achieve 80%+ coverage
- [ ] Integration Tests
  - [ ] `[Feature]ControllerIntegrationTest.java`
  - [ ] Use TestContainers for database
  - [ ] Test all API endpoints
  - [ ] Test validation
  - [ ] Test error responses
- [ ] Test Data Builders
  - [ ] `[Feature]TestDataBuilder.java`

### Phase 7: Documentation & Quality
- [ ] Add Javadoc to all public interfaces
- [ ] Document business rules in service interfaces
- [ ] Update API documentation (OpenAPI/Swagger)
- [ ] Run `mvn spotless:apply` for formatting
- [ ] Run `mvn test` - ensure all tests pass
- [ ] Run `mvn verify` - ensure integration tests pass
- [ ] Fix any compiler warnings
- [ ] Address SonarQube issues

---

## 4. Files to Create/Modify

### New Files
```
shared/
└── src/main/java/io/agentic/microagent/[service]/shared/
    ├── http/
    │   ├── apis/
    │   │   └── [Feature]Api.java
    │   └── features/[feature-name]/
    │       ├── request/
    │       │   ├── Create[Feature]Request.java
    │       │   └── Update[Feature]Request.java
    │       └── response/
    │           └── [Feature]Response.java
    └── exceptions/
        ├── [Feature]NotFoundException.java
        └── [Feature]AlreadyExistsException.java

core/
└── src/main/java/io/agentic/microagent/[service]/core/
    └── features/[feature-name]/
        ├── entities/
        │   └── [Feature].java
        ├── request/
        │   ├── Create[Feature]Command.java
        │   ├── Update[Feature]Command.java
        │   └── Get[Feature]Query.java
        ├── service/
        │   ├── [Feature]CommandServiceImpl.java
        │   └── [Feature]QueryServiceImpl.java
        ├── mapper/
        │   └── [Feature]CoreMapper.java
        ├── [Feature]CommandService.java
        ├── [Feature]QueryService.java
        └── [Feature]Repository.java

data-access/
└── src/main/java/io/agentic/microagent/[service]/dataaccess/
    └── relational/[feature-name]/
        ├── entities/
        │   └── [Feature]JpaEntity.java
        ├── repository/
        │   └── [Feature]JpaRepository.java
        ├── mapper/
        │   └── [Feature]JpaMapper.java
        └── [Feature]RepositoryImpl.java

api/
└── src/main/java/io/agentic/microagent/api/
    └── features/[feature-name]/
        ├── mapper/
        │   └── [Feature]ApiMapper.java
        └── [Feature]Controller.java

test/
└── src/test/java/io/agentic/microagent/[service]/test/
    ├── [Feature]CommandServiceImplTest.java
    ├── [Feature]QueryServiceImplTest.java
    └── [Feature]ControllerIntegrationTest.java
```

### Modified Files
- `[service]-app/src/main/resources/application.yml` - [If config changes needed]
- `pom.xml` - [If new dependencies needed]
- [Other files]

---

## 5. API Endpoints Design

### Endpoints

| Method | Endpoint | Description | Request | Response | Status |
|--------|----------|-------------|---------|----------|--------|
| POST   | `/api/v1/[resources]` | Create new resource | `Create[Feature]Request` | `[Feature]Response` | 201 |
| GET    | `/api/v1/[resources]/{id}` | Get by ID | - | `[Feature]Response` | 200 |
| GET    | `/api/v1/[resources]` | Query all (paginated) | Query params | `PageResponse<[Feature]Response>` | 200 |
| PUT    | `/api/v1/[resources]/{id}` | Update (full) | `Update[Feature]Request` | `[Feature]Response` | 200 |
| PATCH  | `/api/v1/[resources]/{id}` | Update (partial) | `Patch[Feature]Request` | `[Feature]Response` | 200 |
| DELETE | `/api/v1/[resources]/{id}` | Delete | - | - | 204 |

### Request Examples
```json
// POST /api/v1/[resources]
{
  "field1": "value1",
  "field2": "value2"
}
```

### Response Examples
```json
// 200 OK
{
  "id": "uuid",
  "field1": "value1",
  "field2": "value2",
  "createdAt": "2026-01-10T10:30:00",
  "updatedAt": "2026-01-10T10:30:00"
}
```

---

## 6. Testing Strategy

### Unit Tests
- Test all service methods (commands and queries)
- Test business validation logic
- Test exception scenarios
- Mock dependencies with Mockito
- Target: 80%+ code coverage

### Integration Tests
- Test all API endpoints
- Test validation rules
- Test error responses
- Use TestContainers for database
- Test pagination and filtering

### Test Scenarios
1. **Happy Path**
   - Create resource successfully
   - Retrieve resource successfully
   - Update resource successfully
   - Delete resource successfully

2. **Validation Errors**
   - Missing required fields
   - Invalid field formats
   - Business rule violations

3. **Error Cases**
   - Resource not found
   - Duplicate resource
   - Unauthorized access (if applicable)

---

## 7. Risks & Considerations

### Potential Issues
- [ ] Issue 1: [Description and mitigation]
- [ ] Issue 2: [Description and mitigation]

### Alternative Approaches Considered
1. **Approach 1**: [Description, pros/cons, why not chosen]
2. **Approach 2**: [Description, pros/cons, why not chosen]

### Performance Considerations
- [ ] Add database indexes on [fields]
- [ ] Use pagination for list endpoints
- [ ] Consider caching for [specific queries]

### Security Considerations
- [ ] Validate all user input
- [ ] Sanitize data before persistence
- [ ] Add authorization checks (if needed)
- [ ] Don't expose sensitive data in logs

### Migration Strategy
- [ ] No migration needed
- [ ] Or: [Describe data migration approach]
- [ ] Backward compatibility: [Yes/No - explain]

---

## 8. Open Questions

- [ ] Question 1: [Description - needs answer]
- [ ] Question 2: [Description - needs answer]

---

## 9. Review Checklist

Before marking this plan as complete:

- [ ] All template sections filled out
- [ ] Implementation steps are detailed and specific
- [ ] All files to create/modify are listed
- [ ] API endpoints are well-defined
- [ ] Testing strategy is comprehensive
- [ ] Risks and mitigations identified
- [ ] Follows MicroAgent architecture guidelines
- [ ] Adheres to coding conventions
- [ ] Plan reviewed and approved by team

---

## 10. Progress Tracking

### Completed Tasks
- [x] Example completed task
- [x] Another completed task

### In Progress
- [ ] Current task being worked on

### Pending
- [ ] Future task 1
- [ ] Future task 2

---

**Notes**:
- Add any additional notes, decisions, or context here
- Document deviations from the plan
- Link to relevant discussions or tickets
