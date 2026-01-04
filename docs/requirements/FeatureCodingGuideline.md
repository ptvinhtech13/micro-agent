# Feature Coding Guideline 
==========================================

This document defines the **MANDATORY** feature structure and coding guidelines for all Microservices within the MicroAgent project. 
Each Microservice must adhere to this structure to ensure consistency, maintainability, and scalability across the codebase.

### Feature Coding Structure Overview
Each Microservice is a **separate Spring Boot Application** organized into 6 modules following **Package-by-Feature** and **CQRS** patterns.

### The 6 Mandatory Modules

#### 1️⃣ `service-name-api` - API Layer
**Purpose**: REST/WebSocket controllers
**Package**: `io.agentic.microagent.api.*`
**Dependencies**: Imports `service-name-core`, `service-name-shared`

```
/service-name-api
└── src/main/java/io/agentic/microagent/api
    └── features                                    # Package-by-Feature
        └── featurename                            # e.g., "agentregistration"
            ├── mapper/                             # MapStruct mappers (DTO ↔ Command/Query)
            │   └── FeatureNameApiMapper.java
            └── FeatureNameController.java          # REST or WebSocket Controller
```

#### 2️⃣ `service-name-app` - Application Entry Point
**Purpose**: Main Spring Boot Application class
**Package**: `io.agentic.microagent.servicename.app`
**Dependencies**: Imports ALL other modules

```
/service-name-app
├── src/main/java/io/agentic/microagent/servicename/app
│   └── ServiceNameApplication.java                # @SpringBootApplication
└── src/main/resources
    ├── application.yml                            # Application configuration
    └── application-{profile}.yml                  # Profile-specific configs
```

#### 3️⃣ `service-name-core` - Business Logic Layer (CQRS)
**Purpose**: Domain logic, services, business entities
**Package**: `io.agentic.microagent.servicename.core`
**Dependencies**: Imports `service-name-shared`
**Rules**: ❌ NO Controllers here, ✅ Pure business logic only

```
/service-name-core
└── src/main/java/io/agentic/microagent/servicename/core
    └── features                                    # Package-by-Feature
        └── feature-name                            # e.g., "agentregistration"
            ├── constants/                          # Feature-specific constants
            │   └── FeatureNameConstants.java
            ├── entities/                           # Domain Entities (NOT JPA entities)
            │   └── FeatureName.java       #  root
            ├── generator/                          # ID generators, factories
            │   └── FeatureNameIdGenerator.java
            ├── mapper/                             # MapStruct mappers (internal)
            │   └── FeatureNameCoreMapper.java
            ├── request/                            # CQRS Commands & Queries
            │   ├── CreateFeatureNameCommand.java   # Command pattern
            │   ├── UpdateFeatureNameCommand.java
            │   └── GetFeatureNameQuery.java        # Query pattern
            ├── service/                            # Service implementations
            │   ├── FeatureNameCommandServiceImpl.java
            │   └── FeatureNameQueryServiceImpl.java
            ├── utils/                              # Feature-specific utilities
            │   └── FeatureNameValidator.java
            ├── FeatureNameCommandService.java      # Command service interface
            ├── FeatureNameQueryService.java        # Query service interface
            └── FeatureNameRepository.java          # Repository interface (not impl!)
```

#### 4️⃣ `service-name-data-access` - Data Access Layer
**Purpose**: Repository implementations, database interaction
**Package**: `io.agentic.microagent.servicename.dataaccess`
**Dependencies**: Imports `service-name-core`, `service-name-shared`

```
/service-name-data-access
└── src/main/java/io/agentic/microagent/servicename/dataaccess
    ├── relational/                                 # Relational DB (PostgreSQL, MySQL)
    │   ├── feature-name/
    │   │   ├── entities/                           # JPA/Hibernate entities
    │   │   │   └── FeatureNameJpaEntity.java
    │   │   ├── mapper/                             # JPA Entity ↔ Domain Entity
    │   │   │   └── FeatureNameJpaMapper.java
    │   │   ├── repository/
    │   │   │   └── FeatureNameJpaRepository.java   # Spring Data JPA interface
    │   │   └── FeatureNameRepositoryImpl.java      # Implements core's Repository
    │   └── RelationalDatabaseAccessConfig.java     # @Configuration, @EnableJpaRepositories
    └── other-data-source-access/                   # NoSQL, Redis, External APIs
        ├── feature-name/
        │   ├── entities/
        │   │   └── FeatureNameMongoEntity.java
        │   ├── mapper/
        │   │   └── FeatureNameMongoMapper.java
        │   ├── repository/
        │   │   └── FeatureNameMongoRepository.java
        │   └── FeatureNameRepositoryImpl.java
        └── OtherDataSourceAccessConfig.java        # @Configuration for MongoDB/Redis
```

#### 5️⃣ `service-name-shared` - Shared Components
**Purpose**: DTOs, constants, utilities shared across modules
**Package**: `io.agentic.microagent.servicename.shared`
**Dependencies**: None (no dependencies on other service modules)

```
/service-name-shared
└── src/main/java/io/agentic/microagent/servicename/shared
    ├── constants/                                  # Cross-module constants
    │   └── ServiceNameConstants.java
    ├── enums/                                      # Shared enums
    │   └── FeatureNameStatus.java
    ├── exceptions/                                 # Custom exceptions
    │   └── FeatureNameException.java
    ├── utils/                                      # Utility classes
    │   └── DateTimeUtils.java
    └── http/                                       # HTTP-related models
        ├── apis/                                   # API interfaces
        │   └── FeatureNameApi.java
        └── features/
            └── feature-name/
                ├── request/                        # API Request DTOs
                │   └── CreateFeatureNameRequest.java
                └── response/                       # API Response DTOs
                    └── FeatureNameResponse.java
```

#### 6️⃣ `service-name-test` - Integration Tests
**Purpose**: Integration tests using TestContainers
**Package**: `io.agentic.microagent.servicename.test`
**Dependencies**: Imports `service-name-app` and test dependencies

```
/service-name-test
└── src/test
    ├── java/io/agentic/microagent/servicename/test
    │   └── FeatureNameIntegrationTest.java
    └── resources
        └── testcontainers.properties
```

---

## Package Naming Conventions

### Service-Specific Packages

| Module      | Package Pattern                                                | Example                                                     |
|-------------|----------------------------------------------------------------|-------------------------------------------------------------|
| API         | `io.agentic.microagent.{service}.api.features.{feature-name}`  | `io.agentic.microagent.registry.api.features.registration`  |
| App         | `io.agentic.microagent.{service}.app`                          | `io.agentic.microagent.registry.app`                        |
| Core        | `io.agentic.microagent.{service}.core.features.{feature-name}` | `io.agentic.microagent.registry.core.features.registration` |
| Data Access | `io.agentic.microagent.{service}.dataaccess.{datasource}`      | `io.agentic.microagent.registry.dataaccess.relational`      |
| Shared      | `io.agentic.microagent.{service}.shared`                       | `io.agentic.microagent.registry.shared`                     |
| Test        | `io.agentic.microagent.{service}.test`                         | `io.agentic.microagent.registry.test`                       |

