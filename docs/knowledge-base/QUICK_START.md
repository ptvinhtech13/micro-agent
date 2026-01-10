# Quick Start Guide - MicroAgent Knowledge Base

This guide shows you and Claude AI how to effectively use the knowledge base.

## 🎯 For Team Members

### Starting a New Feature

1. **Review guidelines first**
   ```bash
   # Read these in order:
   1. docs/knowledge-base/feature-guidelines/README.md
   2. docs/knowledge-base/feature-guidelines/feature-structure.md
   3. docs/knowledge-base/coding-conventions/README.md
   ```

2. **Create a plan** (for complex features)
   ```bash
   # Copy template
   cp docs/knowledge-base/plans/templates/feature-implementation-template.md \
      docs/knowledge-base/plans/active/2026-01-10-my-feature.md

   # Fill out the template
   # Review with team
   # Get approval
   ```

3. **Implement following the plan**
   - Use the implementation checklist
   - Follow coding conventions
   - Write tests as you go

4. **Mark as complete**
   ```bash
   # Move to completed
   mv docs/knowledge-base/plans/active/2026-01-10-my-feature.md \
      docs/knowledge-base/plans/completed/
   ```

### Fixing a Bug

1. **Use bug-fix template**
   ```bash
   cp docs/knowledge-base/plans/templates/bug-fix-template.md \
      docs/knowledge-base/plans/active/2026-01-10-fix-registration-bug.md
   ```

2. **Analyze root cause**
3. **Document solution**
4. **Implement with tests**
5. **Move to completed when done**

## 🤖 For Claude AI

### Example: Implementing a New Feature

**User Request**: "Add user authentication to the agent registry service"

**Claude AI Should**:

1. **Start by acknowledging and setting up plan**
   ```
   "I'll help you implement user authentication. This is a complex feature,
   so let me create a plan first for your review."
   ```

2. **Read the knowledge base**
   ```
   Read: docs/knowledge-base/feature-guidelines/feature-structure.md
   Read: docs/knowledge-base/feature-guidelines/rest-api-design.md
   Read: docs/knowledge-base/coding-conventions/README.md
   ```

3. **Explore the codebase**
   ```
   Glob: Find existing services to understand patterns
   Grep: Search for authentication-related code
   Read: Examine similar features
   ```

4. **Create a plan**
   ```
   Copy: feature-implementation-template.md → active/2026-01-10-user-authentication.md
   Fill out:
   - Overview and requirements
   - Technical analysis
   - Implementation steps (detailed!)
   - Files to create/modify
   - API endpoints design
   - Testing strategy
   ```

5. **Present plan to user**
   ```
   "I've created a comprehensive implementation plan. Here's the summary:

   [High-level overview]

   The full plan is at: docs/knowledge-base/plans/active/2026-01-10-user-authentication.md

   Key decisions:
   1. [Decision 1]
   2. [Decision 2]

   Would you like me to proceed with this approach?"
   ```

6. **After approval, implement step-by-step**
   ```
   Phase 1: Shared Layer
   ✅ Create CreateUserRequest.java
   ✅ Create UserResponse.java
   ✅ Create UserApi.java
   ...

   Phase 2: Core Layer
   ✅ Create User domain entity
   ✅ Create CreateUserCommand
   ...
   ```

7. **Update plan as you go**
   ```
   Mark tasks as completed: [x]
   Document any deviations
   ```

8. **Mark complete when done**
   ```
   Update plan status to "Completed"
   Add completion date
   Move to completed/ folder
   ```

### Example: Code Review

**User Request**: "Review this code and make sure it follows our conventions"

**Claude AI Should**:

1. **Read the checklist**
   ```
   Read: docs/knowledge-base/coding-conventions/code-review-checklist.md
   ```

2. **Check against standards**
   ```
   Read: docs/knowledge-base/coding-conventions/java-coding-standards.md
   Read: docs/knowledge-base/coding-conventions/naming-conventions.md
   ```

