# Coding Conventions

This directory contains all coding standards and best practices for the MicroAgent project.

## 📋 Contents

1. **[Java Coding Standards](./java-coding-standards.md)**
   - Naming conventions
   - Code organization
   - Documentation standards

2. **[Naming Conventions](./naming-conventions.md)**
   - Class naming
   - Method naming
   - Variable naming
   - Package naming

3. **[Best Practices](./best-practices.md)**
   - Transaction management
   - Exception handling
   - Logging
   - Validation
   - Security
   - Performance

4. **[Code Review Checklist](./code-review-checklist.md)**
   - Pre-commit checklist
   - Review guidelines
   - Quality gates

## 🎯 Quick Rules

### Critical Architecture Rules
- ❌ **NEVER** put business logic in API layer
- ❌ **NEVER** let Core layer depend on Data Access layer
- ❌ **NEVER** use JPA entities in Core layer
- ❌ **NEVER** expose domain entities in API responses
- ✅ **ALWAYS** use MapStruct for mapping between layers
- ✅ **ALWAYS** follow CQRS naming patterns
- ✅ **ALWAYS** keep Shared module dependency-free

### Code Quality Standards
- ✅ Minimum 80% test coverage
- ✅ No compiler warnings
- ✅ All tests must pass
- ✅ Run `mvn spotless:apply` before commit
- ✅ Follow Lombok best practices

## 🚀 For Claude AI

When writing code:
1. Read the relevant sections in this directory first
2. Follow ALL naming conventions exactly
3. Apply best practices consistently
4. Use the code review checklist before marking tasks complete
5. Reference specific rules when making architectural decisions

---

**See Also**: [Feature Guidelines](../feature-guidelines/README.md) | [Architecture](../architecture/README.md)