---

## 📚 Architecture Patterns Used

- **Package-by-Feature**: Organize code by business feature, not technical layer
- **CQRS (Command Query Responsibility Segregation)**: Separate read/write operations
- **Hexagonal Architecture**: Core business logic isolated from infrastructure
- **Repository Pattern**: Abstract data access logic
- **DTO Pattern**: Separate API models from domain models

---

## ⚠️ Critical Architecture Rules

### Layer Dependency Rules
1. ❌ **NEVER** put business logic in API layer (controllers should only delegate to services)
2. ❌ **NEVER** let Core layer depend on Data Access layer (dependency inversion principle)
3. ❌ **NEVER** use JPA entities in Core layer (use domain entities instead)
4. ❌ **NEVER** expose domain entities directly in API responses (always use DTOs)
5. ❌ **NEVER** import infrastructure concerns into Core layer (Spring Data, JPA annotations, etc.)
6. ✅ **ALWAYS** use MapStruct for mapping between layers (compile-time safety)
7. ✅ **ALWAYS** follow CQRS naming: `{Verb}{Feature}{Command/Query}` (e.g., `CreateAgentCommand`)
8. ✅ **ALWAYS** implement Repository interface in Data Access layer (Core defines interface)
9. ✅ **ALWAYS** keep Shared module dependency-free (no dependencies on other service modules)

### Package-by-Feature Rules
10. ✅ **ALWAYS** group related classes by feature, not by technical layer
11. ✅ **ALWAYS** keep feature packages cohesive (high cohesion, low coupling)
12. ❌ **NEVER** create generic packages like `utils` or `helpers` at root level (use feature-specific utils)

---

## 🔧 Java Best Practices Rules

### 1. Naming Conventions

#### Class Naming
- **Services**: Use `{Feature}{Command/Query}Service` for interfaces, `{Feature}{Command/Query}ServiceImpl` for implementations
  ```java
  // ✅ Good
  public interface AgentRegistrationCommandService { }
  public class AgentRegistrationCommandServiceImpl implements AgentRegistrationCommandService { }

  // ❌ Bad
  public class AgentRegService { }
  public class AgentServiceImpl { }
  ```

- **Entities**: Use noun suffixes
  - Domain entities: `{Feature}`, `{Feature}Model`
  - JPA entities: `{Feature}JpaEntity`
  - DTOs: `{Feature}Request`, `{Feature}Response`, `{Feature}Dto`
  ```java
  // ✅ Good
  public class Agent { }           // Core domain entity
  public class AgentEntity { }           // Data access JPA entity
  public class CreateAgentRequest { }       // API request DTO

  // ❌ Bad
  public class AgentData { }                // Unclear purpose
  ```

- **Mappers**: Use `{Feature}{Layer}Mapper`
  ```java
  // ✅ Good
  @Mapper(componentModel = "spring")
  public interface AgentApiMapper { }       // API layer mapper

  @Mapper(componentModel = "spring")
  public interface AgentJpaMapper { }       // Data access mapper
  ```

- **Controllers**: Use `{Feature}Controller`
  ```java
  // ✅ Good
  @RestController
  @RequestMapping("/api/v1/agents")
  public class AgentRegistrationController { }

  // ❌ Bad
  @RestController
  public class AgentAPI { }
  ```
- **Controllers**: All APIs of Feature must be centralized into  `{Feature}Api.java` interface in shared module and implemented in `{Feature}Controller.java` in API module.
  ```java
  // ✅ Good
  public class AgentRegistrationApi {
    @PostMapping("/api/v1/agents")
    AgentProfileResponse createAgentProfile(@RequestBody @Valid CreateAgentProfileRequest request);
  
    @GetMapping("/api/v1/agents/{agentId}")
    AgentProfileResponse getAgentProfile(@RequestParam("agentId") String agentId);
  } 
  
  // ✅ Good
  @RestController
  public class AgentRegistrationController implements AgentRegistrationApi { 
    
    // ✅ Good
    @Override
    AgentProfileResponse createAgentProfile(@RequestBody @Valid CreateAgentProfileRequest request) {
        // do something
    }
    
    // ✅ Good
    @Override
    AgentProfileResponse getAgentProfile(String agentId) {
        // do something
    } 
  
  }

  // ❌ Bad
  @RestController
  @RequestMapping("/api/v1/agents")
  public class AgentRegistrationController {
        
    @PostMapping("/api/v1/agents")
    // ❌ Bad
    AgentProfileResponse createAgentProfile(@RequestBody @Valid CreateAgentProfileRequest request) {
        // do something
    }
    
    @GetMapping("/api/v1/agents/{agentId}")
    // ❌ Bad
    AgentProfileResponse getAgentProfile(String agentId) {
        // do something
    } 
  }
    
  ```

- **Repositories**: Use `{Feature}Repository` for interface, `{Feature}RepositoryImpl` for implementation
  ```java
  // ✅ Good - Core layer
  public interface AgentRepository { }

  // ✅ Good - Data Access layer
  public interface AgentJpaRepository extends JpaRepository<AgentJpaEntity, String> { }
  public class AgentRepositoryImpl implements AgentRepository { }
  ```

#### Method Naming
- **CQRS Commands**: Use verbs - `create`, `update`, `delete`, `register`, `activate`
  ```java
  // ✅ Good
  public Agent createAgent(CreateAgentCommand command);
  public void deleteAgent(DeleteAgentCommand command);

  // ❌ Bad
  public Agent agent(CreateAgentCommand command);  // Not a verb
  public void remove(String id);                            // Use 'delete' for consistency
  ```

- **CQRS Queries**: Use `get`, `find`, `search`, `list`, `count`
  ```java
  // ✅ Good
  public Agent getAgentById(GetAgentByIdQuery query);
  public List<Agent> findAgentsByStatus(FindAgentsByStatusQuery query);
  public Page<Agent> searchAgents(SearchAgentsQuery query);

  // ❌ Bad
  public Agent retrieveAgent(String id);           // Use 'get' for single item
  public List<Agent> agents();                     // Not descriptive
  ```

- **Boolean Methods**: Use `is`, `has`, `can`, `should`
  ```java
  // ✅ Good
  public boolean isActive();
  public boolean hasCapability(String capability);
  public boolean canExecuteTask(String taskType);

  // ❌ Bad
  public boolean active();                                  // Missing 'is' prefix
  public boolean checkCapability(String capability);        // Use 'has'
  ```

#### Variable Naming
- **Constants**: UPPER_SNAKE_CASE
  ```java
  // ✅ Good
  public static final String DEFAULT_AGENT_STATUS = "ACTIVE";
  public static final int MAX_RETRY_ATTEMPTS = 3;

  // ❌ Bad
  public static final String defaultAgentStatus = "ACTIVE";
  public static final int maxRetryAttempts = 3;
  ```

