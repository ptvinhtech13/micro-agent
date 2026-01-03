# MicroAgent - AI Agent System

A multi-module Spring Boot application implementing an AI agent system with a 6-layer architecture.

## Project Structure

```
microagent/
├── agentic-framework/          # Agent Framework parent module
│   ├── agent-shared/           # Shared utilities for framework
│   ├── agent-brain/            # Agent brain implementation
│   ├── agent-context/          # Context management
│   ├── agent-memory/           # Memory management
│   ├── agent-tools/            # Tool integration
│   ├── agent-engage/           # Engagement handling
│   └── agent-task/             # Task management
├── agentic-shared/             # Common utilities across modules
├── agentic-core/               # Business logic (CQRS pattern)
├── agentic-data-access/        # Repository implementations
├── agentic-api/                # REST/WebSocket controllers
├── agentic-app/                # Main Spring Boot application
├── agentic-test/               # Integration tests with TestContainers
└── docker-compose/             # Infrastructure setup
```

## Module Details

### 1. agentic-framework
Parent module containing the core agent framework components:
- **agent-shared**: Common utilities for the framework
- **agent-brain**: Core orchestration (AgentBrain, IntentClassifier)
- **agent-context**: Context engine and management
- **agent-memory**: Multi-tier memory system
- **agent-tools**: Tool registry and MCP integration
- **agent-engage**: Communication and interaction
- **agent-task**: Task decomposition and execution

### 2. agentic-shared
Shared module for common functionality:
- Constants and enums
- Custom exceptions
- Utility classes
- HTTP API interfaces and DTOs

### 3. agentic-core
Business logic following CQRS pattern:
- Feature-based organization
- Command and Query services
- Domain entities
- Repository interfaces

### 4. agentic-data-access
Data persistence layer:
- JPA/Hibernate for relational database
- Redis for caching
- Repository implementations
- Database configurations

### 5. agentic-api
API layer:
- REST controllers
- WebSocket controllers
- Request/response mapping
- API validation

### 6. agentic-app
Main application module:
- Spring Boot main class
- Application configuration
- Component scanning setup

### 7. agentic-test
Integration testing:
- TestContainers setup
- Spring Boot tests
- Infrastructure testing

### 8. docker-compose
Infrastructure setup:
- PostgreSQL database
- Redis cache
- Configuration files
- Scripts for local development

## Getting Started

### Prerequisites
- Java 21
- Maven 3.8+
- Docker & Docker Compose

### Running the Application

1. **Start infrastructure:**
   ```bash
   cd docker-compose
   ./local.run.sh -d
   ```

2. **Build the project:**
   ```bash
   mvn clean install
   ```

3. **Run the application:**
   ```bash
   cd agentic-app
   mvn spring-boot:run
   ```

### Configuration

Edit `agentic-app/src/main/resources/application.properties`:
- Database connection settings
- Redis configuration
- Spring AI Anthropic API key
- Logging levels

## Architecture

The system follows a 6-layer architecture:

1. **Core Orchestration Layer**: Main decision-making and routing
2. **Context & Memory Layer**: Context building and memory management
3. **Task Management Layer**: Task decomposition and execution
4. **Tool & MCP Layer**: External tool integration
5. **Communication Layer**: API and event handling
6. **Predefined Flow Layer**: Template-based execution

For detailed architecture documentation, see:
- `/docs/architecture/high_level_java_design.md`
- `/docs/architecture/agent_architecture_diagram.md`

## Development Patterns

### CQRS Pattern
The core and API modules follow Command Query Responsibility Segregation:
- **Commands**: Write operations (Create, Update, Delete)
- **Queries**: Read operations (Get, List, Search)

### Package Feature Pattern
Features are organized by business capability, not by technical layer.

### MapStruct
DTO to Entity mapping using MapStruct for type-safe transformations.

## Building

```bash
# Validate structure
mvn validate

# Compile all modules
mvn compile

# Run tests
mvn test

# Package
mvn package

# Install to local repo
mvn install
```

## Testing

### Unit Tests
```bash
mvn test
```

### Integration Tests
```bash
cd agentic-test
mvn verify
```

## Technology Stack

- **Java 21**
- **Spring Boot 3.5.9**
- **Spring AI 1.1.2** (Anthropic integration)
- **PostgreSQL 16.4**
- **Redis 7.2**
- **MapStruct 1.5.5**
- **Lombok 1.18.30**
- **TestContainers 1.19.3**

## Claude Code Integration

This project includes Claude Code conventions and slash commands in `.claude/`:
- `/review-arch` - Review code against architecture
- `/new-component` - Create new components
- `/explain-layer` - Explain the architecture
- `/validate-interfaces` - Validate implementations

See `.claude/commands/GUIDE.md` for more information.

## License

[Your License Here]

## Contributors

[Your Contributors Here]
