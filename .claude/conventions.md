# Project Conventions

## Code Style
- Follow standard Java conventions
- Use meaningful variable and method names
- Keep methods focused and single-purpose
- Add Javadoc for public APIs

## Architecture Patterns
- Follow the layered architecture defined in `/docs/architecture/high_level_java_design.md`
- Core layers: Orchestration, Context & Memory, Task Management, Tool & MCP, Communication, Predefined Flow
- Use Spring dependency injection for all components
- Separate concerns: AgentBrain (orchestrator), Services (business logic), Tools (execution)
- Keep business logic in service layer
- Follow the interface-first approach shown in architecture docs

## Testing
- Write unit tests for service layer
- Integration tests for API endpoints
- Aim for meaningful test coverage
- Use descriptive test method names

## Git Workflow
- Write clear, descriptive commit messages
- Keep commits atomic and focused
- Reference issue numbers when applicable

## Documentation
- Update README when adding features
- Document architectural decisions in /docs
- Keep API documentation current