- **Variables**: camelCase, descriptive, no abbreviations
  ```java
  // ✅ Good
  String agentId;
  List<Agent> activeAgents;
  AgentRegistrationCommand registrationCommand;

  // ❌ Bad
  String aid;                                              // Abbreviation
  List<Agent> list;                               // Not descriptive
  AgentRegistrationCommand cmd;                            // Abbreviation
  ```

### 2. Code Organization Rules

#### Service Layer Structure
- ✅ **ALWAYS** separate Command and Query services
- ✅ **ALWAYS** use interfaces for service contracts
- ✅ **ALWAYS** annotate implementations with `@Service`
- ❌ **NEVER** mix commands and queries in the same service

```java
// ✅ Good - Command Service
public interface AgentCommandService {
    Agent createAgent(CreateAgentCommand command);
    void deleteAgent(DeleteAgentCommand command);
}

@Service
@RequiredArgsConstructor
public class AgentCommandServiceImpl implements AgentCommandService {
    private final AgentRepository agentRepository;

    @Override
    @Transactional
    public Agent createAgent(CreateAgentCommand command) {
        // Implementation
    }
}

// ✅ Good - Query Service
public interface AgentQueryService {
    Agent getAgentById(GetAgentByIdQuery query);
    List<Agent> findAgentsByStatus(FindAgentsByStatusQuery query);
}

@Service
@RequiredArgsConstructor
public class AgentQueryServiceImpl implements AgentQueryService {
    private final AgentRepository agentRepository;

    @Override
    @Transactional(readOnly = true)
    public Agent getAgentById(GetAgentByIdQuery query) {
        // Implementation
    }
}

// ❌ Bad - Mixed Commands and Queries
public interface AgentService {
    Agent createAgent(CreateAgentCommand command);  // Command
    Agent getAgentById(String id);                  // Query - mixed!
}
```

#### Controller Layer Structure
- ✅ **ALWAYS** use `@RestController` for REST APIs
- ✅ **ALWAYS** use `@RequestMapping` at class level for versioning
- ✅ **ALWAYS** use specific HTTP method annotations (`@GetMapping`, `@PostMapping`, etc.)
- ✅ **ALWAYS** validate request DTOs with `@Valid`
- ✅ **ALWAYS** return direct DTO with proper HTTP status code by using @ResponseStatus for explicit HTTP responses
- ❌ **NEVER** perform business logic in controllers

### 3. Exception Handling Rules

#### Custom Exceptions
- ✅ **ALWAYS** create parent exception class using the service name plus exception (e.g., `ServiceNameException`)
- ✅ **ALWAYS** create domain-specific exceptions in `shared` module and extends from parent exception
- ✅ **ALWAYS** extend from appropriate base exception (RuntimeException for unchecked)
- ✅ **ALWAYS** include meaningful error messages and error codes
- ✅ **ALWAYS** use `@ControllerAdvice` for global exception handling

```java
// ✅ Good - Custom Exception in shared module
public class AgentNotFoundException extends AgentException {
    private final String agentId;
    private final String errorCode;

    public AgentNotFoundException(String agentId) {
        super(String.format("Agent not found with ID: %s", agentId));
        this.agentId = agentId;
        this.errorCode = "AGENT_NOT_FOUND";
    }
}

public class AgentAlreadyExistsException extends AgentException {
    private final String agentName;
    private final String errorCode;

    public AgentAlreadyExistsException(String agentName) {
        super(String.format("Agent already exists with name: %s", agentName));
        this.agentName = agentName;
        this.errorCode = "AGENT_ALREADY_EXISTS";
    }
}

// ❌ Bad
public class AgentException extends Exception { }  // Too generic
public class Exception1 extends RuntimeException { }  // Non-descriptive name
```

### 4. Transaction Management Rules

- ✅ **ALWAYS** use `@Transactional` on service methods that modify data (Commands)
- ✅ **ALWAYS** use `@Transactional(readOnly = true)` on query methods
- ✅ **ALWAYS** handle transactions at service layer, NOT controller or repository layer
- ❌ **NEVER** use `@Transactional` on private methods (Spring AOP limitation)
- ❌ **NEVER** catch exceptions inside `@Transactional` methods without re-throwing (breaks rollback)

```java
// ✅ Good - Command Service with Transactions
@Service
@RequiredArgsConstructor
@Slf4j
public class AgentCommandServiceImpl implements AgentCommandService {
    private final AgentRepository agentRepository;

    @Override
    @Transactional
    public Agent createAgent(CreateAgentCommand command) {
        log.info("Creating agent: {}", command.getName());

        // Validation
        if (agentRepository.existsByName(command.getName())) {
            throw new AgentAlreadyExistsException(command.getName());
        }

        // Business logic
        Agent agent = Agent.builder()
                .id(UUID.randomUUID().toString())
                .name(command.getName())
                .status(AgentStatus.ACTIVE)
                .createdAt(LocalDateTime.now())
                .build();

        // Persist
        return agentRepository.save(agent);
    }

    @Override
    @Transactional
    public void deleteAgent(DeleteAgentCommand command) {
        Agent agent = agentRepository.findById(command.getAgentId())
                .orElseThrow(() -> new AgentNotFoundException(command.getAgentId()));

        agentRepository.delete(agent);
    }
}

// ✅ Good - Query Service with Read-Only Transactions
@Service
@RequiredArgsConstructor
public class AgentQueryServiceImpl implements AgentQueryService {
    private final AgentRepository agentRepository;

    @Override
    @Transactional(readOnly = true)
    public Agent getAgentById(GetAgentByIdQuery query) {
        return agentRepository.findById(query.getAgentId())
                .orElseThrow(() -> new AgentNotFoundException(query.getAgentId()));
    }

    @Override
    @Transactional(readOnly = true)
    public List<Agent> findAgentsByStatus(FindAgentsByStatusQuery query) {
        return agentRepository.findByStatus(query.getStatus());
    }
}

// ❌ Bad - Transaction in Controller
@RestController
public class AgentController {
    @PostMapping
    @Transactional  // BAD! Transactions belong in service layer
    public ResponseEntity<AgentResponse> createAgent(@RequestBody CreateAgentRequest request) {
        // ...
    }
}

// ❌ Bad - Swallowing exceptions in transactional method
@Transactional
public Agent createAgent(CreateAgentCommand command) {
    try {
        return agentRepository.save(agent);
    } catch (Exception e) {
        log.error("Error", e);
        return null;  // BAD! Transaction won't rollback
    }
}
```

### 5. Logging Best Practices

