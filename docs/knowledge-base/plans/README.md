# Development Plans

This directory is for creating, tracking, and managing development plans for features, bug fixes, and refactoring work.

## 📂 Directory Structure

```
plans/
├── README.md           # This file - instructions for using plans
├── templates/          # Reusable plan templates
│   ├── feature-implementation-template.md
│   ├── bug-fix-template.md
│   └── refactoring-template.md
├── active/            # Currently in-progress plans
│   └── [your-plan-name].md
└── completed/         # Finished plans (for reference)
    └── [completed-plan-name].md
```

## 🎯 Purpose

The plans folder helps:
- **Claude AI** understand complex features before implementation
- **Team members** review and approve implementation approaches
- **Track progress** on multi-step development tasks
- **Document decisions** made during development
- **Knowledge sharing** through completed plans

## 📝 When to Create a Plan

Create a plan for:
- ✅ New feature implementation (3+ files affected)
- ✅ Complex bug fixes requiring multiple changes
- ✅ Refactoring work affecting architecture
- ✅ API design requiring team input
- ✅ Database schema changes
- ✅ Integration with external services

**Don't** create a plan for:
- ❌ Simple bug fixes (1-2 line changes)
- ❌ Typo corrections
- ❌ Minor documentation updates
- ❌ Trivial refactoring

## 🚀 How to Use Plans

### For Claude AI

1. **Before starting complex work:**
   ```
   1. Copy appropriate template from templates/ to active/
   2. Rename: [YYYY-MM-DD]-[feature-name].md
   3. Fill out all sections in the template
   4. Analyze codebase to complete technical details
   5. Present plan to user for approval
   ```

2. **During implementation:**
   ```
   1. Reference the plan for implementation steps
   2. Update the plan as you make progress
   3. Document any deviations or changes
   4. Check off completed tasks
   ```

3. **After completion:**
   ```
   1. Mark all tasks as complete
   2. Add "Completed" date at top
   3. Move from active/ to completed/
   4. Update main README if needed
   ```

### For Team Members

1. **Review plans** before Claude starts implementation
2. **Provide feedback** on approach and architecture
3. **Approve or request changes** to the plan
4. **Reference completed plans** for similar work

## 📋 Plan Template Structure

All plans should include:

1. **Overview**
   - Feature/Task description
   - Business requirements
   - Success criteria

2. **Technical Analysis**
   - Affected components
   - Dependencies
   - Architecture considerations

3. **Implementation Steps**
   - Detailed step-by-step plan
   - Files to create/modify
   - Order of implementation

4. **Testing Strategy**
   - Unit tests needed
   - Integration tests needed
   - Test scenarios

5. **Risks & Considerations**
   - Potential issues
   - Alternative approaches
   - Migration needs

## 🔍 Example: Creating a Plan

### Scenario: Implementing User Authentication

```bash
# 1. Copy template
cp templates/feature-implementation-template.md active/2026-01-10-user-authentication.md

# 2. Fill out template with:
- Feature description
- Technical requirements
- Implementation steps
- Testing approach

# 3. Review with team

# 4. Implement following the plan

# 5. Move to completed when done
mv active/2026-01-10-user-authentication.md completed/
```

## 📌 Plan Naming Convention

Use this format: `[YYYY-MM-DD]-[brief-description].md`

Examples:
- `2026-01-10-user-authentication.md`
- `2026-01-10-fix-agent-registration-bug.md`
- `2026-01-10-refactor-task-execution.md`

## ✅ Best Practices

### For Complex Features
1. Break down into smaller sub-tasks
2. Identify all affected modules
3. Consider migration/backward compatibility
4. Plan rollback strategy
5. Document API changes

### For Bug Fixes
1. Identify root cause
2. List affected areas
3. Plan regression tests
4. Consider edge cases

### For Refactoring
1. Justify the refactoring
2. List all files to change
3. Plan incremental approach
4. Ensure tests cover changes

## 🤖 Instructions for Claude AI

### Creating Plans

When asked to implement a complex feature:

1. **Analyze first**
   - Explore codebase using Glob/Grep
   - Understand existing patterns
   - Identify dependencies

2. **Draft plan**
   - Use appropriate template
   - Fill in all sections thoroughly
   - Be specific about files and changes

3. **Present to user**
   - Summarize key points
   - Highlight decisions needed
   - Ask for approval

4. **Implement**
   - Follow plan step-by-step
   - Update plan as you progress
   - Document deviations

### Using Existing Plans

1. Check `completed/` for similar features
2. Reference implementation patterns
3. Apply lessons learned
4. Adapt approach as needed

### Plan Quality Checklist

Before presenting a plan:
- [ ] All template sections filled out
- [ ] Implementation steps are specific
- [ ] Files to modify are listed
- [ ] Testing strategy is clear
- [ ] Risks are identified
- [ ] Alternative approaches considered
- [ ] Follows architectural guidelines
- [ ] Aligns with coding conventions

## 📚 Related Documentation

- [Feature Guidelines](../feature-guidelines/README.md)
- [Implementation Checklist](../feature-guidelines/implementation-checklist.md)
- [Architecture Overview](../architecture/README.md)
- [Coding Conventions](../coding-conventions/README.md)

---

**Pro Tip**: Good plans save time! Spend 15-20% of your time planning to avoid 50%+ rework.

**Last Updated**: 2026-01-10
**Maintained By**: Architecture Team
