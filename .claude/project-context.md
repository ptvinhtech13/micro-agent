# Project Context

## Overview
This is a microagent project built with Spring Boot.

## Key Technologies
- Java
- Spring Boot
- Maven

## Project Structure
- `/src/main/java` - Application source code
- `/src/test/java` - Test code
- `/docs` - Architecture and design documentation
- `/docs/architecture` - Detailed architecture diagrams and designs
  - `high_level_java_design.md` - Complete system architecture with all layers
  - `agent_architecture_diagram.md` - Conceptual data structure model
  - `architecture_visual_mermaid.md` - Visual diagrams
  - `enhanced_data_flow_with_feedback_loop.md` - Data flow patterns

## Architecture Overview
The system follows a 6-layer architecture:
1. **Core Orchestration Layer**: AgentBrain, IntentClassifier, ExecutionRouter
2. **Context & Memory Layer**: ContextEngine, MemoryManager, MemoryRetriever
3. **Task Management Layer**: TaskDecomposer, TaskPlanExecutor, CollaboratorSelector
4. **Tool & MCP Layer**: ToolRegistry, MCPServerManager, ToolExecutor
5. **Communication Layer**: AgentController, EventPublisher, ResponseFormatter
6. **Predefined Flow Layer**: FlowRepository, FlowMatcher, FlowExecutor

## Important Patterns
- Interface-first design: Define interfaces before implementations
- Spring dependency injection: Use constructor injection with Lombok
- Data models: Use `@Data` and `@Builder` annotations
- Execution paths: PREDEFINED → SIMPLE → MEDIUM → COMPLEX based on complexity
- Memory tiers: Working (Redis) → Episodic (Vector DB) → Semantic (PostgreSQL)