- ✅ **ALWAYS** use SLF4J with Lombok's `@Slf4j` annotation
- ✅ **ALWAYS** log at appropriate levels (DEBUG, INFO, WARN, ERROR)
- ✅ **ALWAYS** use parameterized logging (not string concatenation)
- ✅ **ALWAYS** log important business events (creation, updates, deletions)
- ❌ **NEVER** log sensitive data (passwords, tokens, PII)
- ❌ **NEVER** use `System.out.println()` or `printStackTrace()`

```java
// ✅ Good
@Service
@RequiredArgsConstructor
@Slf4j
public class AgentCommandServiceImpl implements AgentCommandService {

    @Override
    @Transactional
    public Agent createAgent(CreateAgentCommand command) {
        log.info("Creating agent with name: {}", command.getName());

        try {
            Agent agent = // ... create agent
            log.info("Successfully created agent with ID: {}", agent.getId());
            return agent;
        } catch (Exception e) {
            log.error("Failed to create agent: {}", command.getName(), e);
            throw e;
        }
    }

    @Override
    @Transactional
    public void deleteAgent(DeleteAgentCommand command) {
        log.warn("Deleting agent with ID: {}", command.getAgentId());
        // ... deletion logic
        log.info("Agent deleted successfully: {}", command.getAgentId());
    }
}

// ❌ Bad
@Service
public class AgentCommandServiceImpl implements AgentCommandService {

    public Agent createAgent(CreateAgentCommand command) {
        System.out.println("Creating agent");  // BAD! Use logger

        log.info("Creating agent: " + command.getName());  // BAD! Use parameterized logging

        log.info("User password: {}", command.getPassword());  // BAD! Logging sensitive data

        try {
            // ...
        } catch (Exception e) {
            e.printStackTrace();  // BAD! Use logger
        }
    }
}
```

### 6. Validation Rules

- ✅ **ALWAYS** use Jakarta Bean Validation annotations on DTOs (`@NotNull`, `@NotBlank`, `@Size`, etc.)
- ✅ **ALWAYS** validate at API boundary (controller layer) using `@Valid`
- ✅ **ALWAYS** perform business validation in service layer
- ✅ **ALWAYS** create custom validators for complex validation logic
- ✅ **ALWAYS** put customized validators logic in the shared module if it is shared across multiple features
- ❌ **NEVER** skip validation on incoming requests

```java
// ✅ Good - DTO with validation annotations
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateAgentRequest {

    @NotBlank(message = "Agent name is required")
    @Size(min = 3, max = 100, message = "Agent name must be between 3 and 100 characters")
    private String name;

    @NotBlank(message = "Agent type is required")
    @Pattern(regexp = "USER|ORDER|PAYMENT", message = "Invalid agent type")
    private String type;

    @NotNull(message = "Capabilities are required")
    @Size(min = 1, message = "At least one capability is required")
    private List<String> capabilities;

    @Email(message = "Invalid email format")
    private String contactEmail;
}

// ✅ Good - Controller using @Valid
@RestController
@RequiredArgsConstructor
public class AgentRegistrationController implements AgentRegistrationApi {

    @Override
    public AgentResponse createAgent(
            @Valid @RequestBody CreateAgentRequest request) {  // @Valid triggers validation
        // ...
    }
}

// ✅ Good - Business validation in service
@Service
@RequiredArgsConstructor
public class AgentCommandServiceImpl implements AgentCommandService {

    @Override
    @Transactional
    public Agent createAgent(CreateAgentCommand command) {
        // Business validation
        if (agentRepository.existsByName(command.getName())) {
            throw new AgentAlreadyExistsException(command.getName());
        }

        if (!isValidCapabilityCombination(command.getCapabilities())) {
            throw new InvalidCapabilityException("Invalid capability combination");
        }

        // ...
    }

    private boolean isValidCapabilityCombination(List<String> capabilities) {
        // Complex business validation logic
        return true;
    }
}

// ✅ Good - Custom Validator
@Component
public class AgentCapabilityValidator implements Validator {

    @Override
    public boolean supports(Class<?> clazz) {
        return CreateAgentRequest.class.equals(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {
        CreateAgentRequest request = (CreateAgentRequest) target;

        if (request.getCapabilities().contains("ADMIN") &&
            !request.getType().equals("USER")) {
            errors.rejectValue("capabilities", "invalid.capability",
                    "ADMIN capability is only allowed for USER type agents");
        }
    }
}

// ❌ Bad - No validation
public class CreateAgentRequest {
    private String name;  // No validation!
    private String type;
}

@PostMapping
public AgentResponse createAgent(@RequestBody CreateAgentRequest request) {
    // No @Valid - validation skipped!
}
```

### 7. Security Best Practices

- ✅ **ALWAYS** sanitize user input
- ✅ **ALWAYS** use parameterized queries (JPA/Hibernate handles this)
- ✅ **ALWAYS** implement proper authentication and authorization
- ✅ **ALWAYS** use HTTPS in production
- ❌ **NEVER** store passwords in plain text
- ❌ **NEVER** expose sensitive information in error messages
- ❌ **NEVER** trust user input

```java
// ✅ Good - Parameterized queries (JPA)
public interface AgentJpaRepository extends JpaRepository<AgentJpaEntity, String> {

    @Query("SELECT a FROM AgentJpaEntity a WHERE a.name = :name")
    Optional<AgentJpaEntity> findByName(@Param("name") String name);

    // Spring Data JPA method naming
    Optional<AgentJpaEntity> findByNameAndStatus(String name, AgentStatus status);
}

// ✅ Good - Secure error handling
@ExceptionHandler(AgentNotFoundException.class)
public ResponseEntity<ErrorResponse> handleAgentNotFound(AgentNotFoundException ex) {
    ErrorResponse error = ErrorResponse.builder()
            .errorCode("AGENT_NOT_FOUND")
            .message("The requested agent was not found")  // Generic message
            .timestamp(LocalDateTime.now())
            .build();
    // Don't expose internal details in production
    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
}

// ❌ Bad - SQL Injection vulnerability (if using native queries incorrectly)
@Query(value = "SELECT * FROM agents WHERE name = '" + name + "'", nativeQuery = true)
List<AgentJpaEntity> findByNameUnsafe(String name);  // NEVER DO THIS!

// ❌ Bad - Exposing sensitive information
@ExceptionHandler(Exception.class)
public ResponseEntity<String> handleError(Exception ex) {
    return ResponseEntity.status(500).body(ex.getMessage());  // May expose internal details
}
```

### 8. Performance Best Practices

- ✅ **ALWAYS** use pagination for large datasets
- ✅ **ALWAYS** use `@Transactional(readOnly = true)` for queries (performance optimization)
- ✅ **ALWAYS** use appropriate fetch strategies (LAZY vs EAGER)
- ✅ **ALWAYS** create database indexes on frequently queried fields
- ✅ **ALWAYS** use caching for frequently accessed data
- ❌ **NEVER** use `findAll()` without pagination on large tables
- ❌ **NEVER** perform N+1 queries

