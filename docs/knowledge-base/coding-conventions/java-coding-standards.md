# Java Coding Standards

This document provides a comprehensive overview of Java coding standards for the MicroAgent platform, based on real implementations in user-service and payment-service.

---

## Quick Reference

| Topic | Document | Key Points |
|-------|----------|------------|
| Names | [Naming Conventions](./naming-conventions.md) | Services, Entities, DTOs, Mappers, Methods |
| Practices | [Best Practices](./best-practices.md) | Transactions, Logging, Validation, Performance |
| Review | [Code Review Checklist](./code-review-checklist.md) | Pre-commit checklist, Review priorities |

---

## Architecture Overview

### Multi-Module Maven Structure

Every microservice follows this structure:

```
service-name/
├── service-name-api          # REST API Layer
├── service-name-application   # Spring Boot Application
├── service-name-core         # Business Logic (CQRS)
├── service-name-data-access  # Data Persistence
├── service-name-shared       # DTOs, Exceptions, Constants
├── service-name-messaging    # Kafka Publishers/Consumers (optional)
├── service-name-scheduling   # Background Jobs (optional)
└── service-name-test         # Integration Tests
```

### Dependency Flow

```
         ┌─────────────┐
         │ Application │
         └──────┬──────┘
                │
       ┌────────┴────────┐
       │                 │
   ┌───▼───┐         ┌───▼────────────┐
   │  API  │         │  Data Access   │
   └───┬───┘         └───┬────────────┘
       │                 │
       │    ┌────────────┤
       │    │            │
   ┌───▼────▼───┐        │
   │    Core    │◄───────┘
   └─────┬──────┘
         │
   ┌─────▼──────┐
   │   Shared   │
   └────────────┘
```

**Rules:**
- API → Core, Shared
- Core → Shared (NO other dependencies)
- Data Access → Core, Shared
- Application → ALL modules
- Shared → NO dependencies

---

## Package Organization

### Package-by-Feature (in Core)

```
io.agentic.microagent.{service}.core.features.{feature}/
├── entities/        # Domain entities (immutable)
├── request/         # Commands and Queries (CQRS)
├── service/         # Service implementations
├── mapper/          # MapStruct mappers
├── events/          # Domain events
├── listeners/       # Event listeners
├── handler/         # Business logic handlers
├── valueobject/     # Value objects
├── config/          # Feature-specific configuration
└── validators/      # Business validators
```

### Package-by-Layer (in Data Access)

```
io.agentic.microagent.{service}.data.access.relational.{feature}/
├── entities/        # JPA entities (mutable)
├── repository/      # JPA repositories
├── mapper/          # Entity mappers (domain ↔ JPA)
├── projections/     # Query projections
└── converter/       # JPA converters
```

---

## Core Patterns

### 1. CQRS Pattern

**Separate Command and Query Services:**

```java
// Command Service - Write Operations
public interface UserCommandService {
    User createUser(CreateUserCommand command);
    User updateUser(UpdateUserCommand command);
    void deleteUser(DeleteUserCommand command);
}

@Service
@RequiredArgsConstructor
@Slf4j
public class UserCommandServiceImpl implements UserCommandService {

    @Override
    @Transactional
    public User createUser(CreateUserCommand command) {
        // Business logic
        return userRepository.save(user);
    }
}

// Query Service - Read Operations
public interface UserQueryService {
    Optional<User> findUserById(UserId id);
    Paging<User> queryUsers(UserPaginationQuery query);
}

@Service
@RequiredArgsConstructor
@Slf4j
public class UserQueryServiceImpl implements UserQueryService {

    @Override
    @Transactional(readOnly = true)
    public Optional<User> findUserById(UserId id) {
        return userRepository.findById(id);
    }
}
```

### 2. Repository Pattern

**Domain Repository Interface (in Core):**

```java
// core/features/users/UserRepository.java
public interface UserRepository {
    User save(User user);
    Optional<User> findById(UserId id);
    void delete(User user);
    boolean existsByEmail(String email);
}
```

**JPA Repository (in Data Access):**

```java
// data-access/relational/users/repository/JpaUserRepository.java
public interface JpaUserRepository extends BaseQuerydslRepository<UserEntity, Long> {
    Optional<UserEntity> findByEmail(String email);

    @EntityGraph(attributePaths = {"permissions", "userGroup"})
    Optional<UserEntity> findByIdWithPermissionsUserGroup(Long id);
}
```

**Repository Implementation (in Data Access):**

