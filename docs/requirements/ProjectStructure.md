Restructure to Multi-Module Project
------------------------------------------

Restructure the current project to support multiple modules for better organization and maintainability following the below:

## Project Info:
- Current Project Name: MicroAgent
- Package Name: io.agentic.microagent

## Base Microservice Structure (BASE MICROSERVICE STRUCTURE)
- Each Microservice is a separate Spring Boot Application with its own modules.
- Must follow the structure below for each Microservice.
```
/service-name-api -- This is seperated module. API module containing REST controllers and WebSocket controllers. Following the Package Feature Pattern Strategy. This will import agentic-core, agentic-shared modules. Package name: io.agentic.microagent.api.*
   /features
      /feature-name -- Following the Command Query Responsibility Segregation (CQRS) pattern.
         /mapper  -- Contains mapping classes to map between different API DTO (Data Transfer Object) and Command/Query Request from service-name-core. We use MapStruct here.
         FeatureNameController.java  - This is REST Controller class or WebSocket Controller class for this Feature. Contains API endpoints for this feature.
/service-name-app -- This is seperated module. Main Spring Boot Application java class. Only this file is contained. This will import all modules. Package name: io.agentic.microagent.servicename.app
/service-name-core -- This is seperated module. Core module containing main business logic and services. Following the Package Feature Pattern Strategy. Rules: Just Business Logic Domain, No Controller is here. Package name: io.agentic.microagent.servicename.core
   /features
      /feature-name -- Following the Command Query Responsibility Segregation (CQRS) pattern.
         /constants -- Containts constant values used in the feature or core module.
         /entities -- Contains Java Aggregate Model Entity Business Classes. (Not meaning JPA/Hibernate Entity) This is aggregate Entity business class of the feature domain.
         /generator -- Contains code generator classes if needed for the feature. Like ID generator for Aggregate Entity Business
         /mapper  -- Contains mapping classes to map between different models (e.g., Entity to DTO, DTO to Entity). We can use MapStruct here.
         /request -- Contains Command Request Model classes and Query Request Model classes of this Feature. Rule: Start with Verb and End with Action (Query/Command) For example: CreateFeatureNameCommand.java, GetFeatureNameQuery.java
         /service -- This contains implementation classes for Command Service Interface and Query Service Interface of this Feature. Contains business logic methods for Command and Query operations. For example: FeatureNameCommandServiceImpl.java, FeatureNameQueryServiceImpl.java
         /utils
         FeatureNameCommandService.java -- This is interface for Command Service of this Feature. Contains business logic methods for Command operations.
         FeatureNameQueryService.java -- This is interface for Query Service of this Feature. Contains business logic methods for Query operations.
         FeatureNameRepository.java -- This is interface for Data Access Repository of this Feature. Contains data access methods for the feature aggregate entity.
/service-name-data-access -- This is seperated module. Data Access module containing repository implementations and database interaction logic. Package name: io.agentic.microagent.servicename.dataaccess
   /other-data-source-access - - This contains other data source access implementations if needed (e.g., NoSQL, In-Memory DB, External API)
      /feature-name
         /entities -- Contains Domain Entity Classes for this feature in other data source.
         /mapper  -- Contains mapping classes to map between Domain Entity and Core Aggregate Module Entity Model
         /repository -- Contains Repository Implementation classes for this feature.
            /EntityNameOtherDataSourceRepository.java -- This is the Repository interface for the Domain Entity in other data source.
         FeatureNameRepositoryImpl.java -- This is the implementation class for the FeatureNameRepository interface in service-name module. Contains database interaction logic using other data source access method.
      OtherDataSourceAccessConfig.java -- This is the configuration class for other data source connection settings. For example: @Configuration class for MongoDB or Redis connection settings.
   /relational
      /feature-name
         /entities -- Contains JPA/Hibernate Entity Classes for relational database tables of this feature.
         /mapper  -- Contains mapping classes to map between JPA/Hibernate Entity and Core Aggregate Module Entity Model.
         /repository -- Contains Repository Implementation classes for this feature.
            /EntityNameJpaRepository.java -- This is the JPA Repository interface for the Domain Entity JPA/Hibernate Entity in relational database.
         FeatureNameRepositoryImpl.java -- This is the implementation class for the FeatureNameRepository interface in service-name module. Contains database interaction logic using JPA/Hibernate or JDBC.
      RelationalDatabaseAccessConfig.java -- This is the configuration class for relational database connection and JPA/Hibernate settings. For example: @Configuration, @EnableJpaRepositories(basePackages = { "io.agentic.microagent.dataaccess.servicename.relational" }) @EntityScan(basePackages = { "io.agentic.microagent.dataaccess.servicename.relational" }), @EnableJpaAuditing
/service-name-shared -- This is seperated module. Shared module containing common utilities, constants, and models used across other modules, event other MicroAgent (MicroAgent per Microservice) Package name: io.agentic.microagent.dataaccess.servicename.shared
   /contants -- Containts constant values used across multiple modules.
   /enums -- Contains enum types used across multiple modules.
   /exceptions -- Contains custom exception classes used across multiple modules.
   /utils -- Contains utility classes and helper functions used across multiple modules.
   /http -- Contains HTTP related classes, e.g., API request/response models if needed across multiple modules.
      /apis -- Contains HTTP API Interface classes. That will be used and implemented by service-name-api Controller module. For example: FeatureNameApi.java
      /features
         /feature-name
            /request -- Contains all API Request DTOs class for this feature. This is used in service-name-api module for API Implementation.
               FeatureNameRequest.java 
            /response -- Contains all API Request DTOs class for this feature. This is used in service-name-api module for API Implementation.
               FeatureNameResponse.java
/service-name-test -- This is seperated module. Just for focus using SpringBootIntegrationTest with TestContainer to setup infra.
```