```java
// ✅ Good - Lazy loading with explicit fetch when needed
@Entity
public class AgentJpaEntity {

    @OneToMany(mappedBy = "agent", fetch = FetchType.LAZY)
    private List<CapabilityJpaEntity> capabilities;
}

// Use JOIN FETCH when you need the data
@Query("SELECT a FROM AgentJpaEntity a LEFT JOIN FETCH a.capabilities WHERE a.id = :id")
Optional<AgentJpaEntity> findByIdWithCapabilities(@Param("id") String id);

// ✅ Good - Caching
@Service
@RequiredArgsConstructor
@Slf4j
public class AgentQueryServiceImpl implements AgentQueryService {

    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = "agents", key = "#query.agentId")
    public Agent getAgentById(GetAgentByIdQuery query) {
        log.debug("Fetching agent from database: {}", query.getAgentId());
        return agentRepository.findById(query.getAgentId())
                .orElseThrow(() -> new AgentNotFoundException(query.getAgentId()));
    }
}

// ✅ Good - Batch processing
@Service
@RequiredArgsConstructor
public class AgentBatchService {

    private static final int BATCH_SIZE = 100;

    @Transactional
    public void processAgentsBatch(List<Agent> agents) {
        for (int i = 0; i < agents.size(); i += BATCH_SIZE) {
            int end = Math.min(i + BATCH_SIZE, agents.size());
            List<Agent> batch = agents.subList(i, end);
            agentRepository.saveAll(batch);

            // Flush and clear to avoid memory issues
            if (i % BATCH_SIZE == 0 && i > 0) {
                entityManager.flush();
                entityManager.clear();
            }
        }
    }
}

// ❌ Bad - No pagination
public List<Agent> getAllAgents() {
    return agentRepository.findAll();  // Could return millions of records!
}

// ❌ Bad - N+1 query problem
public List<AgentWithCapabilities> getAllAgentsWithCapabilities() {
    List<AgentJpaEntity> agents = agentRepository.findAll();
    return agents.stream()
            .map(agent -> {
                List<Capability> capabilities = agent.getCapabilities();  // N+1 queries!
                return new AgentWithCapabilities(agent, capabilities);
            })
            .collect(Collectors.toList());
}
```

### 9. Testing Best Practices

- ✅ **ALWAYS** write unit tests for service layer
- ✅ **ALWAYS** write integration tests for API endpoints
- ✅ **ALWAYS** use meaningful test method names: `should{ExpectedBehavior}_when{StateOrCondition}`
- ✅ **ALWAYS** follow AAA pattern: Arrange, Act, Assert
- ✅ **ALWAYS** use test fixtures and builders for test data
- ✅ **ALWAYS** test edge cases and error scenarios
- ❌ **NEVER** write tests that depend on other tests
- ❌ **NEVER** use production data in tests

```java
// ✅ Good - Unit Test for Service
@ExtendWith(MockitoExtension.class)
class AgentCommandServiceImplTest {

    @Mock
    private AgentRepository agentRepository;

    @InjectMocks
    private AgentCommandServiceImpl agentCommandService;

    @Test
    void shouldCreateAgent_whenValidCommandProvided() {
        // Arrange
        CreateAgentCommand command = CreateAgentCommand.builder()
                .name("Test Agent")
                .type(AgentType.USER)
                .capabilities(List.of("READ", "WRITE"))
                .build();

        Agent expectedAgent = Agent.builder()
                .id("agent-123")
                .name("Test Agent")
                .status(AgentStatus.ACTIVE)
                .build();

        when(agentRepository.existsByName(command.getName())).thenReturn(false);
        when(agentRepository.save(any(Agent.class))).thenReturn(expectedAgent);

        // Act
        Agent result = agentCommandService.createAgent(command);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getName()).isEqualTo("Test Agent");
        assertThat(result.getStatus()).isEqualTo(AgentStatus.ACTIVE);

        verify(agentRepository).existsByName(command.getName());
        verify(agentRepository).save(any(Agent.class));
    }

    @Test
    void shouldThrowException_whenAgentAlreadyExists() {
        // Arrange
        CreateAgentCommand command = CreateAgentCommand.builder()
                .name("Existing Agent")
                .build();

        when(agentRepository.existsByName(command.getName())).thenReturn(true);

        // Act & Assert
        assertThatThrownBy(() -> agentCommandService.createAgent(command))
                .isInstanceOf(AgentAlreadyExistsException.class)
                .hasMessageContaining("Existing Agent");

        verify(agentRepository).existsByName(command.getName());
        verify(agentRepository, never()).save(any());
    }
}

// ✅ Good - Integration Test
@SpringBootTest
@AutoConfigureMockMvc
@Testcontainers
class AgentRegistrationControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16.4")
            .withDatabaseName("testdb");

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
    }

    @Test
    void shouldCreateAgent_whenValidRequestProvided() throws Exception {
        // Arrange
        CreateAgentRequest request = CreateAgentRequest.builder()
                .name("Integration Test Agent")
                .type("USER")
                .capabilities(List.of("READ", "WRITE"))
                .build();

        // Act & Assert
        mockMvc.perform(post("/api/v1/agents")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("Integration Test Agent"))
                .andExpect(jsonPath("$.status").value("ACTIVE"));
    }

    @Test
    void shouldReturnBadRequest_whenInvalidRequestProvided() throws Exception {
        // Arrange
        CreateAgentRequest request = CreateAgentRequest.builder()
                .name("")  // Invalid - blank name
                .build();

        // Act & Assert
        mockMvc.perform(post("/api/v1/agents")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }
}

// ✅ Good - Test Data Builder
public class AgentTestDataBuilder {

    public static Agent buildDefaultAgent() {
        return Agent.builder()
                .id(UUID.randomUUID().toString())
                .name("Test Agent")
                .type(AgentType.USER)
                .status(AgentStatus.ACTIVE)
                .capabilities(List.of("READ", "WRITE"))
                .createdAt(LocalDateTime.now())
                .build();
    }

    public static Agent buildAgentWithStatus(AgentStatus status) {
        return buildDefaultAgent().toBuilder()
                .status(status)
                .build();
    }
}

// ❌ Bad - Poor test naming
@Test
void test1() { }  // Not descriptive

@Test
void testCreate() { }  // What scenario?

// ❌ Bad - Tests depending on execution order
@Test
@Order(1)
void createAgent() {
    // Creates agent with ID "123"
}

@Test
@Order(2)
void getAgent() {
    // Assumes agent "123" exists from previous test
}
```

### 10. Documentation Standards

- ✅ **ALWAYS** use Javadoc for public APIs, interfaces, and complex logic
- ✅ **ALWAYS** document business rules and constraints
- ✅ **ALWAYS** keep documentation up-to-date with code changes
- ✅ **ALWAYS** use `@param`, `@return`, `@throws` in Javadoc
- ❌ **NEVER** write obvious comments (code should be self-documenting)
- ❌ **NEVER** leave commented-out code in production