```java
// data-access/relational/users/UserRepositoryImpl.java
@Component
@RequiredArgsConstructor
public class UserRepositoryImpl implements UserRepository {
    private final JpaUserRepository jpaUserRepository;
    private final UserEntityMapper userEntityMapper;

    @Override
    public User save(User user) {
        var entity = userEntityMapper.toEntity(user);
        var saved = jpaUserRepository.save(entity);
        return userEntityMapper.toModel(saved);
    }

    @Override
    public Optional<User> findById(UserId id) {
        return jpaUserRepository.findById(id.getValue())
            .map(userEntityMapper::toModel);
    }
}
```

### 3. Mapper Pattern (MapStruct)

**Three Types of Mappers:**

1. **API Request Mapper** (HTTP DTO → Command/Query)
2. **API Response Mapper** (Domain → HTTP DTO)
3. **Entity Mapper** (Domain ↔ JPA Entity)

**Example - API Request Mapper:**

```java
// api/features/users/mapper/UserRequestMapper.java
@Mapper(config = AppMapStructConfiguration.class,
        uses = {MapstructCommonMapper.class, MapstructCommonDomainMapper.class})
public interface UserRequestMapper {
    UserRequestMapper INSTANCE = Mappers.getMapper(UserRequestMapper.class);

    @Mapping(target = "userId", ignore = true)
    CreateUserCommand toCreateUserCommand(AddUserRequest request);
}
```

**Example - API Response Mapper:**

```java
// api/features/users/mapper/UserResponseMapper.java
@Mapper(config = AppMapStructConfiguration.class)
public interface UserResponseMapper {
    UserResponseMapper INSTANCE = Mappers.getMapper(UserResponseMapper.class);

    @Mapping(target = "id", source = "id.value", qualifiedByName = "longToString")
    UserResponse toUserResponse(User user);

    List<UserResponse> toUserResponseList(List<User> users);
}
```

**Example - Entity Mapper:**

```java
// data-access/relational/users/mapper/UserEntityMapper.java
@Mapper(config = AppMapStructConfiguration.class)
public interface UserEntityMapper {
    UserEntityMapper INSTANCE = Mappers.getMapper(UserEntityMapper.class);

    // Domain → JPA Entity
    @Mapping(target = "id", ignore = true)
    UserEntity toEntity(User user);

    // JPA Entity → Domain
    @Mapping(target = "id", source = "id", qualifiedByName = "longToUserId")
    User toModel(UserEntity entity);

    @Named("longToUserId")
    default UserId longToUserId(Long id) {
        return id != null ? new UserId(id) : null;
    }
}
```

---

## Entity Design

### Domain Entities (Core Layer)

**Immutable with Lombok:**

```java
@Getter
@Builder(toBuilder = true)
@With
@RequiredArgsConstructor
@EqualsAndHashCode(of = "id")
public class User {
    private final UserId id;
    private final String email;
    private final Instant createdAt;
    private final Instant updatedAt;

    // Business methods
    public User activate() {
        return this.withStatus(UserStatus.ACTIVE);
    }

    public boolean isActive() {
        return UserStatus.ACTIVE.equals(this.status);
    }
}
```

**Key Points:**
- ✅ Immutable (no setters)
- ✅ Uses value objects for IDs
- ✅ Business methods return new instances
- ✅ NO JPA annotations
- ✅ Located in `core/features/{feature}/entities/`

### JPA Entities (Data Access Layer)

**Mutable with JPA Annotations:**

```java
@Entity
@Table(name = "users", indexes = {
    @Index(name = "idx_user_email", columnList = "email"),
    @Index(name = "idx_user_tenant", columnList = "tenant_id")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@SQLDelete(sql = "UPDATE users SET deleted_at = current_timestamp WHERE id = ?")
@SQLRestriction("deleted_at IS NULL")
@EntityListeners(AuditingEntityListener.class)
public class UserEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "email", nullable = false, unique = true)
    @Email
    @Length(max = 255)
    private String email;

    @Column(name = "first_name")
    @Length(max = 120)
    private String firstName;

    @Column(name = "last_name")
    @Length(max = 120)
    private String lastName;

    @Column(name = "user_status")
    @Enumerated(EnumType.STRING)
    @NotNull
    private UserStatus status;
    
    @CreatedDate
    @Column(name = "created_at", updatable = false)
    private Instant createdAt;

    @LastModifiedDate
    @Column(name = "updated_at")
    private Instant updatedAt;

    @Column(name = "deleted_at")
    private Instant deletedAt;
}
```

**Key Points:**
- ✅ Mutable (has setters for JPA)
- ✅ Suffixed with "Entity"
- ✅ Has JPA annotations
- ✅ Soft delete configured
- ✅ Audit annotations
- ✅ Indexes on frequently queried fields
- ✅ Lazy loading by default
- ✅ Located in `data-access/relational/{feature}/entities/`

