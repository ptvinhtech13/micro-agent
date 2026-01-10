# Feature Implementation Guidelines

This directory contains comprehensive guidelines for implementing features in the MicroAgent platform.

## 📋 Contents

1. **[Feature Structure](./feature-structure.md)**
   - 6-module architecture overview
   - Package-by-Feature pattern
   - Module dependencies

2. **[Implementation Checklist](./implementation-checklist.md)**
   - Step-by-step guide for new features
   - Required files and components
   - Testing requirements

3. **[CQRS Patterns](./cqrs-patterns.md)**
   - Command/Query separation
   - Service layer patterns
   - Naming conventions

4. **[REST API Design](./rest-api-design.md)**
   - URL conventions
   - HTTP methods
   - Request/Response standards
   - Error handling
   - Pagination and filtering

## 🏗️ The 6-Module Architecture

Every microservice follows this structure:

```
service-name/
├── service-name-api/           # Controllers (REST/WebSocket)
├── service-name-app/           # Spring Boot Application
├── service-name-core/          # Business Logic (CQRS)
├── service-name-data-access/   # Repository Implementations
├── service-name-shared/        # DTOs, Constants, Exceptions
└── service-name-test/          # Integration Tests
```

## 🎯 Quick Start

### Implementing a New Feature

1. **Plan** - Create a plan in `../plans/active/`
2. **API Layer** - Create controller and API mapper
3. **Shared Layer** - Define DTOs and exceptions
4. **Core Layer** - Implement services and domain logic
5. **Data Access** - Create entities and repositories
6. **Tests** - Write unit and integration tests
7. **Review** - Use implementation checklist

### Required Components Checklist

- [ ] API: Controller + API Mapper
- [ ] Shared: Request/Response DTOs + API Interface
- [ ] Core: Command Service + Query Service + Repository Interface
- [ ] Core: Domain Entities + Commands/Queries
- [ ] Data Access: JPA Entities + Repository Implementation
- [ ] Tests: Unit tests (80%+ coverage) + Integration tests

## 🚀 For Claude AI

When implementing a feature:
1. **Always** read [Feature Structure](./feature-structure.md) first
2. **Follow** the [Implementation Checklist](./implementation-checklist.md) exactly
3. **Apply** CQRS patterns from [CQRS Patterns](./cqrs-patterns.md)
4. **Design** APIs according to [REST API Design](./rest-api-design.md)
5. **Create a plan** in `../plans/active/` for complex features
6. **Mark checklist items** as you complete them

### Common Mistakes to Avoid
- ❌ Mixing Commands and Queries in the same service
- ❌ Putting business logic in controllers
- ❌ Using JPA entities in Core layer
- ❌ Skipping validation on DTOs
- ❌ Not following REST API conventions

---

**See Also**: [Coding Conventions](../coding-conventions/README.md) | [Architecture](../architecture/README.md) | [Plans](../plans/README.md)