```java
// ✅ Good - Interface Javadoc
/**
 * Service interface for agent command operations following CQRS pattern.
 * This service handles all write operations for agent management including
 * creation, updates, and deletion.
 *
 * <p>All methods in this service are transactional and will rollback on any exception.</p>
 *
 * @see AgentQueryService for read operations
 * @since 1.0.0
 */
public interface AgentCommandService {

    /**
     * Creates a new agent in the system.
     *
     * <p>Business Rules:</p>
     * <ul>
     *   <li>Agent name must be unique across the system</li>
     *   <li>Agent must have at least one capability</li>
     *   <li>Agent status defaults to ACTIVE upon creation</li>
     * </ul>
     *
     * @param command the command containing agent creation details
     * @return the created agent  with generated ID and timestamps
     * @throws AgentAlreadyExistsException if an agent with the same name already exists
     * @throws InvalidCapabilityException if the capability combination is invalid
     */
    Agent createAgent(CreateAgentCommand command);

    /**
     * Deletes an agent from the system.
     *
     * <p>Note: This is a soft delete. The agent status is set to DELETED
     * but the record remains in the database for audit purposes.</p>
     *
     * @param command the command containing the agent ID to delete
     * @throws AgentNotFoundException if the agent does not exist
     */
    void deleteAgent(DeleteAgentCommand command);
}

// ✅ Good - Complex logic documentation
/**
 * Validates if the given capability combination is allowed for the agent type.
 *
 * <p>Validation Rules:</p>
 * <ul>
 *   <li>USER agents can have ADMIN capability</li>
 *   <li>ORDER agents cannot have USER_MANAGEMENT capability</li>
 *   <li>PAYMENT agents must have TRANSACTION capability</li>
 * </ul>
 *
 * @param type the agent type
 * @param capabilities the list of capabilities to validate
 * @return true if the combination is valid, false otherwise
 */
private boolean isValidCapabilityCombination(AgentType type, List<String> capabilities) {
    // Complex validation logic
}

// ✅ Good - Self-documenting code (no comment needed)
public Agent getActiveAgentById(String agentId) {
    return agentRepository.findByIdAndStatus(agentId, AgentStatus.ACTIVE)
            .orElseThrow(() -> new AgentNotFoundException(agentId));
}

// ❌ Bad - Obvious comments
// Get agent by ID
public Agent getAgentById(String id) { }  // Comment adds no value

// Set the name
agent.setName(name);  // Obvious

// ❌ Bad - Commented-out code
public void deleteAgent(String id) {
    agentRepository.delete(id);
    // agentRepository.softDelete(id);  // Remove this!
    // System.out.println("Deleted");   // Remove this!
}

// ❌ Bad - Outdated documentation
/**
 * Creates an agent and sends email notification
 * (NOTE: Email notification was removed 6 months ago!)
 */
public Agent createAgent(CreateAgentCommand command) {
    // No email sending anymore
}
```

### 11. MapStruct Best Practices

- ✅ **ALWAYS** use `componentModel = "spring"` for Spring integration
- ✅ **ALWAYS** define separate mappers for each layer (API, Core, Data Access)
- ✅ **ALWAYS** use `@Mapping` for non-matching field names
- ✅ **ALWAYS** handle collections and nested objects properly
- ❌ **NEVER** use manual mapping when MapStruct can generate it
- ❌ **NEVER** share mappers across layers

```java
// ✅ Good - API Layer Mapper
@Mapper(componentModel = "spring")
public interface AgentApiMapper {

    @Mapping(source = "agentName", target = "name")
    @Mapping(source = "agentType", target = "type")
    CreateAgentCommand toCommand(CreateAgentRequest request);

    @Mapping(source = "id", target = "agentId")
    @Mapping(source = "createdAt", target = "createdTimestamp")
    AgentResponse toResponse(Agent );

    List<AgentResponse> toResponseList(List<Agent> s);
}

// ✅ Good - Data Access Layer Mapper
@Mapper(componentModel = "spring")
public interface AgentJpaMapper {

    @Mapping(target = "createdAt", ignore = true)  // Set by JPA
    @Mapping(target = "updatedAt", ignore = true)  // Set by JPA
    AgentJpaEntity toJpaEntity(Agent agent);

    Agent toDomainEntity(AgentJpaEntity jpaEntity);

    List<Agent> toDomainEntityList(List<AgentJpaEntity> jpaEntities);
}

// ✅ Good - Custom mapping methods
@Mapper(componentModel = "spring")
public interface AgentCoreMapper {

    @Mapping(target = "capabilities", qualifiedByName = "stringToCapabilities")
    Agent to(CreateAgentCommand command);

    @Named("stringToCapabilities")
    default List<Capability> stringToCapabilities(List<String> capabilityNames) {
        return capabilityNames.stream()
                .map(name -> Capability.builder()
                        .name(name)
                        .enabled(true)
                        .build())
                .collect(Collectors.toList());
    }
}

// ❌ Bad - Manual mapping instead of MapStruct
public AgentResponse toResponse(Agent agent) {
    AgentResponse response = new AgentResponse();
    response.setAgentId(agent.getId());
    response.setName(agent.getName());
    response.setType(agent.getType().name());
    // ... many lines of manual mapping
    return response;
}
```

### 12. Lombok Best Practices

- ✅ **ALWAYS** use `@Data` for simple DTOs
- ✅ **ALWAYS** use `@Builder` for objects with many fields
- ✅ **ALWAYS** use `@RequiredArgsConstructor` for dependency injection
- ✅ **ALWAYS** use `@Slf4j` for logging
- ❌ **NEVER** use `@Data` on JPA entities (causes issues with bidirectional relationships)
- ❌ **NEVER** use `@EqualsAndHashCode` on entities with lazy loading

```java
// ✅ Good - DTO with Lombok
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateAgentRequest {
    @NotBlank
    private String name;

    @NotBlank
    private String type;

    private List<String> capabilities;
}

// ✅ Good - Service with Lombok
@Service
@RequiredArgsConstructor
@Slf4j
public class AgentCommandServiceImpl implements AgentCommandService {
    private final AgentRepository agentRepository;
    private final AgentEventPublisher eventPublisher;

    // No need for constructor, Lombok generates it
}

// ✅ Good - JPA Entity with selective Lombok
@Entity
@Table(name = "agents")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AgentJpaEntity {

    @Id
    private String id;

    @Column(nullable = false, unique = true)
    private String name;

    // Don't use @Data or @EqualsAndHashCode on JPA entities
}

// ✅ Good - Domain Entity with Lombok
@Getter
@Builder(toBuilder = true)
@EqualsAndHashCode(of = "id")
@ToString
public class Agent {
    private final String id;
    private final String name;
    private final AgentType type;
    private final AgentStatus status;
    private final List<Capability> capabilities;
    private final LocalDateTime createdAt;
}

// ❌ Bad - Using @Data on JPA entity
@Entity
@Data  // BAD! Can cause issues with lazy loading and bidirectional relationships
public class AgentJpaEntity {
    @OneToMany(mappedBy = "agent")
    private List<CapabilityJpaEntity> capabilities;
}
```