3. **Provide structured feedback**
   ```
   "I've reviewed the code against our coding conventions. Here are my findings:

   ✅ Follows:
   - Proper CQRS naming
   - Uses MapStruct for mapping
   - Has @Transactional annotations

   ❌ Issues:
   1. Line 45: Business logic in controller (should be in service)
      - Violates rule: docs/knowledge-base/coding-conventions/README.md#critical-rules
   2. Line 78: Missing @Valid on request DTO
   3. Line 120: Using JPA entity in API response (should use DTO)

   📝 Suggestions:
   1. Extract validation logic to utility class
   2. Add Javadoc to public methods
   "
   ```

## 📖 Common Workflows

### Workflow 1: "Help me implement [Feature]"

```
1. Claude reads:
   - feature-guidelines/feature-structure.md
   - feature-guidelines/implementation-checklist.md
   - coding-conventions/README.md

2. Claude explores codebase:
   - Find similar features
   - Understand patterns
   - Identify affected modules

3. Claude creates plan:
   - Use feature-implementation-template.md
   - Fill out all sections
   - Present to user for approval

4. User reviews and approves

5. Claude implements:
   - Follow plan step-by-step
   - Follow coding conventions
   - Update plan as progressing
   - Run tests

6. Complete and move plan to completed/
```

### Workflow 2: "Fix this bug"

```
1. Claude reads:
   - plans/templates/bug-fix-template.md

2. Claude investigates:
   - Reproduce the bug
   - Analyze root cause
   - Document findings

3. Claude creates plan:
   - Use bug-fix-template.md
   - Document root cause
   - Propose solution
   - Present to user

4. User approves approach

5. Claude implements fix:
   - Make changes
   - Add regression tests
   - Verify fix

6. Complete and move plan to completed/
```

### Workflow 3: "Review this code"

```
1. Claude reads:
   - coding-conventions/code-review-checklist.md
   - coding-conventions/best-practices.md

2. Claude analyzes code:
   - Check naming conventions
   - Verify layer boundaries
   - Check for anti-patterns

3. Claude provides feedback:
   - List what's good
   - List issues with references to docs
   - Suggest improvements

4. Claude can fix issues if requested
```

## 🎓 Learning from Past Work

### Reference Completed Plans

```bash
# Find similar completed work
ls docs/knowledge-base/plans/completed/

# Example: Implementing similar feature?
# Read: docs/knowledge-base/plans/completed/2025-12-15-task-execution.md
# Learn: Implementation patterns, decisions made, issues encountered
```

### Build Organizational Knowledge

Every completed plan becomes:
- A reference for similar future work
- Documentation of decisions made
- Examples of how to structure features
- Lessons learned repository

## 📋 Checklists for Claude AI

### Before Starting ANY Task

- [ ] Read relevant knowledge base sections
- [ ] Explore codebase for similar patterns
- [ ] Create plan if complex (3+ files affected)
- [ ] Present plan to user if created
- [ ] Get user approval before implementation

### During Implementation

- [ ] Follow the plan (if created)
- [ ] Follow coding conventions
- [ ] Update plan as progressing
- [ ] Mark checklist items as completed
- [ ] Reference knowledge base when uncertain

### Before Marking Complete

- [ ] All tests pass
- [ ] Code follows conventions
- [ ] Implementation checklist complete
- [ ] Plan updated (if exists)
- [ ] Plan moved to completed/ (if exists)

## 🚀 Tips for Success

### For Claude AI
1. **Always read first** - Don't guess, read the knowledge base
2. **Be specific in plans** - List exact files and changes
3. **Reference docs** - When explaining decisions, link to relevant docs
4. **Update as you go** - Keep plans current during implementation
5. **Learn from history** - Check completed plans for similar work

### For Team Members
1. **Keep KB updated** - Update docs when patterns change
2. **Review plans** - Provide feedback on Claude's plans
3. **Add templates** - Create new templates for common tasks
4. **Share completed plans** - They're valuable references

## 📞 Need Help?

- **Architecture questions**: See `architecture/README.md`
- **Coding questions**: See `coding-conventions/README.md`
- **Feature questions**: See `feature-guidelines/README.md`
- **Planning questions**: See `plans/README.md`

---

**Remember**: The knowledge base is here to help maintain consistency and quality. Use it!

**Last Updated**: 2026-01-10