## Project Structure
```
/docker-compose
   /infra
      /scripts -- bash scripts (for infra setup if needed)
      /configs -- configuration files (e.g., yaml, env, if needed)
      /volumes -- local mounted docker volume data
      common.yml -- common configuration for all docker-compose configs.
      docker-compose.local.yml -- docker-compose file for local development environment. All docker image version should refer to the versions declared in versions.env
      versions.env -- version declaration for docker images. For example: POSTGRES_VERSION=16.4, POSTGRES_FORWARDED_PORT=5432
      local.run.sh -- One-shot run script with the flag -d to indicate for background docker run, othervise foreground run.
/agentic-framework -- This is seperated module. All folders in below is the seperated module and sub-module/child of agentic-framework module. Package name: io.agentic.microagent.framework
   /agent-brain -- Contains core classes for Agent Brain implementation. 
   /agent-context -- Contains classes for building and managing agent context.
   /agent-core -- Contains core classes for Agent Framework implementation.
   /agent-engage -- Contains classes for agent engagement and interaction handling.
   /agent-memory -- Contains classes for agent memory management.
   /agent-planning -- Contains classes for agent planning and decision-making, task decomposition
   /agent-reasoning -- Contains classes for agent reasoning and problem-solving.
   /agent-shared -- Shared module for Agent Framework containing common utilities, constants, and models used across other Agent Framework modules.
   /agent-tools -- Contains classes for integrating and managing external tools for agents.
   /agent-task -- Contains classes for agent task management and execution.
/agent-registry-service -- This is seperated module. Service Registry module for Agent discovery and registration. Package name: io.agentic.microagent.registry
    <<YOU MUST FOLLOW THE SAME BASE MICROSERVICE STRUCTURE AS DESCRIBED ABOVE FOR MICROSERVICE MODULES with api, app, core, data-access, shared, test modules and service name is "registry">>   
/agent-demo -- This is seperated module. Agent demostration. Package name: io.agentic.microagent.demo
    <<YOU MUST FOLLOW THE SAME BASE MICROSERVICE STRUCTURE AS DESCRIBED ABOVE FOR MICROSERVICE MODULES with api, app, core, data-access, shared, test modules and service name is "demo">>   

```