---

## 🌐 REST API Design & Conventions

This section defines **MANDATORY** REST API standards for all CRUD operations across the MicroAgent platform.

### 1. Resource Naming Conventions

#### General Rules
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

### 2. HTTP Methods for CRUD Operations

#### Standard CRUD Mapping

| Operation | HTTP Method | URL Pattern                  | Description                    |
|-----------|-------------|------------------------------|--------------------------------|
| Create    | POST        | `/api/v1/agents`             | Create a new agent             |
| Read One  | GET         | `/api/v1/agents/{id}`        | Get a single agent by ID       |
| Read All  | GET         | `/api/v1/agents`             | Get all agents (with pagination) |
| Update    | PUT         | `/api/v1/agents/{id}`        | Full update (replace entire resource) |
| Update    | PATCH       | `/api/v1/agents/{id}`        | Partial update (update specific fields) |
| Delete    | DELETE      | `/api/v1/agents/{id}`        | Delete an agent                |

#### Method Usage Rules

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
    @PostMapping("/api/v1/agents/{agentId}")
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
          @RequestParam(defaultValue = "{id}") String[] sort,
          @RequestParam(defaultValue = "desc") String direction,
          @RequestParam("filter.byStatuses") List<Status> filterByStatuses,
          @RequestParam("filter.fromDateTime") Long filterByDateTime,
          @RequestParam("filter.toDateTime") Long filterByDateTime);
    
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

### 3. URL Structure & Patterns

#### Base URL Structure
```
https://{domain}/api/{version}/{resource}/{id}/{sub-resource}/{sub-id}
```

#### Versioning
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

#### Path Parameters vs Query Parameters

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

### 4. Request & Response Conventions

#### Request Body Standards

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

#### Response Body Standards

- ✅ **ALWAYS** return consistent response structure
- ✅ **ALWAYS** include resource ID in responses
- ✅ **ALWAYS** include timestamps (createdAt, updatedAt)
- ✅ **ALWAYS** use wrapper objects for single resources

### 5. HTTP Status Codes

#### Standard Status Codes for CRUD Operations

| Operation | Success Status | Description                           |
|-----------|----------------|---------------------------------------|
| POST      | 201 Created    | Resource created successfully         |
| GET       | 200 OK         | Resource(s) retrieved successfully    |
| PUT       | 200 OK         | Resource updated successfully         |
| PATCH     | 200 OK         | Resource partially updated            |
| DELETE    | 204 No Content | Resource deleted successfully         |

#### Error Status Codes

| Status Code | Usage                                    |
|-------------|------------------------------------------|
| 400         | Bad Request (validation errors)          |
| 401         | Unauthorized (authentication required)   |
| 403         | Forbidden (insufficient permissions)     |
| 404         | Not Found (resource doesn't exist)       |
| 409         | Conflict (duplicate resource)            |
| 422         | Unprocessable Entity (business rule violation) |
| 500         | Internal Server Error                    |

### 6. Error Response Structure

#### Consistent Error Format

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
  "message": "Agent not found",
  "timestamp": "2026-01-04T10:30:00"
}

// ✅ Good - Validation Error Response
@Data
@Builder
public class ValidationErrorResponse {
    private String errorCode;
    private String message;
    private Map<String, String> fieldErrors;
    private LocalDateTime timestamp;
}

// Example response:
{
  "errorCode": "VALIDATION_ERROR",
  "message": "Validation failed",
  "details": [{
    "field": "agentName",
    "error": "Agent name is required"
  }],
  "timestamp": "2026-01-04T10:30:00"
}

// ❌ Bad - Inconsistent error responses
public String handleError(Exception e) {
    return e.getMessage();  // Just a string, not structured
}

public ResponseEntity<Map<String, Object>> handleError(Exception e) {
    return ResponseEntity.ok(Map.of("error", e.toString()));  // Exposes stack trace
}
```

### 7. Pagination, Filtering, and Sorting

#### Pagination Standards

- ✅ **ALWAYS** use pagination for list endpoints
- ✅ **ALWAYS** use `page` (0-based), `size` (default: 50), and `sort` (list of column fields name), `direction` (asc, desc)
- ✅ **ALWAYS** provide sensible defaults (page=0, size=50))
- ✅ **ALWAYS** return `PageResponse` object with metadata including (content (List of items), totalPage, totalElements, currentPage)

```java
// ✅ Good - Pagination implementation
@GetMapping
@ResponseStatus(HttpStatus.OK)
public PageResponse<AgentProfileResponse> queryAgentProfiles(
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "50") int size,
        @RequestParam(defaultValue = "{id}") String[] sort,
        @RequestParam(defaultValue = "desc") String direction,
        @RequestParam("filter.byStatuses") List<Status> filterByStatuses,
        @RequestParam("filter.fromDateTime") Long filterByDateTime,
        @RequestParam("filter.toDateTime") Long filterByDateTime);

// Example request:
GET /api/v1/agents?page=0&size=50&sort=createdAt,desc&sort=name,asc&filter.byStatuses=ACTIVE,INACTIVE&filter.fromDateTime=2026-01-01T00:00:00&filter.toDateTime=2026-01-31T23:59:59

// ❌ Bad - No pagination
@GetMapping
public List<AgentResponse> getAllAgents() {
    return agentService.findAll();  // Could return millions of records
}
```

### 8. Resource Relationships & Nested Resources

#### Parent-Child Relationship Patterns

- ✅ **ALWAYS** use nested URLs for parent-child relationships
- ✅ **ALWAYS** limit nesting to 5 levels maximum
- ✅ **ALWAYS** validate parent resource exists before accessing children

```java
// ✅ Good - Nested resource endpoints


public interface AgentTaskApi {
    @GetMapping("/api/v1/agents/{agentId}/tasks/{taskId}")
    @ResponseStatus(HttpStatus.OK)
    TaskResponse getAgentTask(
            @PathVariable String agentId,
            @PathVariable String taskId);

    @PostMapping("/api/v1/agents/{agentId}/tasks")
    @ResponseStatus(HttpStatus.CREATED)
    TaskResponse createAgentTask(
            @PathVariable String agentId,
            @Valid @RequestBody CreateTaskRequest request);

    @PutMapping("/api/v1/agents/{agentId}/tasks/{taskId}")
    @ResponseStatus(HttpStatus.OK)
    TaskResponse updateAgentTask(
            @PathVariable String agentId,
            @PathVariable String taskId,
            @Valid @RequestBody UpdateTaskRequest request);

