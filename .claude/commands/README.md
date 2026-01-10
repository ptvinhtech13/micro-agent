# Available Slash Commands

Quick reference for custom commands in this project.

## Getting Started
- Read `GUIDE.md` for comprehensive documentation on how to use and create slash commands
- Commands are invoked by typing `/command-name` in the chat

## Available Commands

### `/review-arch`
Review current code against the project architecture.
- Checks layer separation, interfaces, dependency injection
- Validates naming conventions and separation of concerns
- Provides specific feedback and suggested fixes

**Usage:** Open a file and type `/review-arch`

---

### `/new-component`
Create a new component following architecture patterns.
- Generates interface and implementation
- Includes proper annotations and structure
- Follows package and naming conventions

**Usage:** `/new-component` (then specify component type)

---

### `/explain-layer`
Explain the 6-layer architecture of this project.
- Overview of all layers
- Key components in each layer
- Data flow between layers
- Context about current file (if open)

**Usage:** `/explain-layer`

---

### `/validate-interfaces`
Validate interfaces and implementations against architecture specs.
- Checks method completeness
- Validates signatures and types
- Identifies missing implementations

**Usage:** Open a file and type `/validate-interfaces`

---

### `/new-api-doc`
Generate comprehensive API documentation using standardized template.
- Creates sequence diagrams with microservice boundaries
- Generates pseudocode for processing steps
- Includes validation rules and timing expectations
- Follows Edge auth/authz pattern

**Usage:** `/new-api-doc` (then answer questions about your API)

---

## Creating Your Own Commands

1. Create a new `.md` file in this folder
2. Write the prompt/instructions for Claude
3. Use kebab-case for the filename (e.g., `my-command.md`)
4. Invoke with `/my-command`

See `GUIDE.md` for detailed instructions and best practices.

## Examples

```bash
# Review architecture compliance
/review-arch

# Create new service
/new-component

# Understand the architecture
/explain-layer

# Validate your interfaces
/validate-interfaces

# Generate API documentation
/new-api-doc
```

## Tips

- Commands can reference project files (e.g., `/docs/architecture/`)
- They work best when combined with `.claude/conventions.md`
- You can pass arguments after the command name
- Keep commands focused and specific

## Need Help?

- Read `GUIDE.md` for full documentation
- Check `.claude/conventions.md` for coding standards
- See `.claude/project-context.md` for architecture overview
