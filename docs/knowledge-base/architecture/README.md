# Architecture Documentation

This directory contains system architecture documentation for the MicroAgent platform.

## 📋 Contents

1. **[System Overview](./system-overview.md)**
   - Platform architecture
   - Microservices overview
   - Technology stack

2. **[Layer Architecture](./layer-architecture.md)**
   - 6-module architecture
   - Layer responsibilities
   - Dependency rules

3. **[Architecture Diagrams](./diagrams/)**
   - System diagrams
   - Component diagrams
   - Data flow diagrams

## 🏗️ Architecture Principles

### Package-by-Feature
- Organize code by business capability, not technical layer
- High cohesion within features
- Low coupling between features

### CQRS (Command Query Responsibility Segregation)
- Separate read and write operations
- Command services for writes
- Query services for reads

### Hexagonal Architecture
- Core business logic isolated from infrastructure
- Domain-driven design
- Dependency inversion

### Clean Architecture
- Independent of frameworks
- Testable
- Independent of UI
- Independent of database

## 📐 The 6-Module Architecture

Every microservice follows this structure:

```
microservice-name/
├── service-name-api/           # API Layer (Controllers)
│   └── REST/WebSocket endpoints
├── service-name-app/           # Application Entry
│   └── Spring Boot Application class
├── service-name-core/          # Business Logic (CQRS)
│   ├── Domain entities
│   ├── Services (Command/Query)
│   └── Repository interfaces
├── service-name-data-access/   # Data Access Layer
│   ├── JPA entities
│   ├── Repository implementations
│   └── Database configurations
├── service-name-shared/        # Shared Components
│   ├── DTOs (Request/Response)
│   ├── Exceptions
│   └── Constants
└── service-name-test/          # Integration Tests
    └── TestContainers tests
```

## 🎯 Layer Responsibilities

### API Layer (`service-name-api`)
- **Purpose**: Handle HTTP requests/responses
- **Responsibilities**:
  - REST/WebSocket controllers
  - Request validation
  - DTO ↔ Command/Query mapping
  - HTTP status code management
- **Rules**:
  - ❌ NO business logic
  - ✅ Delegate to services
  - ✅ Use @Valid for validation
  - ✅ Return DTOs, not domain entities

### Application Layer (`service-name-app`)
- **Purpose**: Application entry point
- **Responsibilities**:
  - Spring Boot Application class
  - Configuration files
  - Profile management
- **Rules**:
  - ✅ Minimal code, mostly config
  - ✅ Import all other modules

### Core Layer (`service-name-core`)
- **Purpose**: Business logic
- **Responsibilities**:
  - Domain entities
  - Business rules
  - Service interfaces and implementations
  - Repository interfaces (not implementations!)
- **Rules**:
  - ❌ NO infrastructure dependencies
  - ❌ NO JPA annotations
  - ❌ NO Spring Data
  - ✅ Pure business logic
  - ✅ Define repository contracts

### Data Access Layer (`service-name-data-access`)
- **Purpose**: Data persistence
- **Responsibilities**:
  - JPA entities
  - Repository implementations
  - Database configurations
- **Rules**:
  - ✅ Implement core repository interfaces
  - ✅ JPA/Hibernate here
  - ✅ Database-specific logic

### Shared Layer (`service-name-shared`)
- **Purpose**: Shared DTOs and utilities
- **Responsibilities**:
  - Request/Response DTOs
  - Custom exceptions
  - Constants and enums
- **Rules**:
  - ❌ NO dependencies on other service modules
  - ✅ Pure POJOs
  - ✅ Validation annotations on DTOs

### Test Layer (`service-name-test`)
- **Purpose**: Integration testing
- **Responsibilities**:
  - End-to-end tests
  - TestContainers setup
  - Integration test scenarios
- **Rules**:
  - ✅ Use TestContainers for databases
  - ✅ Test real scenarios
  - ✅ 80%+ coverage

## 🚦 Dependency Rules

```
         ┌─────────────┐
         │     App     │
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

**Rules**:
- API → Core, Shared
- Core → Shared (NO other dependencies)
- Data Access → Core, Shared
- App → ALL modules
- Shared → NO dependencies

## 🎯 For Claude AI

When working on architecture:
1. **Always** respect layer boundaries
2. **Never** violate dependency rules
3. **Follow** the 6-module structure strictly
4. **Reference** this documentation when making architectural decisions
5. **Update** diagrams if architecture changes

### Quick Reference: "Where does this go?"

| Component | Module | Layer |
|-----------|--------|-------|
| REST Controller | api | API |
| Request/Response DTO | shared | Shared |
| API Interface | shared | Shared |
| Domain Entity | core | Core |
| Command/Query | core | Core |
| Service Interface | core | Core |
| Service Implementation | core | Core |
| Repository Interface | core | Core |
| JPA Entity | data-access | Data Access |
| Repository Implementation | data-access | Data Access |
| Exception | shared | Shared |
| Constants | shared | Shared |
| Integration Test | test | Test |

---

## 📚 Related Documentation

- [Feature Guidelines](../feature-guidelines/README.md)
- [Coding Conventions](../coding-conventions/README.md)
- [Implementation Checklist](../feature-guidelines/implementation-checklist.md)

For detailed architecture documentation, see:
- `/docs/architecture/` - Legacy architecture docs
- `/docs/requirements/` - Project structure details

---

**Last Updated**: 2026-01-10
**Maintained By**: Architecture Team