---

## DTO Design

### Request DTOs

```java
@Getter
@Builder
@RequiredArgsConstructor
public class AddUserRequest {

    @NotEmpty(message = "Email is required")
    @Email(message = "Invalid email format")
    @Length(max = 255)
    private final String email;

    @NotEmpty(message = "First name is required")
    @Length(max = 120)
    private final String firstName;

    @Length(max = 120)
    private final String lastName;
    
    @NotNull
    @Valid  // Cascade validation
    private final List<String> permissions;
}
```

**Key Points:**
- ✅ Immutable
- ✅ Validation annotations
- ✅ Named: `{Action}{Feature}Request`
- ✅ Located in `shared/http/{feature}/request/`

### Response DTOs

```java
@Data
@Builder
public class UserResponse {
    private String id;
    private String email;
    private String firstName;
    private String lastName;
    private String createdAt;
    private String updatedAt;
}
```

**Key Points:**
- ✅ Mutable
- ✅ No validation (output only)
- ✅ Named: `{Feature}Response`
- ✅ Located in `shared/http/{feature}/response/`
- ✅ No sensitive data

---

## Command and Query Design

### Commands (Write Operations)

```java
@Getter
@Builder
@With
@RequiredArgsConstructor
public class CreateUserCommand {
    private final UserId userId;
    private final String email;
    private final String firstName;
    private final String lastName;
}
```

**Or using Java Records:**

```java
@Builder
@With
public record CreateUserCommand(
    UserId userId,
    String email,
    String firstName,
    String lastName
) {}
```

**Key Points:**
- ✅ Immutable with `@With` for modifications
- ✅ Named: `{Action}{Feature}Command`
- ✅ Located in `core/features/{feature}/request/`
- ✅ Contains all data for the operation

### Queries (Read Operations)

```java
@Getter
@Setter  // For builder pattern
@Builder
@With
public class FilterUserQuery {
    private final UserId byUserId;
    private final String byEmail;
    private final String byFirstName;
    private final String byLastName;
}
```

**Key Points:**
- ✅ Immutable with `@With`
- ✅ Named: `Filter{Feature}Query` or `{Feature}PaginationQuery`
- ✅ Located in `core/features/{feature}/request/`
- ✅ Contains filter/search criteria

---

## Exception Design

### Base Domain Exception

```java
public class UserDomainException extends DomainException {
    public UserDomainException(String message, Throwable cause) {
        super(message, cause);
    }

    public UserDomainException(String message) {
        super(message);
    }
}
```

### Specific Exceptions

```java
public class UserNotFoundException extends UserDomainException {
    public UserNotFoundException(String message) {
        super(message);
    }

    public UserNotFoundException() {
        super("User not found");
    }
}

public class UserAlreadyExistingException extends UserDomainException {
    public UserAlreadyExistingException(String message) {
        super(message);
    }

    public UserAlreadyExistingException() {
        super("User already exists");
    }
}
```

**Key Points:**
- ✅ All exceptions in `shared/exceptions/`
- ✅ Annotated with `@ErrorCodeValue`
- ✅ Named: `{Feature}{ErrorType}Exception`
- ✅ Have default and custom message constructors
- ✅ Extend from service base domain exception

---

## Controller Design

### API Interface (in Shared)

```java
public interface UserApi {

    @PostMapping("/api/v1/users")
    @ResponseStatus(HttpStatus.CREATED)
    UserResponse addNewUser(@Valid @RequestBody AddUserRequest request);

    @GetMapping("/api/v1/users/{userId}")
    @ResponseStatus(HttpStatus.OK)
    UserResponse getUser(@PathVariable("userId") Long userId);

    @GetMapping("/api/v1/users")
    @ResponseStatus(HttpStatus.OK)
    PageResponse<UserResponse> queryUsers(
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "50") int size,
        @RequestParam(defaultValue = "id") String[] sort,
        @RequestParam(defaultValue = "desc") String direction
    );

    @PutMapping("/api/v1/users/{userId}")
    @ResponseStatus(HttpStatus.OK)
    UserResponse updateUser(
        @PathVariable("userId") Long userId,
        @Valid @RequestBody UpdateUserRequest request
    );

    @DeleteMapping("/api/v1/users/{userId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    void deleteUser(@PathVariable("userId") Long userId);
}
```

### Controller Implementation (in API)

