# Templates

## API Documentation Template

The API documentation template has been consolidated into a single, self-contained file:

**Location**: `.claude/commands/new-api-doc.md`

This file contains:
- Instructions for Claude on how to generate documentation
- Questions to ask users (Q1-Q10)
- Complete template structure (with and without audit support)
- All examples and formatting guidelines

## Usage

Run the slash command:
```
/new-api-doc
```

Claude will:
1. Ask you 10 questions about your API
2. Generate complete documentation based on your answers
3. Save it to the location you specify

## No External Dependencies

The template is **self-contained** - it doesn't reference external files like `[V1] Create Agent Profile API` anymore. Everything needed to generate documentation is embedded in the single template file.

This eliminates waste and ensures consistent, efficient documentation generation.
