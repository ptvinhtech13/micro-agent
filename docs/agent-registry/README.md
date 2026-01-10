# Agent Registry APIs

Complete API documentation for Agent Registry service.

**Version**: 1.0.0
**Base URL**: `https://api.microagent.io/api/v1/agents`
**Protocol**: HTTPS only

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

**Last Updated**: 2026-01-10
**Maintained By**: API Team