```java
@RestController
@RequiredArgsConstructor
public class UserController implements UserApi {

    private final UserCommandService userCommandService;
    private final UserQueryService userQueryService;

    @Override
    public UserResponse addNewUser(AddUserRequest request) {
        var command = UserRequestMapper.INSTANCE.toCreateUserCommand(request);
        var user = userCommandService.createUser(command);
        return UserResponseMapper.INSTANCE.toUserResponse(user);
    }

    @Override
    public UserResponse getUser(Long userId) {
        var user = userQueryService.getUserById(new UserId(userId));
        return UserResponseMapper.INSTANCE.toUserResponse(user);
    }

    @Override
    public PageResponse<UserResponse> queryUsers(
            int page, int size, String[] sort, String direction) {
        var query = UserPaginationQuery.builder()
            .page(page)
            .size(size)
            .sort(sort)
            .direction(direction)
            .build();

        var paging = userQueryService.queryUsers(query);

        return PageResponse.<UserResponse>builder()
            .content(UserResponseMapper.INSTANCE.toUserResponseList(paging.getContent()))
            .totalElements(paging.getTotalElements())
            .totalPages(paging.getTotalPages())
            .currentPage(paging.getCurrentPage())
            .build();
    }

    @Override
    public UserResponse updateUser(Long userId, UpdateUserRequest request) {
        var command = UserRequestMapper.INSTANCE.toUpdateUserCommand(userId, request);
        var user = userCommandService.updateUser(command);
        return UserResponseMapper.INSTANCE.toUserResponse(user);
    }

    @Override
    public void deleteUser(Long userId) {
        userCommandService.deleteUser(new UserId(userId));
    }
}
```

**Key Points:**
- ✅ Implements API interface from shared
- ✅ Uses `@RequiredArgsConstructor` for DI
- ✅ NO business logic (only delegation)
- ✅ Uses mappers for conversions
- ✅ Validation via `@Valid` in interface

---

## Quick Reference Tables

### Where Does Code Go?

| Component | Module | Location |
|-----------|--------|----------|
| REST Controller | api | `api/features/{feature}/` |
| API Interface | shared | `shared/http/apis/` |
| Request DTO | shared | `shared/http/{feature}/request/` |
| Response DTO | shared | `shared/http/{feature}/response/` |
| Domain Entity | core | `core/features/{feature}/entities/` |
| Command/Query | core | `core/features/{feature}/request/` |
| Service Interface | core | `core/features/{feature}/` |
| Service Impl | core | `core/features/{feature}/service/` |
| Repository Interface | core | `core/features/{feature}/` |
| JPA Entity | data-access | `data-access/relational/{feature}/entities/` |
| JPA Repository | data-access | `data-access/relational/{feature}/repository/` |
| Repository Impl | data-access | `data-access/relational/{feature}/` |
| Exception | shared | `shared/exceptions/` |
| API Mapper | api | `api/features/{feature}/mapper/` |
| Entity Mapper | data-access | `data-access/relational/{feature}/mapper/` |

### Lombok Annotations by Type

| Type | Annotations |
|------|-------------|
| DTO (Request/Response) | `@Getter`, `@Builder`, `@RequiredArgsConstructor` |
| Domain Entity | `@Getter`, `@Builder(toBuilder=true)`, `@With`, `@RequiredArgsConstructor` |
| JPA Entity | `@Getter`, `@Setter`, `@NoArgsConstructor`, `@AllArgsConstructor`, `@Builder` |
| Command/Query | `@Getter`, `@Builder`, `@With`, `@RequiredArgsConstructor` or `record` |
| Service | `@RequiredArgsConstructor`, `@Slf4j` |

### Transaction Annotations

| Service Type | Method Type | Annotation |
|--------------|-------------|------------|
| Command Service | Write | `@Transactional` |
| Query Service | Read | `@Transactional(readOnly = true)` |
| Controller | Any | NO `@Transactional` |

---

## Summary

### The 10 Commandments

1. ✅ **Use CQRS** - Separate command and query services
2. ✅ **Follow naming conventions** - Be consistent
3. ✅ **Immutable domain entities** - Use `@With` and `@Builder`
4. ✅ **Repository pattern** - Interface in core, impl in data-access
5. ✅ **MapStruct for mapping** - Three mapper types (Request, Response, Entity)
6. ✅ **Multi-layer validation** - DTO, business logic, entity
7. ✅ **Proper transactions** - `@Transactional` on services, not controllers
8. ✅ **Exception hierarchy** - Base domain exception + specific exceptions
9. ✅ **Package-by-feature** - Organize by business capability
10. ✅ **API-first design** - Define interfaces in shared module

---

**For More Details:**
- [Naming Conventions](./naming-conventions.md)
- [Best Practices](./best-practices.md)
- [Code Review Checklist](./code-review-checklist.md)

**Last Updated:** 2026-01-10
**Based on:** user-service and payment-service implementations
