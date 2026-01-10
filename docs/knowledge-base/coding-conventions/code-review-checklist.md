# Code Review Checklist

Use this checklist when reviewing code or before committing your own code.

---

## Architecture & Design

### Module Structure
- [ ] Code is in the correct module (API, Core, Data Access, Shared)
- [ ] No circular dependencies between modules
- [ ] Dependency direction follows: API/Data Access → Core → Shared
- [ ] No business logic in API or Data Access layers

### Package Organization
- [ ] Package-by-feature structure followed in Core
- [ ] Entities in `entities/` package
- [ ] Services in `service/` package
- [ ] Repositories in correct layer (interface in Core, impl in Data Access)
- [ ] DTOs in `shared/http/{feature}/request|response/`

### CQRS Pattern
- [ ] Commands and queries are separated
- [ ] Command services handle only write operations
- [ ] Query services handle only read operations
- [ ] No mixing of commands and queries in same service

---

## Naming Conventions

### Classes
- [ ] Services: `{Feature}CommandService` / `{Feature}QueryService`
- [ ] Implementations: `{Feature}CommandServiceImpl` / `{Feature}QueryServiceImpl`
- [ ] Domain entities: `{Entity}` (no "Entity" suffix in Core)
- [ ] JPA entities: `{Entity}Entity`
- [ ] Controllers: `{Feature}Controller`
- [ ] Repositories: `{Feature}Repository` (interface), `Jpa{Feature}Repository`, `{Feature}RepositoryImpl`
- [ ] DTOs: `{Action}{Feature}Request` / `{Feature}Response`
- [ ] Commands: `{Action}{Feature}Command`
- [ ] Queries: `Filter{Feature}Query` / `{Feature}PaginationQuery`
- [ ] Mappers: `{Feature}RequestMapper` / `{Feature}ResponseMapper` / `{Feature}EntityMapper`
- [ ] Exceptions: `{Feature}{ErrorType}Exception`

### Methods
- [ ] Command methods use action verbs: `create`, `update`, `delete`, `add`, `remove`
- [ ] Query methods use: `get` (throws), `find` (Optional), `query`, `count`, `exists`
- [ ] Boolean methods: `is`, `has`, `can`, `should`
- [ ] Mapper methods: `to{TargetType}`

### Variables
- [ ] camelCase for variables and parameters
- [ ] UPPER_SNAKE_CASE for constants
- [ ] Descriptive names, no abbreviations
- [ ] No single-letter variables (except loop counters)

---

## Code Quality

### Services

#### Command Services
- [ ] Annotated with `@Service`
- [ ] Uses `@RequiredArgsConstructor` for dependency injection
- [ ] All write methods have `@Transactional`
- [ ] No `@Transactional(readOnly = true)` on command services
- [ ] Business validation is performed
- [ ] Exceptions are thrown for business rule violations
- [ ] Uses `@Slf4j` for logging

#### Query Services
- [ ] Annotated with `@Service`
- [ ] Uses `@RequiredArgsConstructor` for dependency injection
- [ ] All read methods have `@Transactional(readOnly = true)`
- [ ] Returns `Optional` for single items that may not exist
- [ ] Throws exceptions for single items that must exist (using `get`)
- [ ] Uses pagination for list queries
- [ ] Uses `@Slf4j` for logging

### Controllers
- [ ] Annotated with `@RestController`
- [ ] Implements API interface from shared module
- [ ] Uses `@RequiredArgsConstructor` for dependency injection
- [ ] All request DTOs validated with `@Valid`
- [ ] No business logic in controller
- [ ] Only delegates to services
- [ ] Returns DTOs with appropriate `@ResponseStatus`
- [ ] Proper exception handling (via `@ExceptionHandler` or global handler)

### Repositories

#### Repository Interfaces (Core)
- [ ] Defines domain-level operations
- [ ] Located in `core/features/{feature}/`
- [ ] Uses domain entities (not JPA entities)
- [ ] No implementation code

#### JPA Repositories (Data Access)
- [ ] Prefixed with `Jpa` (e.g., `JpaUserRepository`)
- [ ] Extends appropriate Spring Data interface
- [ ] Located in `data-access/relational/{feature}/repository/`
- [ ] Uses JPA entities

#### Repository Implementations (Data Access)
- [ ] Suffixed with `Impl`
- [ ] Implements domain repository interface from Core
- [ ] Delegates to JPA repository
- [ ] Uses entity mappers for conversions

---

## Entities & DTOs

### Domain Entities (Core)
- [ ] Immutable (using `@Getter`, `@Builder`, `@With`, `@RequiredArgsConstructor`)
- [ ] No JPA annotations
- [ ] No setters
- [ ] Located in `core/features/{feature}/entities/`
- [ ] Uses value objects for IDs (e.g., `UserId`)
- [ ] Uses `@EqualsAndHashCode(of = "id")` if needed

