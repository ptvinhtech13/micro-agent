# Custom Slash Commands Guide

## What are Slash Commands?

Slash commands are custom shortcuts you can create to automate repetitive tasks when working with Claude Code. They are markdown files in the `.claude/commands/` folder that contain prompts Claude will execute when you type `/command-name`.

## How to Use Slash Commands

### Basic Usage
1. Create a `.md` file in `.claude/commands/` (e.g., `review-code.md`)
2. Write the prompt/instructions you want Claude to follow
3. Use it by typing `/review-code` in chat

### Example
If you create `.claude/commands/explain-arch.md` with content:
```markdown
Explain the architecture of this project based on /docs/architecture/
```

Then typing `/explain-arch` will expand to that prompt and Claude will execute it.

## Why Use Slash Commands?

### 1. **Consistency**
Ensure Claude follows the same process every time for common tasks like:
- Code reviews against your architecture
- Creating new components with proper structure
- Running tests with specific configurations

### 2. **Speed**
Instead of typing long instructions repeatedly, just type a short command:
- `/review-arch` instead of "Review this code against our architecture patterns in docs/architecture/"
- `/new-service UserService` instead of explaining the full structure every time

### 3. **Team Alignment**
Share commands with your team so everyone:
- Gets consistent code reviews
- Follows the same patterns
- Uses the same testing approach

### 4. **Context Preservation**
Commands can reference your:
- Architecture docs
- Coding conventions
- Project-specific patterns
- Testing strategies

## Best Practices

### 1. **Be Specific**
Good:
```markdown
Review the current file against the layered architecture in /docs/architecture/high_level_java_design.md.
Check: interface segregation, dependency injection, proper layer separation.
```

Better than:
```markdown
Review this code
```

### 2. **Include Context**
Reference your project files:
```markdown
Create a new service following the patterns in /docs/architecture/
- Implement the interface-first approach
- Add proper Spring annotations
- Include basic error handling
```

### 3. **Use Parameters**
You can reference arguments after the command name:
```markdown
Create a new component for {component_name} following our architecture patterns.
```

Then use: `/new-component UserAuthentication`

### 4. **Combine with Conventions**
Commands work great with `.claude/conventions.md`:
```markdown
Review this code against our conventions and architecture.
Flag any violations of:
1. Layer separation
2. Dependency injection patterns
3. Naming conventions
```

## Command Naming Tips

- Use kebab-case: `review-arch`, not `reviewArch` or `review_arch`
- Be descriptive: `create-service-layer`, not `create`
- Group related commands: `test-unit`, `test-integration`, `test-all`

## Example Use Cases

### Code Review
```
/review-arch
/check-tests
/lint-code
```

### Creation
```
/new-service UserService
/new-controller PaymentController
/new-tool DatabaseQueryTool
```

### Testing & Quality
```
/run-tests
/check-coverage
/analyze-performance
```

### Documentation
```
/update-readme
/gen-api-docs
/explain-component TaskDecomposer
```

## Tips for This Project

Given your microagent architecture, useful commands might include:
- `/new-layer-component` - Create components following the 6-layer architecture
- `/review-against-design` - Check code against high-level design docs
- `/validate-interfaces` - Ensure proper interface implementation
- `/check-spring-config` - Verify Spring bean configuration

## Advanced: Dynamic Commands

You can create commands that adapt based on context:
```markdown
Analyze the current file and determine which layer it belongs to based on
/docs/architecture/high_level_java_design.md. Then verify it follows the
correct patterns for that layer.
```

## Getting Started

1. Look at the example commands in this folder
2. Copy and modify them for your needs
3. Create new commands for tasks you do frequently
4. Share useful commands with your team

## Learn More

See Claude Code documentation: https://code.claude.com/docs