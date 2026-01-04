# MicroAgent Multi-Module Project Structure
==========================================

This document defines the **MANDATORY** project structure for the MicroAgent platform. All microservices **MUST** follow this structure exactly.

## Project Info
- **Project Name**: MicroAgent
- **Base Package**: `io.agentic.microagent`
- **Build Tool**: Maven (Multi-Module)
- **Java Version**: 21
- **Spring Boot Version**: 3.5.9

---

## 📋 Table of Contents
1. [Base Microservice Structure (MANDATORY)](#base-microservice-structure)
2. [Complete Project Structure](#complete-project-structure)
3. [Concrete Examples](#concrete-examples)
4. [Package Naming Conventions](#package-naming-conventions)

---

## Base Microservice Structure (MANDATORY)

### Overview
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
        └── feature-name                            # e.g., "agent-registration"
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
        └── feature-name                            # e.g., "agent-registration"
            ├── constants/                          # Feature-specific constants
            │   └── FeatureNameConstants.java
            ├── entities/                           # Domain Entities (NOT JPA entities)
            │   └── FeatureNameAggregate.java       # Aggregate root
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

## Complete Project Structure

```
/microagent                                         # Root project directory
├── pom.xml                                         # Root parent POM
│
├── /docker-compose                                 # Docker infrastructure
│   ├── /infra
│   │   ├── /scripts                                # Bash scripts for infra
│   │   ├── /configs                                # Configuration files
│   │   └── /volumes                                # Docker volume data
│   ├── common.yml                                  # Common docker-compose config
│   ├── docker-compose.local.yml                    # Local development compose
│   ├── versions.env                                # Docker image versions
│   └── local.run.sh                                # Run script
│
├── /agentic-framework                              # Agent Framework (Parent Module)
│   ├── pom.xml
│   ├── /agent-brain                                # Brain/reasoning capabilities
│   ├── /agent-context                              # Context management
│   ├── /agent-core                                 # Core agent functionality
│   ├── /agent-engage                               # Engagement & interaction
│   ├── /agent-memory                               # Memory management
│   ├── /agent-planning                             # Planning & task decomposition
│   ├── /agent-shared                               # Shared utilities
│   ├── /agent-task                                 # Task management
│   └── /agent-tools                                # Tool integration
│
├── /agent-registry-service                         # Service Registry (Microservice)
│   ├── pom.xml                                     # Parent POM for registry
│   ├── /registry-api                               # ✅ API Layer
│   │   ├── pom.xml
│   │   └── src/main/java/io/agentic/microagent/registry/api/features/
│   │       └── agent-registration/
│   │           ├── mapper/
│   │           └── AgentRegistrationController.java
│   ├── /registry-app                               # ✅ Application Entry
│   │   ├── pom.xml
│   │   ├── src/main/java/io/agentic/microagent/registry/app/
│   │   │   └── RegistryServiceApplication.java
│   │   └── src/main/resources/
│   │       └── application.yml
│   ├── /registry-core                              # ✅ Business Logic (CQRS)
│   │   ├── pom.xml
│   │   └── src/main/java/io/agentic/microagent/registry/core/features/
│   │       └── agent-registration/
│   │           ├── constants/
│   │           ├── entities/
│   │           ├── generator/
│   │           ├── mapper/
│   │           ├── request/
│   │           ├── service/
│   │           ├── utils/
│   │           ├── AgentRegistrationCommandService.java
│   │           ├── AgentRegistrationQueryService.java
│   │           └── AgentRegistrationRepository.java
│   ├── /registry-data-access                       # ✅ Data Access Layer
│   │   ├── pom.xml
│   │   └── src/main/java/io/agentic/microagent/registry/dataaccess/
│   │       ├── relational/
│   │       │   ├── agent-registration/
│   │       │   │   ├── entities/
│   │       │   │   ├── mapper/
│   │       │   │   ├── repository/
│   │       │   │   └── AgentRegistrationRepositoryImpl.java
│   │       │   └── RelationalDatabaseAccessConfig.java
│   │       └── other-data-source-access/
│   ├── /registry-shared                            # ✅ Shared Components
│   │   ├── pom.xml
│   │   └── src/main/java/io/agentic/microagent/registry/shared/
│   │       ├── constants/
│   │       ├── enums/
│   │       ├── exceptions/
│   │       ├── utils/
│   │       └── http/
│   │           ├── apis/
│   │           └── features/agent-registration/
│   │               ├── request/
│   │               └── response/
│   └── /registry-test                              # ✅ Integration Tests
│       ├── pom.xml
│       └── src/test/java/io/agentic/microagent/registry/test/
│
├── /agent-policy-service                           # Policy Management Service (Microservice)
│   ├── pom.xml                                     # Parent POM for policy
│   ├── /policy-api                                 # ✅ API Layer
│   │   ├── pom.xml
│   │   └── src/main/java/io/agentic/microagent/policy/api/features/
│   │       ├── policy-management/
│   │       │   ├── mapper/
│   │       │   └── PolicyManagementController.java
│   │       ├── policy-enforcement/
│   │       │   ├── mapper/
│   │       │   └── PolicyEnforcementController.java
│   │       └── tag-management/
│   │           ├── mapper/
│   │           └── TagManagementController.java
│   ├── /policy-app                                 # ✅ Application Entry
│   │   ├── pom.xml
│   │   ├── src/main/java/io/agentic/microagent/policy/app/
│   │   │   └── PolicyServiceApplication.java
│   │   └── src/main/resources/
│   │       └── application.yml
│   ├── /policy-core                                # ✅ Business Logic (CQRS)
│   │   ├── pom.xml
│   │   └── src/main/java/io/agentic/microagent/policy/core/features/
│   │       ├── policy-management/
│   │       │   ├── constants/
│   │       │   ├── entities/                       # Policy, PolicyContent, PolicyRule
│   │       │   ├── mapper/
│   │       │   ├── request/                        # CreatePolicyCommand, UpdatePolicyCommand
│   │       │   ├── service/
│   │       │   ├── utils/
│   │       │   ├── PolicyManagementCommandService.java
│   │       │   ├── PolicyManagementQueryService.java
│   │       │   └── PolicyRepository.java
│   │       ├── policy-enforcement/
│   │       │   ├── constants/
│   │       │   ├── entities/                       # PolicyValidationResult, PolicyViolation
│   │       │   ├── mapper/
│   │       │   ├── request/                        # ValidatePolicyCommand, EnforcePolicyCommand
│   │       │   ├── service/
│   │       │   ├── utils/
│   │       │   ├── PolicyEnforcementService.java
│   │       │   └── PolicyEnforcementEngine.java
│   │       └── tag-management/
│   │           ├── constants/
│   │           ├── entities/                       # Tag
│   │           ├── mapper/
│   │           ├── request/                        # CreateTagCommand, AssignTagCommand
│   │           ├── service/
│   │           ├── utils/
│   │           ├── TagManagementCommandService.java
│   │           ├── TagManagementQueryService.java
│   │           └── TagRepository.java
│   ├── /policy-data-access                         # ✅ Data Access Layer
│   │   ├── pom.xml
│   │   └── src/main/java/io/agentic/microagent/policy/dataaccess/
│   │       ├── relational/
│   │       │   ├── policy-management/
│   │       │   │   ├── entities/                   # PolicyJpaEntity
│   │       │   │   ├── mapper/
│   │       │   │   ├── repository/
│   │       │   │   │   └── PolicyJpaRepository.java
│   │       │   │   └── PolicyRepositoryImpl.java
│   │       │   ├── tag-management/
│   │       │   │   ├── entities/                   # TagJpaEntity
│   │       │   │   ├── mapper/
│   │       │   │   ├── repository/
│   │       │   │   │   └── TagJpaRepository.java
│   │       │   │   └── TagRepositoryImpl.java
│   │       │   ├── violation-tracking/
│   │       │   │   ├── entities/                   # PolicyViolationJpaEntity
│   │       │   │   ├── mapper/
│   │       │   │   ├── repository/
│   │       │   │   │   └── PolicyViolationJpaRepository.java
│   │       │   │   └── ViolationTrackingRepositoryImpl.java
│   │       │   └── RelationalDatabaseAccessConfig.java
│   │       └── other-data-source-access/
│   ├── /policy-shared                              # ✅ Shared Components
│   │   ├── pom.xml
│   │   └── src/main/java/io/agentic/microagent/policy/shared/
│   │       ├── constants/
│   │       ├── enums/                              # PolicyStatus, EnforcementAction, EnforcementPhase
│   │       ├── exceptions/
│   │       ├── utils/
│   │       └── http/
│   │           ├── apis/
│   │           └── features/
│   │               ├── policy-management/
│   │               │   ├── request/
│   │               │   │   └── CreatePolicyRequest.java
│   │               │   └── response/
│   │               │       └── PolicyResponse.java
│   │               ├── policy-enforcement/
│   │               │   ├── request/
│   │               │   └── response/
│   │               └── tag-management/
│   │                   ├── request/
│   │                   └── response/
│   └── /policy-test                                # ✅ Integration Tests
│       ├── pom.xml
│       └── src/test/java/io/agentic/microagent/policy/test/
│
├── /agent-brain                                    # Agent Brain Service (Microservice - Orchestrator)
│   ├── pom.xml                                     # Parent POM for brain
│   ├── /brain-api                                  # ✅ API Layer
│   │   ├── pom.xml
│   │   └── src/main/java/io/agentic/microagent/brain/api/features/
│   │       └── orchestration/
│   │           ├── mapper/
│   │           └── OrchestrationController.java
│   ├── /brain-app                                  # ✅ Application Entry
│   │   ├── pom.xml
│   │   ├── src/main/java/io/agentic/microagent/brain/app/
│   │   │   └── BrainServiceApplication.java
│   │   └── src/main/resources/
│   │       └── application.yml
│   ├── /brain-core                                 # ✅ Business Logic (CQRS)
│   │   ├── pom.xml
│   │   └── src/main/java/io/agentic/microagent/brain/core/features/
│   │       └── orchestration/
│   │           ├── constants/
│   │           ├── entities/                       # TaskPlan, AgentCoordination
│   │           ├── mapper/
│   │           ├── request/                        # PlanTaskCommand, CoordinateAgentsCommand
│   │           ├── service/
│   │           ├── utils/
│   │           ├── OrchestrationCommandService.java
│   │           ├── OrchestrationQueryService.java
│   │           └── OrchestrationRepository.java
│   ├── /brain-data-access                          # ✅ Data Access Layer
│   │   ├── pom.xml
│   │   └── src/main/java/io/agentic/microagent/brain/dataaccess/
│   │       └── relational/
│   │           ├── orchestration/
│   │           │   ├── entities/
│   │           │   ├── mapper/
│   │           │   ├── repository/
│   │           │   └── OrchestrationRepositoryImpl.java
│   │           └── RelationalDatabaseAccessConfig.java
│   ├── /brain-shared                               # ✅ Shared Components
│   │   ├── pom.xml
│   │   └── src/main/java/io/agentic/microagent/brain/shared/
│   │       ├── constants/
│   │       ├── enums/                              # TaskStatus, CoordinationMode
│   │       ├── exceptions/
│   │       ├── utils/
│   │       └── http/
│   │           ├── apis/
│   │           └── features/orchestration/
│   │               ├── request/
│   │               └── response/
│   └── /brain-test                                 # ✅ Integration Tests
│       ├── pom.xml
│       └── src/test/java/io/agentic/microagent/brain/test/
│
└── /agent-demo                                     # Demo Parent Module
    ├── pom.xml                                     # Parent POM (modules: agent-user-service, agent-order-service, agent-payment-service)
    │
    ├── /agent-user-service                         # User Management Specialist Agent
    │   ├── pom.xml                                 # Parent POM for user
    │   ├── /user-api                               # ✅ API Layer
    │   │   ├── pom.xml
    │   │   └── src/main/java/io/agentic/microagent/user/api/features/
    │   │       └── user-management/
    │   │           ├── mapper/
    │   │           └── UserManagementController.java
    │   ├── /user-app                               # ✅ Application Entry
    │   │   ├── pom.xml
    │   │   ├── src/main/java/io/agentic/microagent/user/app/
    │   │   │   └── UserServiceApplication.java
    │   │   └── src/main/resources/
    │   │       └── application.yml
    │   ├── /user-core                              # ✅ Business Logic (CQRS)
    │   │   ├── pom.xml
    │   │   └── src/main/java/io/agentic/microagent/user/core/features/
    │   │       └── user-management/
    │   │           ├── constants/
    │   │           ├── entities/
    │   │           ├── mapper/
    │   │           ├── request/
    │   │           ├── service/
    │   │           ├── utils/
    │   │           ├── UserManagementCommandService.java
    │   │           ├── UserManagementQueryService.java
    │   │           └── UserManagementRepository.java
    │   ├── /user-data-access                       # ✅ Data Access Layer
    │   │   ├── pom.xml
    │   │   └── src/main/java/io/agentic/microagent/user/dataaccess/
    │   │       └── relational/
    │   │           ├── user-management/
    │   │           │   ├── entities/
    │   │           │   ├── mapper/
    │   │           │   ├── repository/
    │   │           │   └── UserManagementRepositoryImpl.java
    │   │           └── RelationalDatabaseAccessConfig.java
    │   ├── /user-shared                            # ✅ Shared Components
    │   │   ├── pom.xml
    │   │   └── src/main/java/io/agentic/microagent/user/shared/
    │   │       ├── constants/
    │   │       ├── enums/
    │   │       ├── exceptions/
    │   │       ├── utils/
    │   │       └── http/
    │   │           ├── apis/
    │   │           └── features/user-management/
    │   │               ├── request/
    │   │               └── response/
    │   └── /user-test                              # ✅ Integration Tests
    │       ├── pom.xml
    │       └── src/test/java/io/agentic/microagent/user/test/
    │
    ├── /agent-order-service                        # Order Management Specialist Agent
    │   ├── pom.xml                                 # Parent POM for order
    │   ├── /order-api                              # ✅ API Layer
    │   │   ├── pom.xml
    │   │   └── src/main/java/io/agentic/microagent/order/api/features/
    │   │       └── order-management/
    │   │           ├── mapper/
    │   │           └── OrderManagementController.java
    │   ├── /order-app                              # ✅ Application Entry
    │   │   ├── pom.xml
    │   │   ├── src/main/java/io/agentic/microagent/order/app/
    │   │   │   └── OrderServiceApplication.java
    │   │   └── src/main/resources/
    │   │       └── application.yml
    │   ├── /order-core                             # ✅ Business Logic (CQRS)
    │   │   ├── pom.xml
    │   │   └── src/main/java/io/agentic/microagent/order/core/features/
    │   │       └── order-management/
    │   │           ├── constants/
    │   │           ├── entities/
    │   │           ├── mapper/
    │   │           ├── request/
    │   │           ├── service/
    │   │           ├── utils/
    │   │           ├── OrderManagementCommandService.java
    │   │           ├── OrderManagementQueryService.java
    │   │           └── OrderManagementRepository.java
    │   ├── /order-data-access                      # ✅ Data Access Layer
    │   │   ├── pom.xml
    │   │   └── src/main/java/io/agentic/microagent/order/dataaccess/
    │   │       └── relational/
    │   │           ├── order-management/
    │   │           │   ├── entities/
    │   │           │   ├── mapper/
    │   │           │   ├── repository/
    │   │           │   └── OrderManagementRepositoryImpl.java
    │   │           └── RelationalDatabaseAccessConfig.java
    │   ├── /order-shared                           # ✅ Shared Components
    │   │   ├── pom.xml
    │   │   └── src/main/java/io/agentic/microagent/order/shared/
    │   │       ├── constants/
    │   │       ├── enums/
    │   │       ├── exceptions/
    │   │       ├── utils/
    │   │       └── http/
    │   │           ├── apis/
    │   │           └── features/order-management/
    │   │               ├── request/
    │   │               └── response/
    │   └── /order-test                             # ✅ Integration Tests
    │       ├── pom.xml
    │       └── src/test/java/io/agentic/microagent/order/test/
    │
    └── /agent-payment-service                      # Payment Management Specialist Agent
        ├── pom.xml                                 # Parent POM for payment
        ├── /payment-api                            # ✅ API Layer
        │   ├── pom.xml
        │   └── src/main/java/io/agentic/microagent/payment/api/features/
        │       └── payment-management/
        │           ├── mapper/
        │           └── PaymentManagementController.java
        ├── /payment-app                            # ✅ Application Entry
        │   ├── pom.xml
        │   ├── src/main/java/io/agentic/microagent/payment/app/
        │   │   └── PaymentServiceApplication.java
        │   └── src/main/resources/
        │       └── application.yml
        ├── /payment-core                           # ✅ Business Logic (CQRS)
        │   ├── pom.xml
        │   └── src/main/java/io/agentic/microagent/payment/core/features/
        │       └── payment-management/
        │           ├── constants/
        │           ├── entities/
        │           ├── mapper/
        │           ├── request/
        │           ├── service/
        │           ├── utils/
        │           ├── PaymentManagementCommandService.java
        │           ├── PaymentManagementQueryService.java
        │           └── PaymentManagementRepository.java
        ├── /payment-data-access                    # ✅ Data Access Layer
        │   ├── pom.xml
        │   └── src/main/java/io/agentic/microagent/payment/dataaccess/
        │       └── relational/
        │           ├── payment-management/
        │           │   ├── entities/
        │           │   ├── mapper/
        │           │   ├── repository/
        │           │   └── PaymentManagementRepositoryImpl.java
        │           └── RelationalDatabaseAccessConfig.java
        ├── /payment-shared                         # ✅ Shared Components
        │   ├── pom.xml
        │   └── src/main/java/io/agentic/microagent/payment/shared/
        │       ├── constants/
        │       ├── enums/
        │       ├── exceptions/
        │       ├── utils/
        │       └── http/
        │           ├── apis/
        │           └── features/payment-management/
        │               ├── request/
        │               └── response/
        └── /payment-test                           # ✅ Integration Tests
            ├── pom.xml
            └── src/test/java/io/agentic/microagent/payment/test/
```

---

## Concrete Examples

### Example 1: Agent Registry Service - Agent Registration Feature

```java
// 1. API Layer (registry-api)
package io.agentic.microagent.registry.api.features.agentregistration;

@RestController
@RequestMapping("/api/v1/agents")
public class AgentRegistrationController {
    // REST endpoints
}

// 2. Core Layer (registry-core)
package io.agentic.microagent.registry.core.features.agentregistration;

// Command
public record CreateAgentRegistrationCommand(String agentId, String name) {}

// Service Interface
public interface AgentRegistrationCommandService {
    void createAgent(CreateAgentRegistrationCommand command);
}

// Repository Interface
public interface AgentRegistrationRepository {
    void save(AgentRegistrationAggregate aggregate);
}

// 3. Data Access Layer (registry-data-access)
package io.agentic.microagent.registry.dataaccess.relational.agentregistration;

// JPA Entity
@Entity
@Table(name = "agent_registrations")
public class AgentRegistrationJpaEntity {
    // JPA fields
}

// Repository Implementation
@Repository
public class AgentRegistrationRepositoryImpl implements AgentRegistrationRepository {
    // Implementation
}

// 4. Shared Layer (registry-shared)
package io.agentic.microagent.registry.shared.http.features.agentregistration.request;

public record CreateAgentRegistrationRequest(String name, String capabilities) {}
```

---

## Package Naming Conventions

### Service-Specific Packages

| Module      | Package Pattern                                                | Example                                                          |
|-------------|----------------------------------------------------------------|------------------------------------------------------------------|
| API         | `io.agentic.microagent.{service}.api.features.{feature-name}`  | `io.agentic.microagent.registry.api.features.agentregistration`  |
| App         | `io.agentic.microagent.{service}.app`                          | `io.agentic.microagent.registry.app`                             |
| Core        | `io.agentic.microagent.{service}.core.features.{feature-name}` | `io.agentic.microagent.registry.core.features.agentregistration` |
| Data Access | `io.agentic.microagent.{service}.dataaccess.{datasource}`      | `io.agentic.microagent.registry.dataaccess.relational`           |
| Shared      | `io.agentic.microagent.{service}.shared`                       | `io.agentic.microagent.registry.shared`                          |
| Test        | `io.agentic.microagent.{service}.test`                         | `io.agentic.microagent.registry.test`                            |

### Service Name Mapping

| Microservice                      | Service Name | Module Prefix | Description                                       |
|-----------------------------------|--------------|---------------|---------------------------------------------------|
| agent-brain                       | `brain`      | `brain-*`     | Orchestrator - Plans & coordinates agents         |
| agent-registry-service            | `registry`   | `registry-*`  | Agent catalog & routing service                   |
| agent-policy-service              | `policy`     | `policy-*`    | Policy governance & enforcement                   |
| **agent-demo** (Parent Module)    | `demo`       | N/A           | Demo parent - Contains specialist agent services  |
| └─ agent-user-service             | `user`       | `user-*`      | User management specialist agent                  |
| └─ agent-order-service            | `order`      | `order-*`     | Order management specialist agent                 |
| └─ agent-payment-service          | `payment`    | `payment-*`   | Payment management specialist agent               |
| agent-{future}                    | `{future}`   | `{future}-*`  | Future microservices follow this pattern          |

---

## 🎯 Quick Reference: Creating a New Microservice

### Step-by-Step Checklist

When creating a new microservice called `agent-xyz-service`:

1. **Create Parent Module**: `agent-xyz-service/pom.xml`
2. **Create 6 Sub-Modules**:
   - ✅ `xyz-api` → API layer
   - ✅ `xyz-app` → Spring Boot application
   - ✅ `xyz-core` → Business logic (CQRS)
   - ✅ `xyz-data-access` → Data access layer
   - ✅ `xyz-shared` → Shared components
   - ✅ `xyz-test` → Integration tests
3. **Follow Package Structure**: Use the examples above as templates
4. **Update Root POM**: Add `agent-xyz-service` to root `pom.xml`
5. **Add Dependencies**: Configure module dependencies correctly

---

## 📚 Architecture Patterns Used

- **Package-by-Feature**: Organize code by business feature, not technical layer
- **CQRS (Command Query Responsibility Segregation)**: Separate read/write operations
- **Hexagonal Architecture**: Core business logic isolated from infrastructure
- **Repository Pattern**: Abstract data access logic
- **DTO Pattern**: Separate API models from domain models

---

## ⚠️ Critical Rules

1. ❌ **NEVER** put business logic in API layer
2. ❌ **NEVER** let Core layer depend on Data Access layer
3. ❌ **NEVER** use JPA entities in Core layer (use domain entities)
4. ✅ **ALWAYS** use MapStruct for mapping between layers
5. ✅ **ALWAYS** follow CQRS naming: `{Verb}{Feature}{Command/Query}`
6. ✅ **ALWAYS** implement Repository interface in Data Access layer

---

**Last Updated**: 2026-01-04
**Maintained By**: Architecture Team