### JPA Entities (Data Access)
- [ ] Suffixed with `Entity`
- [ ] Has JPA annotations (`@Entity`, `@Table`, etc.)
- [ ] Mutable (using `@Getter`, `@Setter`)
- [ ] Located in `data-access/relational/{feature}/entities/`
- [ ] Has `@Id` annotation
- [ ] Uses appropriate fetch strategies (LAZY by default)
- [ ] Has indexes on frequently queried fields
- [ ] Uses soft delete if applicable (`@SQLDelete`, `@SQLRestriction`)
- [ ] Has audit fields if applicable (`@CreatedDate`, `@LastModifiedDate`, etc.)

### Request DTOs
- [ ] Located in `shared/http/{feature}/request/`
- [ ] Immutable (using `@Getter`, `@Builder`, `@RequiredArgsConstructor`)
- [ ] Has validation annotations (`@NotNull`, `@NotEmpty`, `@Email`, etc.)
- [ ] Named with pattern: `{Action}{Feature}Request`
- [ ] No business logic

### Response DTOs
- [ ] Located in `shared/http/{feature}/response/`
- [ ] Immutable (using `@Getter`, `@Builder`, `@RequiredArgsConstructor`)
- [ ] Named with pattern: `{Feature}Response`
- [ ] No sensitive data exposed

### Commands (CQRS)
- [ ] Located in `core/features/{feature}/request/`
- [ ] Immutable (using `@Getter`, `@Builder`, `@With`, `@RequiredArgsConstructor` or `record`)
- [ ] Named with pattern: `{Action}{Feature}Command`
- [ ] Contains data needed for write operation

### Queries (CQRS)
- [ ] Located in `core/features/{feature}/request/`
- [ ] Immutable (using `@Getter`, `@Builder`, `@With`, `@RequiredArgsConstructor`)
- [ ] Named with pattern: `Filter{Feature}Query` or `{Feature}PaginationQuery`
- [ ] Contains filter/search criteria

---

## Mappers (MapStruct)

### Configuration
- [ ] Annotated with `@Mapper(config = AppMapStructConfiguration.class)`
- [ ] Uses common mappers: `uses = {MapstructCommonMapper.class, ...}`
- [ ] Has `INSTANCE` singleton field

### Mapping Rules
- [ ] Separate mappers for different layers (Request, Response, Entity)
- [ ] Uses `@Mapping` for non-matching fields
- [ ] Uses `qualifiedByName` for custom conversions
- [ ] Uses `@Context` for additional mapping context
- [ ] Uses `@AfterMapping` for post-processing
- [ ] No manual mapping code where MapStruct can generate it

---

## Exception Handling

### Exception Classes
- [ ] Base domain exception per service (extends `DomainException`)
- [ ] All exceptions extend from base domain exception
- [ ] All exceptions annotated with `@ErrorCodeValue`
- [ ] Located in `shared/exceptions/`
- [ ] Named with pattern: `{Feature}{ErrorType}Exception`
- [ ] Has default constructor with default message
- [ ] Has constructor with custom message
- [ ] Has constructor with message and cause

### Exception Usage
- [ ] Exceptions thrown for business rule violations
- [ ] Exceptions thrown when entity not found (for `get` methods)
- [ ] Exceptions not swallowed in transactional methods
- [ ] Exceptions re-thrown after logging
- [ ] Error messages are meaningful
- [ ] No sensitive data in error messages

---

## Validation

### DTO Validation
- [ ] All required fields have `@NotNull` or `@NotEmpty`
- [ ] Email fields have `@Email`
- [ ] String fields have `@Length(max = X)`
- [ ] Custom validators used for complex rules
- [ ] Nested objects validated with `@Valid`

### Controller Validation
- [ ] All request parameters/bodies use `@Valid`
- [ ] Validation errors handled by exception handler

### Business Validation
- [ ] Business rules validated in service layer
- [ ] Appropriate exceptions thrown for violations
- [ ] Validation happens before database operations

---

## Transaction Management

### Command Services
- [ ] All write methods annotated with `@Transactional`
- [ ] No `@Transactional` on private methods
- [ ] Exceptions not swallowed (or re-thrown)
- [ ] No `readOnly = true` on command methods

### Query Services
- [ ] All read methods annotated with `@Transactional(readOnly = true)`
- [ ] Read-only optimization enabled

### Controllers
- [ ] No `@Transactional` annotations (transactions belong in service layer)

---

## Logging

### Configuration
- [ ] Service classes use `@Slf4j` annotation
- [ ] No `System.out.println()` or `e.printStackTrace()`

### Log Levels
- [ ] `DEBUG` for detailed diagnostic information
- [ ] `INFO` for important business events
- [ ] `WARN` for potentially harmful situations
- [ ] `ERROR` for error events with exceptions

### Log Content
- [ ] Uses parameterized logging (no string concatenation)
- [ ] Important events logged (create, update, delete)
- [ ] Errors logged with exception parameter
- [ ] No sensitive data logged (passwords, tokens, PII)

---

## Database

### JPA Configuration
- [ ] Entities have appropriate indexes
- [ ] Lazy loading used by default
- [ ] EntityGraph or JOIN FETCH used to avoid N+1
- [ ] Soft delete configured if applicable
- [ ] Audit annotations present if applicable
- [ ] Optimistic locking (`@Version`) if needed
- [ ] `@DynamicUpdate` used for entities with many fields

