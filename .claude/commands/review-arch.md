Review the current code/file against the architecture defined in `/docs/architecture/high_level_java_design.md`.

Check for:
1. **Layer Separation**: Is this component in the correct layer (Orchestration, Context & Memory, Task Management, Tool & MCP, Communication, or Predefined Flow)?
2. **Interface Implementation**: Does it follow the interface-first approach?
3. **Dependency Injection**: Are dependencies properly injected via Spring?
4. **Naming Conventions**: Do class and method names match the architecture patterns?
5. **Separation of Concerns**: Is business logic properly separated from infrastructure code?

Provide specific feedback on any violations and suggest fixes.