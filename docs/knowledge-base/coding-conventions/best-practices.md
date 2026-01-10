# Best Practices

This document outlines **MANDATORY** best practices derived from real implementations in user-service and payment-service.

---

## Table of Contents

1. [Transaction Management](#transaction-management)
2. [Exception Handling](#exception-handling)
3. [Logging](#logging)
4. [Validation](#validation)
5. [Security](#security)
6. [Performance](#performance)
7. [MapStruct Usage](#mapstruct-usage)
8. [Lombok Usage](#lombok-usage)
9. [Immutability](#immutability)
10. [Database Practices](#database-practices)

---

## Transaction Management
### Query Services

✅ **ALWAYS** use `@Transactional(readOnly = true)` on query service methods

```java
// ✅ Good
@Service
@RequiredArgsConstructor
public class UserQueryServiceImpl implements UserQueryService {

    @Override
    @Transactional(readOnly = true)
    public Optional<User> getUserById(UserId id) {
        return userRepository.findById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public Paging<User> queryUsers(UserPaginationQuery query) {
        return userRepository.queryUsers(query);
    }
}

// ❌ Bad
@Service
public class UserQueryServiceImpl implements UserQueryService {

    public Optional<User> getUserById(UserId id) {
        // Missing readOnly optimization
        return userRepository.findById(id);
    }
}
```

### Transaction Rules

❌ **NEVER** use `@Transactional` on:
- Private methods (Spring AOP limitation)
- Controller methods (transactions belong in service layer)
- Repository methods (managed by Spring Data)

❌ **NEVER** catch exceptions in transactional methods without re-throwing

```java
// ❌ Bad - Swallows exception, breaks rollback
@Transactional
public User createUser(CreateUserCommand command) {
    try {
        return userRepository.save(user);
    } catch (Exception e) {
        log.error("Error creating user", e);
        return null;  // Transaction won't rollback!
    }
}

// ✅ Good - Re-throws exception
@Transactional
public User createUser(CreateUserCommand command) {
    try {
        return userRepository.save(user);
    } catch (DataIntegrityViolationException e) {
        log.error("Data integrity violation", e);
        throw new UserAlreadyExistingException("User already exists", e);
    }
}
```

---

## Exception Handling

### Exception Hierarchy

✅ **ALWAYS** create a base domain exception for your service

```java
// ✅ Good - Base exception
public class UserDomainException extends DomainException {
    public UserDomainException(String message, Throwable cause) {
        super(message, cause);
    }

    public UserDomainException(String message) {
        super(message);
    }
}

// ✅ Good - Specific exceptions
public class UserNotFoundException extends UserDomainException {
    public UserNotFoundException(String message) {
        super(message);
    }

    public UserNotFoundException() {
        super("User not found");
    }
}
```

### Exception Messages

✅ **ALWAYS** provide meaningful error messages

```java
// ✅ Good
throw new UserNotFoundException("User not found with ID: " + userId.getValue());
throw new UserEmailAlreadyExistingException("Email address already in use: " + email);

// ❌ Bad
throw new UserNotFoundException("Not found");
throw new UserException("Error");
```

### Exception Location

✅ **ALWAYS** place exceptions in `shared/exceptions/`

This allows all modules to access them without circular dependencies.

---

## Logging

### SLF4J with Lombok

✅ **ALWAYS** use `@Slf4j` annotation

```java
// ✅ Good
@Service
@RequiredArgsConstructor
@Slf4j
public class UserCommandServiceImpl implements UserCommandService {

    @Override
    @Transactional
    public User createUser(CreateUserCommand command) {
        log.info("Creating user with email: {}", command.getEmail());

        try {
            User user = // create user
            log.info("Successfully created user with ID: {}", user.getId().getValue());
            return user;
        } catch (Exception e) {
            log.error("Failed to create user: {}", command.getEmail(), e);
            throw e;
        }
    }
}

// ❌ Bad
@Service
public class UserCommandServiceImpl implements UserCommandService {
    private static final Logger log = LoggerFactory.getLogger(UserCommandServiceImpl.class);
    // Verbose, use @Slf4j instead
}

// ❌ Bad
System.out.println("Creating user");  // Never use System.out
e.printStackTrace();                  // Never use printStackTrace
```

### Log Levels

Use appropriate log levels:

- **DEBUG**: Detailed diagnostic information
- **INFO**: Important business events (creation, updates, deletions)
- **WARN**: Potentially harmful situations
- **ERROR**: Error events that might still allow the application to continue

```java
// ✅ Good
log.debug("Validating user command: {}", command);
log.info("Creating user with email: {}", command.getEmail());
log.warn("User status change may affect permissions: {}", userId);
log.error("Failed to create user: {}", command.getEmail(), e);

// ❌ Bad
log.info("Entering method");  // Too verbose, use DEBUG
log.error("User created");    // Not an error, use INFO
```

### Parameterized Logging

✅ **ALWAYS** use parameterized logging (not string concatenation)

```java
// ✅ Good
log.info("Creating user with email: {}", command.getEmail());
log.error("Failed to process payment: {}, amount: {}", paymentId, amount, exception);

// ❌ Bad
log.info("Creating user with email: " + command.getEmail());  // String concatenation
```

### Sensitive Data

❌ **NEVER** log sensitive data

```java
// ❌ Bad - Logging sensitive data
log.info("User password: {}", command.getPassword());
log.info("Credit card: {}", paymentMethod.getCardNumber());
log.info("SSN: {}", user.getSocialSecurityNumber());

// ✅ Good
log.info("User authentication successful for email: {}", email);
log.info("Payment processed for user: {}", userId);
```

---

## Validation

### Multi-Layer Validation

Implement validation at multiple layers:

1. **DTO Layer** - Jakarta Bean Validation
2. **Business Logic Layer** - Domain rules
3. **Entity Layer** - Database constraints

### DTO Validation

✅ **ALWAYS** use Jakarta Bean Validation annotations on DTOs

```java
// ✅ Good
@Getter
@Builder
@RequiredArgsConstructor
public class CreateUserRequest {

    @NotEmpty(message = "Email is required")
    @Email(message = "Invalid email format")
    @Length(max = 255)
    private final String email;

    @NotEmpty(message = "First name is required")
    @Length(max = 120)
    private final String firstName;

    @Length(max = 120)
    private final String lastName;

    @PhoneNumber  // Custom validator
    @Length(max = 20)
    private final String phoneNumber;

    @NotNull
    @Valid  // Cascade validation
    private final AddressRequest address;
}

// ❌ Bad - No validation
public class CreateUserRequest {
    private String email;    // No validation
    private String firstName;
}
```

### Controller Validation

✅ **ALWAYS** use `@Valid` in controller methods

```java
// ✅ Good
@RestController
public class UserController implements UserApi {

    @Override
    public UserResponse createNewUser(@Valid @RequestBody AddUserRequest request) {
        // @Valid triggers validation
        var command = UserRequestMapper.INSTANCE.toCreateUserCommand(request);
        var user = userCommandService.createUser(command);
        return UserResponseMapper.INSTANCE.toUserResponse(user);
    }
}

// ❌ Bad - Missing @Valid
@Override
public UserResponse createNewUser(@RequestBody AddUserRequest request) {
    // Validation not triggered
}
```

### Business Logic Validation

✅ **ALWAYS** validate business rules in service layer

```java
// ✅ Good
@Service
@RequiredArgsConstructor
public class UserCommandServiceImpl implements UserCommandService {

    @Override
    @Transactional
    public User createUser(CreateUserCommand command) {
        // Business validation
        if (userRepository.existsByEmail(command.getEmail())) {
            throw new UserEmailAlreadyExistingException(
                "Email address already in use: " + command.getEmail());
        }

        // Validate business rules
        validateUserRole(command.getUserRole());

        // Create user
        return userRepository.save(user);
    }

    private void validateUserRole(UserRole role) {
        if (!role.isActive()) {
            throw new UserRoleNotActiveException("Cannot assign inactive role");
        }
    }
}
```

### Custom Validators

✅ Create custom validators for complex rules

```java
// ✅ Good - Custom constraint
@Documented
@Constraint(validatedBy = AllowedUserAccessValidator.class)
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface AllowedUserAccess {
    UserStatus[] allowed();
    String message() default "Only statuses {allowed} are allowed";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}

// Validator implementation
public class AllowedUserAccessValidator
        implements ConstraintValidator<AllowedUserStatus, UserStatus> {

    private Set<UserStatus> allowedStatuses;

    @Override
    public void initialize(AllowedUserStatus annotation) {
        this.allowedStatuses = Set.of(annotation.allowed());
    }

    @Override
    public boolean isValid(UserStatus status, ConstraintValidatorContext context) {
        if (status == null) return true;
        return allowedStatuses.contains(status);
    }
}
```

---

## Security

### Input Sanitization

✅ **ALWAYS** sanitize user input

```java
// ✅ Good
public User createUser(CreateUserCommand command) {
    // Validation ensures email format is correct
    // Trim and normalize input
    String normalizedEmail = command.getEmail().trim().toLowerCase();

    // Business validation
    if (userRepository.existsByEmail(normalizedEmail)) {
        throw new UserEmailAlreadyExistingException("Email already exists");
    }

    // ...
}
```

### Parameterized Queries

✅ **ALWAYS** use JPA/QueryDSL (prevents SQL injection)

```java
// ✅ Good - JPA method (Spring Data)
public interface JpaUserRepository extends JpaRepository<UserEntity, Long> {
    Optional<UserEntity> findByEmail(String email);  // Parameterized
}

// ✅ Good - QueryDSL
public List<UserEntity> findUsers(FilterUserQuery filter) {
    QUserEntity user = QUserEntity.userEntity;

    BooleanBuilder predicate = new BooleanBuilder();

    if (filter.getByEmail() != null) {
        predicate.and(user.email.eq(filter.getByEmail()));
    }

    return queryFactory.selectFrom(user)
        .where(predicate)
        .fetch();
}

// ❌ Bad - Native query with concatenation (SQL INJECTION RISK)
@Query(value = "SELECT * FROM users WHERE email = '" + email + "'", nativeQuery = true)
List<UserEntity> findByEmailUnsafe(String email);  // NEVER DO THIS!
```

### Error Messages

❌ **NEVER** expose sensitive information in error messages

```java
// ✅ Good
@ExceptionHandler(UserNotFoundException.class)
public ResponseEntity<ErrorResponse> handleUserNotFound(UserNotFoundException ex) {
    ErrorResponse error = ErrorResponse.builder()
        .errorCode("USER_NOT_FOUND")
        .message("The requested user was not found")  // Generic message
        .timestamp(LocalDateTime.now())
        .build();
    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
}

// ❌ Bad
@ExceptionHandler(Exception.class)
public ResponseEntity<String> handleError(Exception ex) {
    // Exposes stack trace and internal details
    return ResponseEntity.status(500).body(ex.toString() + "\n" + ex.getStackTrace());
}
```

---

## Performance

### Pagination

✅ **ALWAYS** use pagination for list endpoints

```java
// ✅ Good
@Override
@Transactional(readOnly = true)
public Paging<User> queryUsers(UserPaginationQuery query) {
    Page<UserEntity> page = userRepository.findAll(
        buildPredicate(query),
        PageRequest.of(query.getPage(), query.getSize())
    );

    return new Paging<>(
        page.getContent().stream()
            .map(UserEntityMapper.INSTANCE::toModel)
            .toList(),
        page.getTotalElements(),
        page.getNumber(),
        page.getSize()
    );
}

// ❌ Bad - No pagination
public List<User> getAllUsers() {
    return userRepository.findAll();  // Could return millions!
}
```

### Lazy Loading

✅ **ALWAYS** use lazy loading by default, fetch eagerly when needed

```java
// ✅ Good - Lazy by default
@Entity
@Table(name = "users")
public class UserEntity {

    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY)
    private List<UserAccessEntity> permissions;
}

// Fetch eagerly when needed
@EntityGraph(attributePaths = {"permissions", "userGroup"})
Optional<UserEntity> findByIdWithPermissionsAndGroup(Long id);

// ❌ Bad - Eager loading everything
@OneToMany(mappedBy = "user", fetch = FetchType.EAGER)
private List<UserAccessEntity> permissions;  // Always loaded
```

### N+1 Query Problem

✅ **ALWAYS** use JOIN FETCH or EntityGraph to avoid N+1 queries

```java
// ✅ Good - Using JOIN FETCH
@Query("SELECT u FROM UserEntity u LEFT JOIN FETCH u.permissions WHERE u.id = :id")
Optional<UserEntity> findByIdWithPermissions(@Param("id") Long id);

// ✅ Good - Using EntityGraph
@EntityGraph(attributePaths = {"permissions", "userGroup"})
Optional<UserEntity> findById(Long id);

// ❌ Bad - N+1 queries
public List<UserWithPermissions> getAllUsersWithPermissions() {
    List<UserEntity> users = userRepository.findAll();
    return users.stream()
        .map(user -> {
            // This causes N+1 queries!
            List<Permission> permissions = user.getPermissions();
            return new UserWithPermissions(user, permissions);
        })
        .toList();
}
```

### Caching

✅ Consider caching for frequently accessed data

```java
// ✅ Good
@Service
@RequiredArgsConstructor
public class UserQueryServiceImpl implements UserQueryService {

    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = "users", key = "#userId.value")
    public Optional<User> getUserById(UserId userId) {
        return userRepository.findById(userId);
    }

    @CacheEvict(value = "users", key = "#user.id.value")
    public void evictUserCache(User user) {
        // Cache invalidation
    }
}
```

---

## MapStruct Usage

### Mapper Configuration

✅ **ALWAYS** use common MapStruct configuration

```java
// ✅ Good
@Mapper(config = AppMapStructConfiguration.class,
        uses = {MapstructCommonMapper.class, MapstructCommonDomainMapper.class})
public interface UserRequestMapper {
    UserRequestMapper INSTANCE = Mappers.getMapper(UserRequestMapper.class);

    CreateUserCommand toCreateUserCommand(AddUserRequest request);
}

// ❌ Bad - No configuration
@Mapper
public interface UserRequestMapper {
    // Missing common configuration
}
```

### Singleton Pattern

✅ **ALWAYS** use INSTANCE field for mapper access

```java
// ✅ Good
@Mapper(config = AppMapStructConfiguration.class)
public interface UserMapper {
    UserMapper INSTANCE = Mappers.getMapper(UserMapper.class);

    User toModel(UserEntity entity);
}

// Usage
User user = UserMapper.INSTANCE.toModel(entity);

// ❌ Bad
User user = Mappers.getMapper(UserMapper.class).toModel(entity);  // Creates new instance
```

### Qualified Mappings

✅ Use `qualifiedByName` for custom conversions

```java
// ✅ Good
@Mapper(config = AppMapStructConfiguration.class)
public interface UserEntityMapper {

    @Mapping(target = "id", source = "id", qualifiedByName = "longToUserId")
    User toModel(UserEntity entity);

    @Named("longToUserId")
    default UserId longToUserId(Long id) {
        return id != null ? new UserId(id) : null;
    }
    
}
```

### Context Parameters

✅ Use `@Context` for additional mapping context

```java
// ✅ Good
@Mapper(config = AppMapStructConfiguration.class)
public interface PaymentMethodResponseMapper {

    PaymentMethodResponse toResponse(PaymentMethod method,
                                     @Context ObjectMapper objectMapper);

    default ProcessorConfigResponse mapConfig(ProcessorConfig config,
                                             @Context ObjectMapper mapper) {
        return mapper.convertValue(config, ProcessorConfigResponse.class);
    }
}
```

---

## Lombok Usage

### Use Appropriate Annotations

✅ **ALWAYS** use correct Lombok annotations for each type

#### DTOs and Requests/Responses

```java
// ✅ Good - DTOs (immutable)
@Getter
@Builder
@RequiredArgsConstructor
public class CreateUserRequest {
    @NotEmpty
    private final String email;
    private final String firstName;
}

// ✅ Good - Responses (immutable)
@Getter
@Builder
@RequiredArgsConstructor
public class UserResponse {
    private final String id;
    private final String email;
    private final String firstName;
}
```

#### Domain Entities

```java
// ✅ Good - Domain entities (immutable with builder)
@Getter
@Builder(toBuilder = true)
@With
@RequiredArgsConstructor
@EqualsAndHashCode(of = "id")
public class User {
    private final UserId id;
    private final String email;
    private final String firstName;
    private final UserStatus status;

    // Immutable - use .with*() or .toBuilder() for changes
}

// ❌ Bad
@Data  // Too generic, creates setters (mutability)
public class User {
    private UserId id;
}
```

#### JPA Entities

```java
// ✅ Good - JPA entities (mutable)
@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "email")
    private String email;

    // Mutable for JPA
}

// ❌ Bad - Using @Data on JPA entity
@Entity
@Data  // Can cause issues with lazy loading and bidirectional relationships
public class UserEntity {
    @OneToMany(mappedBy = "user")
    private List<PermissionEntity> permissions;  // Lazy loading issues!
}
```

#### Services

```java
// ✅ Good - Services
@Service
@RequiredArgsConstructor  // Constructor injection
@Slf4j                     // Logging
public class UserCommandServiceImpl implements UserCommandService {
    private final UserRepository userRepository;
    private final UserEventPublisher eventPublisher;

    // Constructor auto-generated by @RequiredArgsConstructor
}

// ❌ Bad - Manual constructor
@Service
public class UserCommandServiceImpl implements UserCommandService {
    private final UserRepository userRepository;

    public UserCommandServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }
}
```

### Commands and Queries

```java
// ✅ Good - Immutable commands with @With
@Getter
@Builder
@With
@RequiredArgsConstructor
public class CreateUserCommand {
    private final String email;
    private final String firstName;
}

// Usage: Create modified copy
CreateUserCommand updated = command.withEmail("new@email.com");

// ✅ Good - Using record (Java 14+)
@Builder
@With
public record UpsertUserCommand(
    String email,
    String firstName
) {}
```

---

## Immutability

### Domain Entities

✅ **ALWAYS** make domain entities immutable

```java
// ✅ Good - Immutable domain entity
@Getter
@Builder(toBuilder = true)
@With
@RequiredArgsConstructor
public class User {
    private final UserId id;
    private final String email;
    private final UserStatus status;

    // Create modified copy
    public User activate() {
        return this.withStatus(UserStatus.ACTIVE);
    }
}

// Usage
User user = // ...
User activatedUser = user.activate();  // Returns new instance

// ❌ Bad - Mutable domain entity
@Data  // Creates setters
public class User {
    private UserId id;
    private String email;

    public void setEmail(String email) {
        this.email = email;  // Mutation!
    }
}
```

### Commands and Queries

✅ **ALWAYS** make commands and queries immutable

```java
// ✅ Good
@Getter
@Builder
@With
@RequiredArgsConstructor
public class CreateUserCommand {
    private final String email;
    private final String firstName;
}

// ❌ Bad
@Data  // Creates setters
public class CreateUserCommand {
    private String email;
    private String firstName;
}
```

### DTOs

---

## Database Practices

### Soft Deletes

✅ Use soft deletes for audit trail

```java
// ✅ Good
@Entity
@Table(name = "payment_methods")
@SQLDelete(sql = "UPDATE payment_methods SET deleted_at = current_timestamp WHERE id = ?")
@SQLRestriction("deleted_at IS NULL")
public class PaymentMethodEntity {

    @Column(name = "deleted_at")
    private Instant deletedAt;
}
```

### Auditing

✅ Use Spring Data auditing annotations

```java
// ✅ Good
@Entity
@Table(name = "users")
@EntityListeners(AuditingEntityListener.class)
public class UserEntity {

    @CreatedDate
    @Column(name = "created_at", updatable = false)
    private Instant createdAt;

    @LastModifiedDate
    @Column(name = "updated_at")
    private Instant updatedAt;

    @CreatedBy
    @Column(name = "created_by")
    private Long createdBy;

    @LastModifiedBy
    @Column(name = "updated_by")
    private Long updatedBy;
}
```

### Optimistic Locking

✅ Use `@Version` for concurrent updates

```java
// ✅ Good
@Entity
@Table(name = "_sessions")
public class SessionEntity {

    @Version
    @Column(name = "locking_version")
    private Long lockingVersion;
}
```

### Dynamic Updates

✅ Use `@DynamicUpdate` for entities with many fields

```java
// ✅ Good
@Entity
@Table(name = "users")
@DynamicUpdate  // Only update changed fields
public class UserEntity {
    // Many fields...
}
```

### Database Indexes

✅ Add indexes on frequently queried fields

```java
// ✅ Good
@Entity
@Table(name = "users", indexes = {
    @Index(name = "idx_user_email", columnList = "email"),
    @Index(name = "idx_user_tenant_status", columnList = "tenant_id, user_status")
})
public class UserEntity {
    @Column(name = "email")
    private String email;
    
    @Column(name = "user_status")
    @Enumerated(EnumType.STRING)
    private UserStatus userStatus;
}
```

---

## Summary Checklist

Before committing code, verify:

### Transactions
- [ ] Query services use `@Transactional(readOnly = true)`
- [ ] Exceptions in transactions are re-thrown

### Exceptions
- [ ] Base domain exception exists
- [ ] Meaningful error messages provided
- [ ] Exceptions in `shared/exceptions/`

### Logging
- [ ] Using `@Slf4j` annotation
- [ ] Parameterized logging (no concatenation)
- [ ] Appropriate log levels
- [ ] No sensitive data in logs

### Validation
- [ ] DTOs have validation annotations
- [ ] Controllers use `@Valid`
- [ ] Business rules validated in services
- [ ] Custom validators for complex rules

### Security
- [ ] User input sanitized
- [ ] Parameterized queries (no SQL injection)
- [ ] Error messages don't expose internals

### Performance
- [ ] List endpoints use pagination
- [ ] Lazy loading by default
- [ ] No N+1 queries
- [ ] Appropriate caching

### Mappers
- [ ] Using common MapStruct configuration
- [ ] INSTANCE singleton field
- [ ] Qualified mappings for custom conversions

### Lombok
- [ ] Appropriate annotations for each type
- [ ] `@RequiredArgsConstructor` for services
- [ ] Domain entities are immutable
- [ ] JPA entities are mutable

### Database
- [ ] Soft deletes where appropriate
- [ ] Auditing annotations
- [ ] Optimistic locking for concurrent updates
- [ ] Indexes on frequently queried fields

---

**Last Updated:** 2026-01-10
**Based on:** user-service and payment-service implementations
