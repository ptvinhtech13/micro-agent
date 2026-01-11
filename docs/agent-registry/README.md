# Agent Registry APIs

Complete API documentation for Agent Registry service.

**Version**: 1.0.0
**Base URL**: `https://api.microagent.io/api/v1/agents`
**Protocol**: HTTPS only

---

## Architecture Overview

The Agent Registry uses a simplified 4-table architecture:

- **`agents`** - Core agent profiles with identity, domain, tags, model config, and custom metadata
- **`agent_tags`** - Standardized, reusable tag registry for categorization
- **`agent_policy_mappings`** - One-to-Many mapping (one agent → many policy bundles)
- **`policy_bundles`** - Policy governance and compliance rules

**Key Features:**
- ✅ Tags stored as JSONB array of tag IDs for flexibility
- ✅ Model configuration separated from custom metadata
- ✅ One agent can have multiple policy bundles
- ✅ No AgentType enum (use tags or metadata instead)

For detailed architecture documentation, see `/docs/architecture/agent_registry_architecture.md`

---

## API Endpoints

### Agent Management APIs

| API Name | Method | Endpoint | Version | Documentation |
|----------|--------|----------|---------|---------------|
| Create Agent Profile API | POST | `/api/v1/agents` | V1 | [[V1] Create Agent Profile API](./%5BV1%5D%20Create%20Agent%20Profile%20API/) |
| Get Agent Profile API | GET | `/api/v1/agents/{agentId}` | V1 | [[V1] Get Agent Profile API](./%5BV1%5D%20Get%20Agent%20Profile%20API/) |
| Query Agent Profiles API | GET | `/api/v1/agents` | V1 | [[V1] Query Agent Profiles API](./%5BV1%5D%20Query%20Agent%20Profiles%20API/) |
| Update Agent Profile API | PUT | `/api/v1/agents/{agentId}` | V1 | [[V1] Update Agent Profile API](./%5BV1%5D%20Update%20Agent%20Profile%20API/) |
| Patch Agent Profile API | PATCH | `/api/v1/agents/{agentId}` | V1 | [[V1] Patch Agent Profile API](./%5BV1%5D%20Patch%20Agent%20Profile%20API/) |
| Delete Agent Profile API | DELETE | `/api/v1/agents/{agentId}` | V1 | [[V1] Delete Agent Profile API](./%5BV1%5D%20Delete%20Agent%20Profile%20API/) |

### Agent Operations APIs

| API Name | Method | Endpoint | Version | Documentation |
|----------|--------|----------|---------|---------------|
| Enable Agent API | POST | `/api/v1/agents/{agentId}/enable` | V1 | [[V1] Enable Agent API](./%5BV1%5D%20Enable%20Agent%20API/) |
| Disable Agent API | POST | `/api/v1/agents/{agentId}/disable` | V1 | [[V1] Disable Agent API](./%5BV1%5D%20Disable%20Agent%20API/) |

### Search & Discovery APIs

| API Name | Method | Endpoint | Version | Documentation |
|----------|--------|----------|---------|---------------|
| Find Agents By Domain API | GET | `/api/v1/agents/domain/{domain}` | V1 | [[V1] Find Agents By Domain API](./%5BV1%5D%20Find%20Agents%20By%20Domain%20API/) |
| Find Agents By Capability API | GET | `/api/v1/agents/capability/{capability}` | V1 | [[V1] Find Agents By Capability API](./%5BV1%5D%20Find%20Agents%20By%20Capability%20API/) |
| Search Agents API | POST | `/api/v1/agents/search` | V1 | [[V1] Search Agents API](./%5BV1%5D%20Search%20Agents%20API/) |

### Version Management APIs

| API Name | Method | Endpoint | Version | Documentation |
|----------|--------|----------|---------|---------------|
| Create Agent Version API | POST | `/api/v1/agents/{agentId}/versions` | V1 | [[V1] Create Agent Version API](./%5BV1%5D%20Create%20Agent%20Version%20API/) |
| Get Agent Versions API | GET | `/api/v1/agents/{agentId}/versions` | V1 | [[V1] Get Agent Versions API](./%5BV1%5D%20Get%20Agent%20Versions%20API/) |
| Rollback Agent Version API | POST | `/api/v1/agents/{agentId}/rollback/{versionId}` | V1 | [[V1] Rollback Agent Version API](./%5BV1%5D%20Rollback%20Agent%20Version%20API/) |