    @DeleteMapping("/api/v1/agents/{agentId}/tasks/{taskId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    void deleteAgentTask(
            @PathVariable String agentId,
            @PathVariable String taskId);

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/api/v1/agents/{agentId}/tasks")
    PageResponse<AgentProfileResponse> queryAgentProfiles(
          @RequestParam(defaultValue = "0") int page,
          @RequestParam(defaultValue = "50") int size,
          @RequestParam(defaultValue = "{id}") String[] sort,
          @RequestParam(defaultValue = "desc") String direction,
          @PathVariable("agentId") String agentId,
          @RequestParam("filter.byStatuses") List<Status> filterByStatuses,
          @RequestParam("filter.fromDateTime") Long filterByDateTime,
          @RequestParam("filter.toDateTime") Long filterByDateTime);
}

// ❌ Bad - Too deep nesting (more than 5 levels)
GET /api/v1/agents/{agentId}/tasks/{taskId}/executions/{executionId}/logs/{logId}/traces/{traceId}/details/{detailId}
// Instead, use:
GET /api/v1/task-executions/{executionId}/logs/{logId}/traces/{traceId}/details/{detailId}

// ❌ Bad - Not validating parent exists
@GetMapping("/api/v1/agents/{agentId}/tasks/{taskId}")
public TaskResponse getTask(@PathVariable String agentId, @PathVariable String taskId) {
    // Only checking if task exists, not if it belongs to the agent
    return taskService.findById(taskId);
}
```

#### Many-to-Many Relationships

- ✅ **ALWAYS** represent many-to-many relationships with dedicated endpoints
- ✅ **ALWAYS** use verb phrases for relationship operations (e.g., `assign`, `remove`)

```java
// ✅ Good - Many-to-many relationship management
public interface AgentCapabilityApi {
    @GetMapping("/api/v1/agents/{agentId}/capabilities")
    @ResponseStatus(HttpStatus.OK)
    List<CapabilityResponse> getAgentCapabilities(@PathVariable String agentId);

    @PostMapping("/api/v1/agents/{agentId}/capabilities")
    @ResponseStatus(HttpStatus.OK)
    void assignCapabilities(
            @PathVariable String agentId,
            @Valid @RequestBody AssignCapabilitiesRequest request);

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

### 9. Special Operations & Custom Actions

#### Custom Actions Beyond CRUD

- ✅ **ALWAYS** use POST for custom actions that change state
- ✅ **ALWAYS** use clear verb names for action endpoints
- ✅ **ALWAYS** place action endpoints under the resource: `/{resource}/{id}/{action}`

```java
// ✅ Good - Custom action endpoints

public interface AgentActionApi {
    @PostMapping("/api/v1/agents/{agentId}/activate")
    @ResponseStatus(HttpStatus.OK)
    AgentResponse activateAgent(@PathVariable String agentId);

    @PostMapping("/api/v1/agents/{agentId}/deactivate")
    @ResponseStatus(HttpStatus.OK)
    AgentResponse deactivateAgent(@PathVariable String agentId);

    @PostMapping("/api/v1/agents/{agentId}/execute")
    @ResponseStatus(HttpStatus.OK)
    TaskExecutionResponse executeTask(
            @PathVariable String agentId,
            @Valid @RequestBody ExecuteTaskRequest request);

    @PostMapping("/api/v1/agents/bulk-activate")
    @ResponseStatus(HttpStatus.OK)
    BulkOperationResponse bulkActivate(
            @Valid @RequestBody BulkActivateRequest request);
}

// ❌ Bad - Using GET for state-changing operations
@GetMapping("/{id}/activate")  // BAD! GET should not change state
// Missing HTTP status annotation
public Agent activate(@PathVariable String id) { }

// ❌ Bad - Unclear action naming
@PostMapping("/{id}/do-something")  // BAD! Not descriptive
// Missing HTTP status annotation
public Agent doSomething(@PathVariable String id) { }
```

---

## 📋 Feature Implementation Checklist

When implementing a new feature, ensure you complete all of the following:

### API Layer (`service-name-api`)
- [ ] Create `{Feature}Controller` with appropriate HTTP mappings
- [ ] Use `@Valid` on all request DTOs
- [ ] Return directly DTO with proper HTTP status codes
- [ ] Create `{Feature}ApiMapper` interface with `@Mapper(componentModel = "spring")`
- [ ] Add request/response logging
- [ ] Handle all exceptions with `@ExceptionHandler` or global handler

### Core Layer (`service-name-core`)
- [ ] Create `{Feature}CommandService` interface for write operations
- [ ] Create `{Feature}QueryService` interface for read operations
- [ ] Create `{Feature}CommandServiceImpl` with `@Transactional` on write methods
- [ ] Create `{Feature}QueryServiceImpl` with `@Transactional(readOnly = true)` on read methods
- [ ] Create `{Feature}Repository` interface (not implementation!)
- [ ] Create domain entities (`{Feature}`, `{Feature}Entity`)
- [ ] Create CQRS commands (`Create{Feature}Command`, `Update{Feature}Command`, etc.)
- [ ] Create CQRS queries (`Get{Feature}ByIdQuery`, `Find{Feature}sQuery`, etc.)
- [ ] Implement business validation logic
- [ ] Create custom exceptions in `shared` module

### Data Access Layer (`service-name-data-access`)
- [ ] Create `{Feature}JpaEntity` with JPA annotations
- [ ] Create `{Feature}JpaRepository` extending `JpaRepository`
- [ ] Create `{Feature}RepositoryImpl` implementing core's `{Feature}Repository`
- [ ] Create `{Feature}JpaMapper` for JPA entity ↔ domain entity mapping
- [ ] Add database indexes on frequently queried fields
- [ ] Use appropriate fetch strategies (LAZY/EAGER)

### Shared Layer (`service-name-shared`)
- [ ] Create request DTOs in `http/features/{feature-name}/request/`
- [ ] Create response DTOs in `http/features/{feature-name}/response/`
- [ ] Add validation annotations to DTOs
- [ ] Create custom exceptions in `exceptions/`
- [ ] Create shared constants in `constants/`
- [ ] Create shared enums in `enums/`

### Testing (`service-name-test`)
- [ ] Write unit tests for service layer (minimum 80% coverage)
- [ ] Write integration tests for API endpoints
- [ ] Test all error scenarios
- [ ] Test validation rules
- [ ] Use TestContainers for database integration tests
- [ ] Follow AAA pattern (Arrange, Act, Assert)

### Documentation
- [ ] Add Javadoc to all public interfaces
- [ ] Document business rules in service interfaces
- [ ] Update API documentation (OpenAPI/Swagger)
- [ ] Update architecture documentation if needed

### Code Quality
- [ ] Run `mvn spotless:apply` for code formatting
- [ ] Run `mvn test` and ensure all tests pass
- [ ] Run `mvn verify` for integration tests
- [ ] No compiler warnings
- [ ] No SonarQube critical/major issues
- [ ] Code review completed

---

**Last Updated**: 2026-01-04
**Maintained By**: Architecture Team
