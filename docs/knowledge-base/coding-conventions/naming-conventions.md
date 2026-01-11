# Naming Conventions

This document defines the **MANDATORY** naming conventions used across all MicroAgent services, derived from real implementations in user-service and payment-service.

---

## Table of Contents

1. [Package Naming](#package-naming)
2. [Class Naming](#class-naming)
3. [Interface Naming](#interface-naming)
4. [Method Naming](#method-naming)
5. [Variable Naming](#variable-naming)
6. [Constants Naming](#constants-naming)
7. [File Naming](#file-naming)

---

## Package Naming

### Base Package Pattern
```
io.agentic.microagent.{service-name}
```

**Examples:**
- `io.agentic.microagent.user.service`
- `io.agentic.microagent.payment`

### Module-Specific Packages

| Module | Package Pattern | Example |
|--------|----------------|---------|
| API/REST | `{base}.rest` or `{base}.api` | `io.agentic.microagent.user.rest` |
| Core | `{base}.core` | `io.agentic.microagent.payment.service.core` |
| Data Access | `{base}.data.access` | `io.agentic.microagent.payment.service.data.access` |
| Shared | `{base}.shared` | `io.agentic.microagent.user.shared` |
| Application | `{base}.app` or `{base}.application` | `io.agentic.microagent.user.service.app` |
| Messaging | `{base}.messaging` | `io.agentic.microagent.payment.messaging` |
| Test | `{base}.test` | `io.agentic.microagent.user.service.test` |

### Feature-Based Sub-Packages (in Core)

```
{base}.core.features.{feature-name}/
├── entities/        # Domain entities
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

**Examples:**
```java
io.agentic.microagent.user.service.core.features.users
io.agentic.microagent.user.service.core.features.cards
io.agentic.microagent.payment.service.core.features.methods
io.agentic.microagent.payment.service.core.features.sessions
```

### Data Access Sub-Packages

```
{base}.data.access/
├── relational/              # JPA/PostgreSQL
│   └── {feature}/
│       ├── entities/        # JPA entities
│       ├── repository/      # JPA repositories
│       ├── mapper/          # Entity mappers
│       ├── projections/     # Query projections
│       └── converter/       # JPA converters
├── dynamo/                  # DynamoDB (if used)
│   └── {feature}/
│       ├── entities/
│       ├── repository/
│       └── mapper/
└── clients/                 # External service clients
    └── {service}/
```

### Shared Module Sub-Packages

```
{base}.shared/
├── constants/              # Constants
├── enums/                  # Enumerations
├── exceptions/             # Exception classes
├── validations/            # Custom validators
└── http/                   # HTTP contracts
    ├── apis/               # API interfaces
    └── {feature}/
        ├── request/        # Request DTOs
        └── response/       # Response DTOs
```

---

## Class Naming

### Services

#### Service Interfaces

**Pattern:** `{Feature}CommandService` or `{Feature}QueryService`

**Rules:**
- ✅ Use `CommandService` for write operations
- ✅ Use `QueryService` for read operations
- ❌ NO "I" prefix for interfaces
- ❌ NO mixing commands and queries in the same service

**Examples:**
```java
// ✅ Good
public interface UserCommandService { }
public interface UserQueryService { }
public interface PaymentMethodCommandService { }
public interface PaymentSessionQueryService { }

// ❌ Bad
public interface IUserService { }              // NO "I" prefix
public interface UserService { }               // Not specific enough
public interface UserCRUDService { }           // Don't mix commands and queries
```

#### Service Implementations

**Pattern:** `{Feature}CommandServiceImpl` or `{Feature}QueryServiceImpl`

**Rules:**
- ✅ Always add "Impl" suffix
- ✅ Match the interface name exactly + "Impl"

**Examples:**
```java
// ✅ Good
@Service
public class UserCommandServiceImpl implements UserCommandService { }

@Service
public class PaymentMethodQueryServiceImpl implements PaymentMethodQueryService { }

// ❌ Bad
public class UserCommandServiceImplementation { }  // Too verbose
public class UserCmdSvc { }                        // Abbreviations
public class UserServiceImpl { }                   // Doesn't specify Command/Query
```

### Entities

#### Domain Entities (in Core)

**Pattern:** `{Entity}` (simple noun)

**Rules:**
- ✅ Use clear, business-meaningful names
- ✅ Singular form
- ✅ NO suffixes like "Entity", "Model", "Domain"
- ✅ Located in `core/features/{feature}/entities/`

**Examples:**
```java
// ✅ Good - Domain entities
public class User { }
public class UserCard { }
public class UserGroup { }
public class PaymentMethod { }
public class PaymentSession { }
public class PaymentTransaction { }

// ❌ Bad
public class UserEntity { }        // Don't use "Entity" suffix in core
public class UserModel { }         // Don't use "Model" suffix
public class UserDomain { }        // Don't use "Domain" suffix
public class Users { }             // Use singular, not plural
```

#### JPA Entities (in Data Access)

**Pattern:** `{Entity}Entity`

**Rules:**
- ✅ Always add "Entity" suffix to distinguish from domain entities
- ✅ Match domain entity name + "Entity"
- ✅ Located in `data-access/relational/{feature}/entities/`

**Examples:**
```java
// ✅ Good - JPA entities
@Entity
@Table(name = "users")
public class UserEntity { }

@Entity
@Table(name = "user_cards")
public class UserCardEntity { }

@Entity
@Table(name = "payment_methods")
public class PaymentMethodEntity { }

// ❌ Bad
@Entity
public class User { }              // Confusing with domain entity
@Entity
public class UserJpa { }           // Use "Entity" suffix, not "Jpa"
@Entity
public class UserData { }          // Not clear
```

### Repositories

#### Repository Interfaces (in Core)

**Pattern:** `{Feature}Repository`

**Rules:**
- ✅ Located in `core/features/{feature}/`
- ✅ Defines domain-level operations
- ❌ NO implementation here

**Examples:**
```java
// ✅ Good - Repository interface in core
public interface UserRepository {
    User save(User user);
    Optional<User> findById(UserId id);
}

public interface PaymentMethodRepository {
    PaymentMethod savePaymentMethod(PaymentMethod method);
    Optional<PaymentMethod> getPaymentMethod(PaymentMethodId id);
}

// ❌ Bad
public interface UserRepo { }                    // Don't abbreviate
public interface IUserRepository { }             // No "I" prefix
```

#### JPA Repositories (in Data Access)

**Pattern:** `Jpa{Feature}Repository`

**Rules:**
- ✅ Prefix with "Jpa" to distinguish from domain repository
- ✅ Extends Spring Data repository interfaces
- ✅ Located in `data-access/relational/{feature}/repository/`

**Examples:**
```java
// ✅ Good - JPA repository
public interface JpaUserRepository extends BaseQuerydslRepository<UserEntity, Long> {
    Optional<UserEntity> findByEmail(String email);
}

public interface JpaPaymentMethodRepository extends JpaRepository<PaymentMethodEntity, Long> {
    // Spring Data JPA methods
}

// ❌ Bad
public interface UserJpaRepository { }           // Prefix with "Jpa", not suffix
public interface UserEntityRepository { }        // Use "Jpa" prefix
```

#### Repository Implementations (in Data Access)

**Pattern:** `{Feature}RepositoryImpl`

**Rules:**
- ✅ Implements domain repository interface from core
- ✅ Suffix with "Impl"
- ✅ Delegates to JPA repository

**Examples:**
```java
// ✅ Good
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
}

// ❌ Bad
public class UserRepositoryImplementation { }    // Too verbose
public class UserRepoImpl { }                    // Don't abbreviate
```

### Controllers

**Pattern:** `{Feature}Controller`

**Rules:**
- ✅ Singular feature name
- ✅ Implements API interface from shared module
- ✅ Located in `rest/{feature}/` or `api/{feature}/`

**Examples:**
```java
// ✅ Good
@RestController
public class UserController implements UserApi {
    // Implements API interface
}

@RestController
public class PaymentMethodController implements PaymentMethodApi {
    // Implements API interface
}

// ❌ Bad
@RestController
public class UserAPI { }                         // Use "Controller" suffix
@RestController
public class UsersController { }                 // Use singular
@RestController
public class UserRestController { }              // Just "Controller" is enough
```

### DTOs (Data Transfer Objects)

#### Request DTOs

**Pattern:** `{Action}{Feature}Request`

**Rules:**
- ✅ Verb + Feature + "Request"
- ✅ Located in `shared/http/{feature}/request/`
- ✅ Immutable with validation annotations

**Examples:**
```java
// ✅ Good - Request DTOs
public class AddCustomerUserRequest { }
public class UpdateUserRequest { }
public class CreatePaymentMethodRequest { }
public class UpdatePaymentSessionRequest { }
public class DeleteUserRequest { }

// ❌ Bad
public class UserRequest { }                     // Not specific enough
public class UserDTO { }                         // Use "Request" suffix
public class AddUserRequestDTO { }               // Just "Request" is enough
public class UserAddRequest { }                  // Action first, then feature
```

#### Response DTOs

**Pattern:** `{Feature}Response`

**Rules:**
- ✅ Feature + "Response"
- ✅ Located in `shared/http/{feature}/response/`
- ✅ Immutable

**Examples:**
```java
// ✅ Good - Response DTOs
public class UserResponse { }
public class UserLiteResponse { }               // Lite version with fewer fields
public class PaymentMethodResponse { }
public class PaymentSessionResponse { }

// ❌ Bad
public class GetUserResponse { }                // Don't include action verb
public class UserResponseDTO { }                // Just "Response" is enough
public class ResponseUser { }                   // Response suffix, not prefix
```

### Commands and Queries (CQRS)

#### Commands

**Pattern:** `{Action}{Feature}Command`

**Rules:**
- ✅ Verb + Feature + "Command"
- ✅ Located in `core/features/{feature}/request/`
- ✅ Immutable using `@Builder`, `@With`, `@Getter`

**Examples:**
```java
// ✅ Good - Commands
@Builder
@With
@Getter
public class CreateUserCommand { }
public class UpdatePaymentMethodCommand { }
public class DeletePaymentSessionCommand { }
public class CreatePaymentTransactionCommand { }

// ❌ Bad
public class UserCommand { }                    // Not specific enough
public class UserCreateCommand { }              // Action first
public class CreateUserDTO { }                  // Use "Command" suffix
```

#### Queries

**Pattern:** `Filter{Feature}Query` or `{Feature}PaginationQuery` or `Get{Feature}Query`

**Rules:**
- ✅ Use "Filter" for filtering queries
- ✅ Use "Pagination" for paginated queries
- ✅ Use "Get" for single-item queries
- ✅ Located in `core/features/{feature}/request/`

**Examples:**
```java
// ✅ Good - Queries
@Builder
@With
@Getter
public class FilterUserQuery {
    private final UserId byUserId;
    private final String byEmail;
    private final Set<UserStatus> byUserStatuses;
}

public class UserPaginationQuery extends PaginationQuery {
    // Pagination parameters
}

public class GetSingleUserQuery { }
public class FilterPaymentMethodQuery { }

// ❌ Bad
public class UserQuery { }                      // Not specific enough
public class QueryUser { }                      // Wrong order
public class UserFilterQuery { }                // "Filter" prefix, not suffix for filter queries
```

### Mappers (MapStruct)

#### API Mappers (Request/Response)

**Pattern:** `{Feature}RequestMapper` or `{Feature}ResponseMapper`

**Rules:**
- ✅ Separate mappers for requests and responses
- ✅ Located in `rest/{feature}/mapper/` or `api/{feature}/mapper/`
- ✅ Use MapStruct `@Mapper` annotation

**Examples:**
```java
// ✅ Good - API mappers
@Mapper(config = AppMapStructConfiguration.class)
public interface UserRequestMapper {
    UserRequestMapper INSTANCE = Mappers.getMapper(UserRequestMapper.class);

    UpdateUserCommand toUpdateUserCommand(UpdateUserRequest request);
}

@Mapper(config = AppMapStructConfiguration.class)
public interface UserResponseMapper {
    UserResponseMapper INSTANCE = Mappers.getMapper(UserResponseMapper.class);

    UserResponse toUserResponse(User user);
}

// ❌ Bad
public interface UserMapper { }                // Not specific enough
public interface UserApiMapper { }             // Use Request/Response specifically
```

#### Entity Mappers (Domain ↔ JPA)

**Pattern:** `{Feature}EntityMapper`

**Rules:**
- ✅ Suffix with "EntityMapper"
- ✅ Located in `data-access/relational/{feature}/mapper/`
- ✅ Maps between domain entities and JPA entities

**Examples:**
```java
// ✅ Good - Entity mappers
@Mapper(config = AppMapStructConfiguration.class)
public interface UserEntityMapper {
    UserEntityMapper INSTANCE = Mappers.getMapper(UserEntityMapper.class);

    // Domain → JPA Entity
    UserEntity toEntity(User user);

    // JPA Entity → Domain
    User toModel(UserEntity entity);
}

@Mapper(config = AppMapStructConfiguration.class)
public interface PaymentMethodEntityMapper {
    // ...
}

// ❌ Bad
public interface UserDataMapper { }            // Use "EntityMapper"
public interface UserJpaMapper { }             // Use "EntityMapper"
```

#### Core Domain Mappers

**Pattern:** `{Feature}Mapper` or `{Feature}CoreMapper`

**Rules:**
- ✅ Located in `core/features/{feature}/mapper/`
- ✅ Used for internal domain transformations

**Examples:**
```java
// ✅ Good - Domain mappers
@Mapper(config = AppMapStructConfiguration.class)
public interface PaymentMethodMapper {
    PaymentMethod toCreate(CreatePaymentMethodCommand command);
    PaymentMethod toUpdate(PaymentMethod method, UpdatePaymentMethodCommand command);
}

// ❌ Bad
public interface PaymentMethodDomainMapper { }  // Just "Mapper" is fine
```

### Exceptions

#### Base Exception

**Pattern:** `{Service}DomainException`

**Rules:**
- ✅ One base exception per service
- ✅ Extends `DomainException` from common library
- ✅ Annotated with `@ErrorCodeValue`

**Examples:**
```java
// ✅ Good
public class UserDomainException extends DomainException { }

public class PaymentDomainException extends DomainException { }

// ❌ Bad
public class UserException { }                  // Use "DomainException"
public class UserServiceException { }           // Use "DomainException"
```

#### Specific Exceptions

**Pattern:** `{Feature}{ErrorType}Exception`

**Rules:**
- ✅ Feature + Error Type + "Exception"
- ✅ Extends service's base domain exception

**Examples:**
```java
// ✅ Good
public class UserNotFoundException extends UserDomainException { }

public class UserAlreadyExistingException extends UserDomainException { }

public class PaymentMethodNotFoundException extends PaymentDomainException { }
public class PaymentSessionExpiredException extends PaymentDomainException { }
public class UserRoleChangeNotAllowException extends UserDomainException { }

// ❌ Bad
public class NotFoundUserException { }          // Feature first
public class UserNotFound { }                   // Must end with "Exception"
public class UserError { }                      // Use "Exception"
```

### Events

**Pattern:** `{Feature}{Action}Event`

**Rules:**
- ✅ Feature + Past-tense action + "Event"
- ✅ Located in `core/features/{feature}/events/`
- ✅ Immutable using `@Builder`, `@Getter`

**Examples:**
```java
// ✅ Good
@Getter
@Builder
@RequiredArgsConstructor
public class UserCreatedEvent {
    private final UUID eventId;
    private final User user;
}

public class UserCreatedEvent { }
public class UserUpdatedEvent { }
public class PaymentTransactionCompletedEvent { }

// ❌ Bad
public class CreateUserEvent { }               // Use past tense
public class UserEvent { }                     // Not specific enough
```

### Value Objects

**Pattern:** Descriptive names without suffixes

**Rules:**
- ✅ Located in `core/features/{feature}/valueobject/`
- ✅ Immutable
- ✅ Named based on what they represent

**Examples:**
```java
// ✅ Good
public class PaymentSessionExpiryQueryResult { }
public class PaymentSettlementData { }
public class UserAddress { }

// ❌ Bad
public class PaymentSessionExpiryValueObject { }  // No "ValueObject" suffix
public class PaymentSessionExpiryVO { }           // No abbreviations
```

### Configuration Classes

**Pattern:** `{Module}{Purpose}Configuration` or `{Feature}Config`

**Rules:**
- ✅ Suffix with "Configuration" or "Config"
- ✅ Annotated with `@Configuration`

**Examples:**
```java
// ✅ Good
@Configuration
public class AppMapStructConfiguration { }

@Configuration
public class IntegrationTestConfiguration { }

@Configuration
public class RelationalDatabaseAccessConfig { }

// ❌ Bad
public class AppConfig { }                     // Be more specific
public class Configuration { }                 // Too generic
```

---

## Interface Naming

### API Interfaces (in Shared)

**Pattern:** `{Feature}Api`

**Rules:**
- ✅ Defines REST API contract
- ✅ Located in `shared/http/apis/`
- ✅ Can be used by controllers and Feign clients

**Examples:**
```java
// ✅ Good
public interface UserApi {
    @PostMapping("/api/v1/users")
    UserResponse createNewUser(@RequestBody AddUserRequest request);

    @GetMapping("/api/v1/users/{userId}")
    UserResponse getUser(@PathVariable Long userId);
}

public interface PaymentMethodApi { }

// ❌ Bad
public interface UserApiInterface { }          // Just "Api" suffix
public interface UserRestApi { }               // Just "Api" is enough
public interface IUserApi { }                  // No "I" prefix
```

---

## Method Naming

### Service Methods

#### Command Methods (Write Operations)

**Pattern:** Action verbs - `create`, `update`, `delete`, `add`, `remove`, `activate`, `deactivate`, `register`

**Examples:**
```java
// ✅ Good - Command service methods
public User createUser(CreateUserCommand command);
public User updateUser(UpdateUserCommand command);
public void deleteUser(DeleteUserCommand command);
public User activateUser(UserId userId);
public PaymentMethod updatePaymentMethod(UpdatePaymentMethodCommand command);

// ❌ Bad
public User user(CreateUserCommand command);   // Not a verb
public User getUser(...);                      // "get" is for queries
public User saveUser(...);                     // Use create/update
```

#### Query Methods (Read Operations)

**Pattern:** `get`, `find`, `query`, `search`, `count`, `exists`

**Rules:**
- ✅ `get` for single item (throws exception if not found)
- ✅ `find` for single item (returns Optional)
- ✅ `query` or `search` for multiple items
- ✅ `count` for counting
- ✅ `exists` for boolean checks

**Examples:**
```java
// ✅ Good - Query service methods
public User getUserById(UserId id);                           // Single item, throws if not found
public Optional<User> findUserByEmail(String email);          // Single item, Optional
public Page<User> queryUsers(UserPaginationQuery query);   // Paginated results
public long countUsersByStatus(UserStatus status);           // Count
public boolean existsUserByEmail(String email);              // Boolean check

// ❌ Bad
public User retrieveUser(UserId id);           // Use "get"
public List<User> users();                     // Not descriptive
public Optional<User> getUserByEmail(...);     // Use "find" for Optional
```

### Repository Methods

**Same as query service methods**, plus:

**Examples:**
```java
// ✅ Good - Repository methods
User save(User user);
void delete(User user);
Optional<User> findById(UserId id);
List<User> findAll();
boolean existsById(UserId id);

// ❌ Bad
User persist(User user);                       // Use "save"
User store(User user);                         // Use "save"
```

### Boolean Methods

**Pattern:** `is`, `has`, `can`, `should`

**Examples:**
```java
// ✅ Good - Boolean methods
public boolean isActive();
public boolean hasPermission(String permission);
public boolean canExecuteTask(String taskType);
public boolean shouldNotifyUser();
public boolean isTrustedSourceAccess();

// ❌ Bad
public boolean active();                       // Missing "is" prefix
public boolean checkPermission(...);           // Use "has"
public boolean getActive();                    // Use "is", not "get"
```

### Mapper Methods

**Pattern:** `to{TargetType}`, or custom names for complex mappings

**Examples:**
```java
// ✅ Good - Mapper methods
CreateUserCommand toCreateUserCommand(AddUserRequest request);
User toModel(UserEntity entity);
UserEntity toEntity(User user);
UserResponse toUserResponse(User user);
List<UserResponse> toUserResponseList(List<User> users);

// Custom mappings
@Named("longToUserId")
UserId longToUserId(Long id);

// ❌ Bad
CreateUserCommand map(AddUserRequest request);     // Not specific
CreateUserCommand convert(AddUserRequest request);  // Use "to"
```

---

## Variable Naming

### Local Variables

**Rules:**
- ✅ camelCase
- ✅ Descriptive, no abbreviations
- ✅ Use full words

**Examples:**
```java
// ✅ Good
String userName;
List<User> activeUsers;
PaymentMethod paymentMethod;
UserCommandService userCommandService;

// ❌ Bad
String usrName;              // No abbreviations
String un;                   // Too short
List<User> list;             // Not descriptive
UserCommandService ucs;      // No abbreviations
```

### Parameters

**Same rules as local variables**

**Examples:**
```java
// ✅ Good
public User createUser(CreateUserCommand command);
public void sendEmail(String emailAddress, String subject, String body);

// ❌ Bad
public User createUser(CreateUserCommand cmd);    // No abbreviations
public void sendEmail(String addr, String subj, String b);  // Too short
```

### Field Names

**Rules:**
- ✅ camelCase for regular fields
- ✅ Prefix with underscore for private fields (optional, based on team preference)
- ✅ Descriptive names

**Examples:**
```java
// ✅ Good
private final UserRepository userRepository;
private final String emailAddress;
private final LocalDateTime createdAt;

// Optional underscore prefix (if team prefers)
private final UserRepository _userRepository;

// ❌ Bad
private final UserRepository repo;            // No abbreviations
private final UserRepository ur;              // Too short
```

---

## Constants Naming

### Static Final Constants

**Pattern:** `UPPER_SNAKE_CASE`

**Examples:**
```java
// ✅ Good
public static final String DEFAULT_USER_STATUS = "ACTIVE";
public static final int MAX_RETRY_ATTEMPTS = 3;
public static final long TIMEOUT_MILLISECONDS = 5000L;
public static final String ERROR_CODE_USER_NOT_FOUND = "USER_NOT_FOUND";

// ❌ Bad
public static final String defaultUserStatus = "ACTIVE";  // Use UPPER_SNAKE_CASE
public static final String DEFAULT_STATUS = "ACTIVE";     // Be more specific
```

### Enum Constants

**Pattern:** `UPPER_SNAKE_CASE`

**Examples:**
```java
// ✅ Good
public enum UserStatus {
    CREATED,
    ACTIVE,
    INACTIVE,
    BLOCKED,
    ARCHIVED
}

public enum PaymentProcessorId {
    STRIPE_WEB_PAYMENT,
    STRIPE_CONNECT_PAYMENT,
    NETS_TERMINAL_PAYMENT,
    CASH_PAYMENT
}

// ❌ Bad
public enum UserStatus {
    Created,             // Use UPPER_SNAKE_CASE
    active,              // Use UPPER_SNAKE_CASE
}
```

---

## File Naming

### Java Files

**Rules:**
- ✅ Match the public class name exactly
- ✅ PascalCase

**Examples:**
```
UserCommandService.java
UserCommandServiceImpl.java
UserEntity.java
AddUserRequest.java
UserResponse.java
```

### Test Files

**Pattern:** `{ClassName}Test.java` or `{ClassName}IntegrationTest.java`

**Examples:**
```
UserCommandServiceImplTest.java
UserControllerIntegrationTest.java
PaymentMethodControllerIntegrationTest.java
```

### Configuration Files

**Pattern:** `{Module}Configuration.java` or `{Purpose}Config.java`

**Examples:**
```
AppMapStructConfiguration.java
IntegrationTestConfiguration.java
RelationalDatabaseAccessConfig.java
```

---

## Summary Checklist

When naming any element, ask yourself:

- [ ] Is it descriptive and self-documenting?
- [ ] Does it follow the established pattern for its type?
- [ ] Is it free of abbreviations?
- [ ] Does it match the team's conventions?
- [ ] Is it consistent with similar elements in the codebase?

**Remember:** Good naming makes code self-documenting and maintainable!

---

**Last Updated:** 2026-01-10
**Based on:** user-service and payment-service implementations