### Queries
- [ ] No SQL injection vulnerabilities (using JPA/QueryDSL)
- [ ] Pagination used for list queries
- [ ] No `findAll()` without pagination on large tables
- [ ] No N+1 query problems
- [ ] Appropriate fetch strategies

---

## Performance

### Queries
- [ ] List endpoints use pagination
- [ ] Proper indexes on frequently queried fields
- [ ] No unnecessary eager loading
- [ ] No N+1 queries (use EntityGraph or JOIN FETCH)
- [ ] Consider caching for frequently accessed data

### Transactions
- [ ] Query methods use `readOnly = true`
- [ ] Transactions as short as possible
- [ ] No long-running operations in transactions

---

## Testing

### Unit Tests
- [ ] Service layer has unit tests
- [ ] Test method names follow pattern: `should{Expected}_when{Condition}`
- [ ] Tests use AAA pattern (Arrange, Act, Assert)
- [ ] Mocks used for dependencies
- [ ] All business logic paths tested
- [ ] Edge cases tested
- [ ] Exception scenarios tested
- [ ] Minimum 80% code coverage

### Integration Tests
- [ ] Controller integration tests exist
- [ ] Tests use TestContainers for database
- [ ] All endpoints tested
- [ ] Validation tested
- [ ] Error responses tested
- [ ] Tests don't depend on execution order
- [ ] No hardcoded test data
- [ ] Test data builders used

---

## Security

### Input Handling
- [ ] User input validated
- [ ] User input sanitized
- [ ] No SQL injection vulnerabilities
- [ ] No XSS vulnerabilities

### Error Handling
- [ ] Error messages don't expose sensitive information
- [ ] Stack traces not exposed in production
- [ ] Generic error messages for security-sensitive operations

### Sensitive Data
- [ ] Passwords not logged
- [ ] Tokens not logged
- [ ] PII not logged unnecessarily
- [ ] Sensitive data not in error messages

---

## Documentation

### Code Documentation
- [ ] Public interfaces have Javadoc
- [ ] Complex logic has explanatory comments
- [ ] Business rules documented in service interfaces
- [ ] API interfaces have Javadoc with `@param`, `@return`, `@throws`

### API Documentation
- [ ] API contracts defined in shared module
- [ ] OpenAPI/Swagger annotations present
- [ ] Request/response examples provided

---

## Lombok

### Appropriate Usage
- [ ] DTOs use `@Getter`, `@Builder`, `@RequiredArgsConstructor`
- [ ] Domain entities use `@Getter`, `@Builder`, `@With`, `@RequiredArgsConstructor`
- [ ] JPA entities use `@Getter`, `@Setter`, `@NoArgsConstructor`, `@AllArgsConstructor`, `@Builder`
- [ ] Services use `@RequiredArgsConstructor`, `@Slf4j`
- [ ] Commands/Queries use `@Getter`, `@Builder`, `@With`, `@RequiredArgsConstructor` or `record`

### Avoid
- [ ] No `@Data` on JPA entities (causes lazy loading issues)
- [ ] No `@Data` on domain entities (creates unwanted mutability)
- [ ] No `@EqualsAndHashCode` on entities with lazy associations

---

## General Best Practices

### Code Style
- [ ] Consistent formatting (run `mvn spotless:apply`)
- [ ] No unused imports
- [ ] No commented-out code
- [ ] No compiler warnings
- [ ] Code is readable and self-documenting

### Dependencies
- [ ] No circular dependencies
- [ ] Only necessary dependencies imported
- [ ] Dependency injection via constructor (not field injection)

### Immutability
- [ ] Domain entities are immutable
- [ ] Commands are immutable
- [ ] Queries are immutable
- [ ] DTOs are immutable
- [ ] Use `@With` for creating modified copies

---

## Pre-Commit Checklist

Before committing:

1. **Build**
   - [ ] `mvn clean compile` succeeds
   - [ ] No compiler warnings

2. **Tests**
   - [ ] `mvn test` passes
   - [ ] `mvn verify` passes (integration tests)
   - [ ] No test failures
   - [ ] Code coverage meets minimum (80%)

3. **Code Quality**
   - [ ] `mvn spotless:apply` executed
   - [ ] No SonarQube critical/major issues
   - [ ] This checklist reviewed

4. **Documentation**
   - [ ] Javadoc added for public APIs
   - [ ] README updated if needed
   - [ ] API documentation updated if needed

---

## Review Priority

When reviewing, focus on these in order:

1. **Critical** (Must Fix)
   - Security vulnerabilities
   - Data integrity issues
   - Architectural violations
   - Transaction management issues

2. **High** (Should Fix)
   - Naming convention violations
   - Missing validation
   - Performance issues (N+1, missing pagination)
   - Exception handling issues

3. **Medium** (Recommend Fix)
   - Missing tests
   - Incomplete logging
   - Inconsistent patterns
   - Missing documentation

4. **Low** (Nice to Have)
   - Code style inconsistencies
   - Opportunities for refactoring
   - Additional test cases

---

**Last Updated:** 2026-01-10
**Based on:** user-service and payment-service implementations