---

## Quick Reference

### Authentication
All APIs require JWT Bearer token authentication:
```
Authorization: Bearer <jwt-token>
```

### Rate Limiting
- Write Operations: 50 requests/minute
- Read Operations: 200 requests/minute
- Search Operations: 100 requests/minute

### Pagination
List endpoints support pagination:
- `page` - Page number (default: 0)
- `size` - Page size (default: 50, max: 100)
- `sort` - Sort field (default: createdAt)
- `direction` - asc or desc (default: desc)

### Error Response Format
```json
{
  "errorCode": "ERROR_CODE",
  "message": "Error description",
  "timestamp": "2026-01-10T10:30:00.000Z",
  "path": "/api/v1/agents/..."
}
```

---

## Agent Profile Structure

### Core Fields

| Field | Type | Required | Description |
|-------|------|----------|-------------|
| `agentId` | String (UUID) | Yes | Unique agent identifier |
| `name` | String | Yes | Unique agent name (case-insensitive) |
| `description` | String | No | Human-readable description |
| `version` | String | Yes | Semantic version (MAJOR.MINOR.PATCH) |
| `status` | Enum | Yes | DRAFT, ACTIVE, INACTIVE, ARCHIVED |
| `domain` | String | Yes | Business domain (e.g., "customer-service") |
| `tags` | Array[String] | No | Array of tag IDs from agent_tags table |
| `modelConfig` | Object | No | Model configuration (provider, modelId, etc.) |
| `metadata` | Object | No | Custom key-value metadata |
| `ownerId` | String | Yes | Owner user ID |
| `ownerType` | String | Yes | USER, TEAM, ORGANIZATION |

### Example Agent Profile

```json
{
  "agentId": "550e8400-e29b-41d4-a716-446655440000",
  "name": "customer-support-agent",
  "description": "AI agent for handling customer support inquiries",
  "version": "1.2.0",
  "status": "ACTIVE",
  "domain": "customer-service",
  "tags": [
    "tag-550e8400-e29b-41d4-a716-446655440001",
    "tag-550e8400-e29b-41d4-a716-446655440002"
  ],
  "modelConfig": {
    "provider": "anthropic",
    "modelId": "claude-3-opus",
    "temperature": 0.7,
    "maxTokens": 4096
  },
  "metadata": {
    "priority": 10,
    "maxConcurrentRequests": 100,
    "greetingMessage": "Hello! How can I help you?",
    "featureFlags": {
      "enableStreaming": true,
      "enableCaching": false
    }
  },
  "ownerId": "user-123",
  "ownerType": "USER",
  "createdAt": "2026-01-05T10:30:00Z",
  "updatedAt": "2026-01-08T14:22:00Z",
  "createdBy": "user-123",
  "updatedBy": "user-123"
}
```

---

## Tag System

### AgentTag Structure

Tags are standardized, categorized, and reusable across all agents.

**Categories:**
- `CAPABILITY` - What the agent can do (e.g., "customer-support", "order-processing")
- `DOMAIN` - Business domain (e.g., "banking", "healthcare")
- `FEATURE` - Technical features (e.g., "streaming", "multi-modal")
- `TOOL` - Tools/integrations (e.g., "web-search", "database-access")
- `LANGUAGE` - Languages supported (e.g., "english", "spanish")
- `CUSTOM` - User-defined tags

**Usage:**
- Agents reference tags by tag_id (UUID)
- Tags are stored as JSONB array in the agents table
- Many-to-many relationship without junction table

---

## Policy Management

### Agent-Policy Relationship

One agent can have multiple policy bundles through the `agent_policy_mappings` table.

**Use Cases:**
- Apply different policies for different environments (dev, staging, prod)
- Version control for policy updates
- Gradual rollout of new policies
- Compliance tracking and audit

**Mapping Structure:**
```json
{
  "mappingId": "mapping-uuid",
  "agentId": "agent-uuid",
  "policyBundleId": "policy-uuid",
  "policyVersion": "1.0.0",
  "isActive": true,
  "appliedAt": "2026-01-10T12:00:00Z",
  "appliedBy": "user-123"
}
```

---

**Last Updated**: 2026-01-11
**Maintained By**: API Team
