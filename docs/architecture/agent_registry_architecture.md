# Agent Registry Architecture

## Overview

The **Agent Registry** is a central catalog system for managing agent profiles with a **simplified, flexible design**.

**Design Philosophy:**
- Keep core fields in structured columns
- Use flexible metadata (JSONB) for configuration
- Use standardized tags for categorization and capabilities
- Use policy bundles for governance
- **Avoid over-normalization** - simplicity over premature abstraction

---

## Simplified Data Model

### 1. AgentProfile

```java
@Data
@Builder
@Entity
@Table(name = "agents")
public class AgentProfile {

    // ========================================
    // CORE IDENTITY
    // ========================================
    @Id
    @Column(name = "agent_id")
    private String agentId;              // UUID

    @Column(name = "name", nullable = false, unique = true)
    private String name;                 // Unique agent name

    @Column(name = "description")
    private String description;          // What does this agent do?

    @Column(name = "version", nullable = false)
    private String version;              // Semantic version: "1.0.0"

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private AgentStatus status;          // DRAFT, ACTIVE, INACTIVE, ARCHIVED

    @Column(name = "domain")
    private String domain;               // Primary domain (e.g., "banking", "healthcare")

    // ========================================
    // OWNERSHIP
    // ========================================
    @Column(name = "owner_id")
    private String ownerId;              // Who owns this agent

    @Column(name = "owner_type")
    private String ownerType;            // USER, TEAM, ORGANIZATION

    // ========================================
    // TAGS (standardized, reusable across agents)
    // ========================================
    @Type(JsonBinaryType.class)
    @Column(name = "tags", columnDefinition = "jsonb")
    private Set<String> tags;            // Array of tag_ids (UUIDs) from agent_tags

    // ========================================
    // MODEL CONFIGURATION
    // ========================================
    @Type(JsonBinaryType.class)
    @Column(name = "model_config", columnDefinition = "jsonb")
    private ModelConfig modelConfig;     // Model configuration (provider, modelId, etc.)

    // ========================================
    // FLEXIBLE METADATA (custom key-value)
    // ========================================
    @Type(JsonBinaryType.class)
    @Column(name = "metadata", columnDefinition = "jsonb")
    private Map<String, Object> metadata;  // Custom metadata for extensibility

    // ========================================
    // AUDIT FIELDS
    // ========================================
    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    @Column(name = "created_by")
    private String createdBy;

    @Column(name = "updated_by")
    private String updatedBy;
}

public enum AgentStatus {
    DRAFT,       // In development
    ACTIVE,      // Live and available
    INACTIVE,    // Temporarily disabled
    ARCHIVED     // Retired/deprecated
}
```

---

### 2. ModelConfig (Model Configuration)

```java
@Data
@Builder
public class ModelConfig {
    private String provider;             // "anthropic", "openai"
    private String modelId;              // "claude-3-opus", "gpt-4"
    private Float temperature;           // 0.0 - 1.0
    private Integer maxTokens;           // Token limit
    private Map<String, Object> additionalParams;
}
```

**Example model_config JSON:**
```json
{
  "provider": "anthropic",
  "modelId": "claude-3-opus",
  "temperature": 0.7,
  "maxTokens": 4096,
  "additionalParams": {
    "topP": 0.9,
    "topK": 40
  }
}
```

### 3. Metadata (Custom Key-Value)

**Flexible metadata is a simple key-value object for any custom configuration:**

```java
// In AgentProfile:
@Type(JsonBinaryType.class)
@Column(name = "metadata", columnDefinition = "jsonb")
private Map<String, Object> metadata;  // Custom metadata for extensibility
```

**Example metadata JSON:**
```json
{
  "priority": 10,
  "maxConcurrentRequests": 100,
  "requestTimeout": "PT30S",
  "allowedUserRoles": ["admin", "operator", "user"],
  "greetingMessage": "Hello! How can I help you today?",
  "maxRetries": 3,
  "enableVerboseLogging": false,
  "specialInstructions": "Always be polite and professional",
  "featureFlags": {
    "enableStreaming": true,
    "enableCaching": false
  }
}
```

---

### 4. Tag System (Robust & Reusable)

**Tags are standardized, categorized, and reusable across all agents.**

#### AgentTag Table

```java
@Data
@Builder
@Entity
@Table(name = "agent_tags")
public class AgentTag {

    @Id
    @Column(name = "tag_id")
    private String tagId;                // Primary key: UUID (e.g., "tag-550e8400-...")

    @Column(name = "tag_name", nullable = false, unique = true)
    private String tagName;              // Unique tag name: e.g., "customer-support"

    @Column(name = "display_name")
    private String displayName;          // e.g., "Customer Support"

    @Column(name = "description")
    private String description;          // What this tag means

    @Enumerated(EnumType.STRING)
    @Column(name = "category", nullable = false)
    private TagCategory category;        // CAPABILITY, DOMAIN, FEATURE, TOOL, LANGUAGE, CUSTOM

    @Column(name = "is_active")
    private Boolean isActive;            // Can be used in new agents

    @Column(name = "usage_count")
    private Integer usageCount;          // How many agents use this tag

    @Column(name = "created_at")
    private Instant createdAt;

    @Column(name = "created_by")
    private String createdBy;
}

public enum TagCategory {
    CAPABILITY,      // What the agent can do (e.g., "order-processing", "refunds")
    DOMAIN,          // Business domain (e.g., "finance", "healthcare")
    FEATURE,         // Technical features (e.g., "streaming", "multi-modal")
    TOOL,            // Tools/integrations (e.g., "web-search", "database-access")
    LANGUAGE,        // Languages supported (e.g., "english", "spanish")
    CUSTOM           // User-defined tags
}
```

#### Predefined Tag Examples

```sql
INSERT INTO agent_tags (tag_id, tag_name, display_name, description, category, is_active, usage_count, created_at, created_by) VALUES

-- CAPABILITY tags
('tag-' || gen_random_uuid(), 'customer-support', 'Customer Support', 'Handle customer inquiries and support tickets', 'CAPABILITY', true, 0, NOW(), 'system'),
('tag-' || gen_random_uuid(), 'order-processing', 'Order Processing', 'Process and manage orders', 'CAPABILITY', true, 0, NOW(), 'system'),
('tag-' || gen_random_uuid(), 'refunds', 'Refunds', 'Handle refund requests and processing', 'CAPABILITY', true, 0, NOW(), 'system'),
('tag-' || gen_random_uuid(), 'appointment-scheduling', 'Appointment Scheduling', 'Schedule and manage appointments', 'CAPABILITY', true, 0, NOW(), 'system'),
('tag-' || gen_random_uuid(), 'data-analysis', 'Data Analysis', 'Analyze data and generate insights', 'CAPABILITY', true, 0, NOW(), 'system'),

-- DOMAIN tags
('tag-' || gen_random_uuid(), 'banking', 'Banking', 'Banking and financial services domain', 'DOMAIN', true, 0, NOW(), 'system'),
('tag-' || gen_random_uuid(), 'healthcare', 'Healthcare', 'Healthcare and medical services', 'DOMAIN', true, 0, NOW(), 'system'),
('tag-' || gen_random_uuid(), 'ecommerce', 'E-Commerce', 'Online retail and e-commerce', 'DOMAIN', true, 0, NOW(), 'system'),
('tag-' || gen_random_uuid(), 'education', 'Education', 'Educational services and learning', 'DOMAIN', true, 0, NOW(), 'system'),

-- FEATURE tags
('tag-' || gen_random_uuid(), 'streaming', 'Streaming', 'Supports streaming responses', 'FEATURE', true, 0, NOW(), 'system'),
('tag-' || gen_random_uuid(), 'multi-modal', 'Multi-Modal', 'Supports text, images, and other modalities', 'FEATURE', true, 0, NOW(), 'system'),
('tag-' || gen_random_uuid(), 'memory', 'Long-term Memory', 'Maintains conversation history and context', 'FEATURE', true, 0, NOW(), 'system'),

-- TOOL tags
('tag-' || gen_random_uuid(), 'web-search', 'Web Search', 'Internet search capability', 'TOOL', true, 0, NOW(), 'system'),
('tag-' || gen_random_uuid(), 'calculator', 'Calculator', 'Mathematical calculations', 'TOOL', true, 0, NOW(), 'system'),
('tag-' || gen_random_uuid(), 'database-access', 'Database Access', 'Query databases', 'TOOL', true, 0, NOW(), 'system'),

-- LANGUAGE tags
('tag-' || gen_random_uuid(), 'english', 'English', 'English language support', 'LANGUAGE', true, 0, NOW(), 'system'),
('tag-' || gen_random_uuid(), 'spanish', 'Spanish', 'Spanish language support', 'LANGUAGE', true, 0, NOW(), 'system'),
('tag-' || gen_random_uuid(), 'french', 'French', 'French language support', 'LANGUAGE', true, 0, NOW(), 'system');
```

#### How Agents Link to Tags (Many-to-Many)

**Many-to-Many Relationship WITHOUT Junction Table:**

```
┌─────────────────────────────┐           ┌─────────────────────────────┐
│        agents               │           │      agent_tags             │
├─────────────────────────────┤           ├─────────────────────────────┤
│ agent_id (PK)               │           │ tag_id (PK)                 │
│ name                        │           │ tag_name (unique)           │
│ tags (JSONB) ───────────────┼──────────▶│ display_name                │
│   ["tag-uuid-1",            │  lookup   │ category                    │
│    "tag-uuid-2",            │           │ is_active                   │
│    "tag-uuid-3"]            │           │ usage_count                 │
└─────────────────────────────┘           └─────────────────────────────┘

Many agents → Many tags (via JSONB array of tag_id)
```

**How it works:**
1. **agents.tags** = JSONB array of tag IDs (e.g., `["tag-550e8400-...", "tag-660f9511-..."]`)
2. **agent_tags.tag_id** = Primary key (UUID) for stable reference
3. **agent_tags.tag_name** = Unique constraint for human-readable names
4. **No junction table** - JSONB provides flexibility
5. **Validation** - Application layer ensures tag IDs exist in agent_tags

**Why use tag_id instead of tag_name?**
- ✅ **Stable for dynamic creation** - Auto-generated UUIDs work perfectly for on-the-fly tag creation
- ✅ **Renameable** - Can change tag_name without updating all agents
- ✅ **Standard practice** - IDs are more stable than names for references
- ✅ **Smaller storage** - UUIDs more compact than long tag names
- ✅ **Fast lookups** - GIN index on JSONB for tag_id queries
- ✅ **No junction table** - Simpler schema
- ✅ **Flexible** - Add/remove tags without schema changes
- ❌ No foreign key constraint (must validate in code)

**Example:**

```json
{
  "agent_id": "agent-123",
  "name": "customer-service-bot",
  "tags": [
    "tag-550e8400-e29b-41d4-a716-446655440001",  // customer-support
    "tag-550e8400-e29b-41d4-a716-446655440002",  // order-processing
    "tag-550e8400-e29b-41d4-a716-446655440003",  // refunds
    "tag-550e8400-e29b-41d4-a716-446655440004",  // ecommerce
    "tag-550e8400-e29b-41d4-a716-446655440005",  // english
    "tag-550e8400-e29b-41d4-a716-446655440006",  // spanish
    "tag-550e8400-e29b-41d4-a716-446655440007"   // web-search
  ]
}
```

**Stored in database:**
```sql
SELECT agent_id, name, tags FROM agents WHERE agent_id = 'agent-123';

-- Result:
-- agent_id  | name                  | tags
-- agent-123 | customer-service-bot  | ["tag-550e8400-e29b-41d4-a716-446655440001", "tag-550e8400-e29b-41d4-a716-446655440002", ...]
```

**Human-readable query (joining with tag names):**
```sql
SELECT
    a.agent_id,
    a.name,
    array_agg(t.tag_name) as tag_names
FROM agents a
CROSS JOIN LATERAL jsonb_array_elements_text(a.tags) AS tag_id
JOIN agent_tags t ON t.tag_id = tag_id
WHERE a.agent_id = 'agent-123'
GROUP BY a.agent_id, a.name;

-- Result:
-- agent_id  | name                  | tag_names
-- agent-123 | customer-service-bot  | {customer-support, order-processing, refunds, ecommerce, english, spanish, web-search}
```

#### Tag Validation Flow

```
┌─────────────────────────────────────────────────────────────────┐
│ 1. User creates/updates agent with tags                         │
│    tags = ["tag-uuid-1", "tag-uuid-2", "invalid-tag-uuid"]     │
└────────────────────────┬────────────────────────────────────────┘
                         │
                         ▼
┌─────────────────────────────────────────────────────────────────┐
│ 2. TagService.validateTags(tags)                                │
│    - Check each tag_id exists in agent_tags                     │
│    - Check tag is active (is_active = true)                     │
└────────────────────────┬────────────────────────────────────────┘
                         │
                         ▼
┌─────────────────────────────────────────────────────────────────┐
│ 3. SQL: SELECT tag_id FROM agent_tags                           │
│         WHERE tag_id = ANY(ARRAY['tag-uuid-1', 'tag-uuid-2',    │
│                                   'invalid-tag-uuid'])           │
│         AND is_active = true                                    │
└────────────────────────┬────────────────────────────────────────┘
                         │
                         ▼
┌─────────────────────────────────────────────────────────────────┐
│ 4. Result: ["tag-uuid-1", "tag-uuid-2"]                         │
│    ❌ "invalid-tag-uuid" not found → ValidationException       │
└────────────────────────┬────────────────────────────────────────┘
                         │
                         ▼
┌─────────────────────────────────────────────────────────────────┐
│ 5. If valid: Save agent with validated tag_ids                  │
│    If invalid: Throw error with missing tag_ids                 │
└─────────────────────────────────────────────────────────────────┘
```

#### Querying the Many-to-Many Relationship

**1. Find agents by tag (one tag using tag_id):**
```sql
-- Find all agents with "customer-support" tag (using tag_id)
SELECT * FROM agents
WHERE tags @> '["tag-550e8400-e29b-41d4-a716-446655440001"]'::jsonb
  AND status = 'ACTIVE';
```

**1b. Find agents by tag name (lookup tag_id first):**
```sql
-- Find agents by tag name (requires join)
SELECT a.*
FROM agents a
JOIN agent_tags t ON a.tags @> jsonb_build_array(t.tag_id)
WHERE t.tag_name = 'customer-support'
  AND a.status = 'ACTIVE';
```

**2. Find agents by multiple tags (AND logic):**
```sql
-- Find agents with ALL these tag_ids
SELECT * FROM agents
WHERE tags @> '["tag-550e8400-e29b-41d4-a716-446655440001",
                "tag-550e8400-e29b-41d4-a716-446655440003",
                "tag-550e8400-e29b-41d4-a716-446655440005"]'::jsonb
  AND status = 'ACTIVE';
```

**3. Find agents by any tag (OR logic):**
```sql
-- Find agents with ANY of these tag_ids
SELECT * FROM agents
WHERE tags ?| array[
    'tag-550e8400-e29b-41d4-a716-446655440001',
    'tag-550e8400-e29b-41d4-a716-446655440003'
]
  AND status = 'ACTIVE';
```

**4. Find all agents using a specific tag:**
```sql
-- Which agents use the "customer-support" tag? (by tag_id)
SELECT agent_id, name, tags
FROM agents
WHERE tags @> '["tag-550e8400-e29b-41d4-a716-446655440001"]'::jsonb;
```

**5. Get tag usage statistics:**
```sql
-- How many agents use each tag?
SELECT
    tag_id,
    tag_name,
    usage_count,
    category
FROM agent_tags
ORDER BY usage_count DESC
LIMIT 10;
```

**6. Find tags used by a specific agent (with readable names):**
```sql
-- What tags does this agent have? (get tag details)
SELECT
    a.agent_id,
    a.name,
    t.tag_id,
    t.tag_name,
    t.category,
    t.display_name
FROM agents a
CROSS JOIN LATERAL jsonb_array_elements_text(a.tags) AS tag_id
JOIN agent_tags t ON t.tag_id = tag_id
WHERE a.agent_id = 'agent-123';
```

**7. Search agents by tag name pattern:**
```sql
-- Find agents with tags matching a pattern
SELECT DISTINCT a.*
FROM agents a
CROSS JOIN LATERAL jsonb_array_elements_text(a.tags) AS tag_id
JOIN agent_tags t ON t.tag_id = tag_id
WHERE t.tag_name LIKE '%support%'
  AND a.status = 'ACTIVE';
```

#### Tag Validation Service

**Key Operations:**

1. **Validate tag IDs**
   - Query: `SELECT tag_id FROM agent_tags WHERE tag_id = ANY(?) AND is_active = true`
   - Checks that all tag_ids exist and are active
   - Returns ValidationException if any tag_id is invalid

2. **Create new tag dynamically**
   - Generates new tag_id (UUID format: "tag-{uuid}")
   - Inserts into agent_tags
   - Returns the generated tag_id

3. **Get tag_id by tag_name**
   - Query: `SELECT tag_id FROM agent_tags WHERE tag_name = ? AND is_active = true`
   - Used for looking up existing tags by human-readable name

4. **Update tag usage count**
   - Query: `UPDATE agent_tags SET usage_count = usage_count + 1 WHERE tag_id = ANY(?)`
   - Increments/decrements usage count for tracking


---

### 5. AgentPolicyMapping (One-to-Many)

**Mapping table for agent-policy relationship (one agent can have multiple policy bundles)**

```java
@Data
@Builder
@Entity
@Table(name = "agent_policy_mappings")
public class AgentPolicyMapping {

    @Id
    @Column(name = "mapping_id")
    private String mappingId;            // Primary key: UUID

    @Column(name = "agent_id", nullable = false)
    private String agentId;              // FK to agents table

    @Column(name = "policy_bundle_id", nullable = false)
    private String policyBundleId;       // FK to policy_bundles table

    @Column(name = "policy_version", nullable = false)
    private String policyVersion;        // Version of policy bundle applied

    @Column(name = "is_active")
    private Boolean isActive;            // Is this mapping currently active?

    @Column(name = "applied_at")
    private Instant appliedAt;           // When was this policy applied

    @Column(name = "applied_by")
    private String appliedBy;            // Who applied this policy

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    @Column(name = "created_by")
    private String createdBy;
}
```

**Relationship:**
```
┌─────────────────────────────┐           ┌─────────────────────────────────────┐           ┌─────────────────────────────┐
│        agents               │           │   agent_policy_mappings             │           │    policy_bundles           │
├─────────────────────────────┤           ├─────────────────────────────────────┤           ├─────────────────────────────┤
│ agent_id (PK)               │◀──────────│ mapping_id (PK)                     │           │ bundle_id (PK)              │
│ name                        │    1:N    │ agent_id (FK)                       │───────────▶│ bundle_name                 │
│ ...                         │           │ policy_bundle_id (FK) ──────────────┼──────N:1──│ bundle_version              │
└─────────────────────────────┘           │ policy_version                      │           │ policies (JSONB)            │
                                          │ is_active                           │           │ ...                         │
                                          │ applied_at                          │           └─────────────────────────────┘
                                          └─────────────────────────────────────┘

One agent → Many policy bundles (via mapping table)
```

---

### 6. PolicyBundle (Governance)

**Keep policy as separate entity** (governance is critical)

```java
@Data
@Builder
@Entity
@Table(name = "policy_bundles")
public class PolicyBundle {

    @Id
    @Column(name = "bundle_id")
    private String bundleId;

    @Column(name = "bundle_version", nullable = false)
    private String bundleVersion;

    @Column(name = "bundle_name", nullable = false)
    private String bundleName;           // "Banking Agent Policy v1.0"

    // Policy content as JSONB
    @Type(JsonBinaryType.class)
    @Column(name = "policies", columnDefinition = "jsonb")
    private PolicyContent policies;

    @Column(name = "approved_by")
    private String approvedBy;

    @Column(name = "approved_at")
    private Instant approvedAt;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    @Column(name = "created_by")
    private String createdBy;
}
```

**Policy Content Structure (JSONB):**
- `purpose` - What the agent CAN/CANNOT do
- `corporate` - Brand guidelines
- `ethical` - Fairness, transparency requirements
- `regulatory` - GDPR, HIPAA compliance rules

See `agent_policy_governance.md` for detailed policy structures.

---

## Database Schema

```sql
-- ========================================
-- AGENTS TABLE
-- ========================================
CREATE TABLE agents (
    -- Core Identity
    agent_id VARCHAR(255) PRIMARY KEY,
    name VARCHAR(255) NOT NULL UNIQUE,
    description TEXT,
    version VARCHAR(50) NOT NULL,
    status VARCHAR(50) NOT NULL,
    domain VARCHAR(100),

    -- Ownership
    owner_id VARCHAR(255),
    owner_type VARCHAR(50),

    -- Tags (JSONB array) - standardized from agent_tags
    tags JSONB,

    -- Model Configuration (JSONB)
    model_config JSONB,

    -- Flexible Metadata (JSONB - custom key-value)
    metadata JSONB,

    -- Audit
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL,
    created_by VARCHAR(255),
    updated_by VARCHAR(255)
);

-- ========================================
-- AGENT TAGS TABLE (Standardized Tags)
-- ========================================
CREATE TABLE agent_tags (
    tag_id VARCHAR(255) PRIMARY KEY,       -- tag_id (UUID) is the primary key
    tag_name VARCHAR(255) NOT NULL UNIQUE, -- tag_name is unique for lookups
    display_name VARCHAR(255),
    description TEXT,
    category VARCHAR(50) NOT NULL,
    is_active BOOLEAN DEFAULT true,
    usage_count INTEGER DEFAULT 0,
    created_at TIMESTAMP NOT NULL,
    created_by VARCHAR(255)
);

-- ========================================
-- AGENT POLICY MAPPINGS TABLE
-- ========================================
CREATE TABLE agent_policy_mappings (
    mapping_id VARCHAR(255) PRIMARY KEY,
    agent_id VARCHAR(255) NOT NULL,
    policy_bundle_id VARCHAR(255) NOT NULL,
    policy_version VARCHAR(50) NOT NULL,
    is_active BOOLEAN DEFAULT true,
    applied_at TIMESTAMP,
    applied_by VARCHAR(255),
    created_at TIMESTAMP NOT NULL,
    created_by VARCHAR(255),

    CONSTRAINT fk_agent FOREIGN KEY (agent_id)
        REFERENCES agents(agent_id) ON DELETE CASCADE,
    CONSTRAINT fk_policy_bundle FOREIGN KEY (policy_bundle_id)
        REFERENCES policy_bundles(bundle_id)
);

-- ========================================
-- POLICY BUNDLES TABLE
-- ========================================
CREATE TABLE policy_bundles (
    bundle_id VARCHAR(255) PRIMARY KEY,
    bundle_version VARCHAR(50) NOT NULL,
    bundle_name VARCHAR(255) NOT NULL,
    policies JSONB NOT NULL,
    approved_by VARCHAR(255),
    approved_at TIMESTAMP,
    created_at TIMESTAMP NOT NULL,
    created_by VARCHAR(255),

    UNIQUE(bundle_id, bundle_version)
);

-- ========================================
-- INDEXES
-- ========================================
-- Agent indexes
CREATE INDEX idx_agents_name ON agents(name);
CREATE INDEX idx_agents_status ON agents(status);
CREATE INDEX idx_agents_domain ON agents(domain);
CREATE INDEX idx_agents_owner ON agents(owner_id);
CREATE INDEX idx_agents_tags ON agents USING GIN(tags);
CREATE INDEX idx_agents_model_config ON agents USING GIN(model_config);
CREATE INDEX idx_agents_metadata ON agents USING GIN(metadata);

-- Agent tags indexes
CREATE INDEX idx_agent_tags_category ON agent_tags(category);
CREATE INDEX idx_agent_tags_active ON agent_tags(is_active);
CREATE INDEX idx_agent_tags_name ON agent_tags(tag_name);  -- For lookups by name

-- Agent policy mappings indexes
CREATE INDEX idx_policy_mappings_agent ON agent_policy_mappings(agent_id);
CREATE INDEX idx_policy_mappings_bundle ON agent_policy_mappings(policy_bundle_id);
CREATE INDEX idx_policy_mappings_active ON agent_policy_mappings(is_active);
CREATE INDEX idx_policy_mappings_agent_active ON agent_policy_mappings(agent_id, is_active);

-- Policy bundle indexes
CREATE INDEX idx_policy_name ON policy_bundles(bundle_name);
CREATE INDEX idx_policy_approved ON policy_bundles(approved_by, approved_at);
```

---

## Service APIs

### Agent Registry Service

**CRUD Operations:**
- `create(profile)` - Create new agent profile
- `getById(agentId)` - Get agent by ID
- `update(agentId, profile)` - Update agent profile
- `delete(agentId)` - Delete agent

**Search & Discovery:**
- `findByDomain(domain)` - Find agents by domain
- `findByTag(tag)` - Find agents by single tag_id
- `findByTags(tags)` - Find agents by multiple tag_ids
- `findByOwner(ownerId)` - Find agents by owner
- `search(criteria)` - Advanced search with multiple criteria

**Status Management:**
- `activate(agentId)` - Set status to ACTIVE
- `deactivate(agentId)` - Set status to INACTIVE
- `archive(agentId)` - Set status to ARCHIVED

**Tag Management:**
- `addTags(agentId, tags)` - Add tag_ids to agent
- `removeTags(agentId, tags)` - Remove tag_ids from agent
- `getTags(agentId)` - Get all tag_ids for agent

### Tag Service

**Tag Registry Management:**
- `createTag(tagName, category, description)` - Create new tag, returns tag_id
- `getTagById(tagId)` - Get tag details by tag_id
- `getTagIdByName(tagName)` - Lookup tag_id by tag_name
- `getAllTags()` - Get all tags
- `getTagsByCategory(category)` - Get tags by category

**Tag Validation & Suggestions:**
- `validateTagIds(tagIds)` - Validate that tag_ids exist and are active
- `searchTags(query)` - Search tags by name or description
- `suggestTags(domain)` - Suggest relevant tags based on agent domain

**Tag Usage Tracking:**
- `incrementUsage(tagIds)` - Increment usage count for tags
- `decrementUsage(tagIds)` - Decrement usage count for tags
- `getTagUsageStats()` - Get tag usage statistics

---

## Example Queries

### Find agents by tag (capability - using tag_id)
```sql
-- Using tag_id directly
SELECT * FROM agents
WHERE tags @> '["tag-550e8400-e29b-41d4-a716-446655440001"]'::jsonb
  AND status = 'ACTIVE';

-- Using tag_name (requires join)
SELECT a.*
FROM agents a
JOIN agent_tags t ON a.tags @> jsonb_build_array(t.tag_id)
WHERE t.tag_name = 'customer-support'
  AND a.status = 'ACTIVE';
```

### Find agents by multiple tags (AND logic)
```sql
-- Using tag_ids
SELECT * FROM agents
WHERE tags @> '["tag-550e8400-e29b-41d4-a716-446655440001",
                "tag-550e8400-e29b-41d4-a716-446655440003",
                "tag-550e8400-e29b-41d4-a716-446655440005"]'::jsonb
  AND status = 'ACTIVE';
```

### Find agents by domain
```sql
SELECT * FROM agents
WHERE domain = 'banking'
  AND status = 'ACTIVE';
```

### Get all tags by category
```sql
SELECT tag_id, tag_name, display_name, usage_count
FROM agent_tags
WHERE category = 'CAPABILITY'
  AND is_active = true
ORDER BY usage_count DESC;
```

### Get tag usage statistics
```sql
SELECT
    category,
    COUNT(*) as total_tags,
    SUM(usage_count) as total_usage,
    AVG(usage_count) as avg_usage_per_tag
FROM agent_tags
WHERE is_active = true
GROUP BY category;
```

### Find agents using specific model provider
```sql
SELECT * FROM agents
WHERE model_config->>'provider' = 'anthropic'
  AND status = 'ACTIVE';
```

### Update agent metadata (no joins!)
```sql
-- Update custom metadata field
UPDATE agents
SET
    metadata = jsonb_set(
        metadata,
        '{priority}',
        '20'::jsonb
    ),
    updated_at = NOW()
WHERE agent_id = ?;

-- Update model configuration
UPDATE agents
SET
    model_config = jsonb_set(
        model_config,
        '{temperature}',
        '0.8'::jsonb
    ),
    updated_at = NOW()
WHERE agent_id = ?;
```

---

## Benefits

✅ **Simplicity** - 4 tables instead of 6+
✅ **Flexibility** - Add config without schema changes (metadata is key-value)
✅ **Performance** - Single query, no joins for agent data
✅ **Reusability** - Standardized tag registry shared across all agents
✅ **Discoverability** - Easy to find agents by tags, domain, or capabilities
✅ **Maintainability** - Easy to understand and evolve
✅ **Queryable** - JSONB supports indexing and complex queries
✅ **Governance** - Tag registry ensures consistency
✅ **Clear separation** - Model config separate from custom metadata

---

## Tag System Benefits

### 1. **Standardization**
All agents use the same tag vocabulary from the registry

### 2. **Consistency**
Tag validation ensures only approved tags are used

### 3. **Discoverability**
Easy to find agents by searching tags (supports AND/OR queries)

### 4. **Analytics**
Track which tags are most used, identify trends

### 5. **Suggestions**
Suggest relevant tags when creating new agents based on domain/type

### 6. **Performance**
GIN index on JSONB enables fast tag queries

### 7. **Flexibility**
JSONB array allows dynamic tag management without schema changes

---

## Example: Creating an Agent with Tags

**Workflow:**

1. **Get suggested tags based on domain**
   - Call `suggestTags("ecommerce")`
   - Returns AgentTag objects with tag_id and tag_name

2. **Convert tag names to tag_ids**
   - **Option A**: Use existing tags - lookup tag_id by tag_name
     - `getTagIdByName("customer-support")` → returns tag_id
   - **Option B**: Create new tag dynamically if it doesn't exist
     - Check if tag exists, if not create it and get new tag_id
     - `createTag("special-promotions", "CAPABILITY", "Handle special promotional campaigns")` → returns new tag_id

3. **Validate tag_ids**
   - Call `validateTagIds(selectedTagIds)` to ensure all tag_ids exist and are active

4. **Create agent with tag_ids**
```json
{
  "agent_id": "550e8400-e29b-41d4-a716-446655440000",
  "name": "ecommerce-support-bot",
  "description": "Handles customer support for e-commerce platform",
  "version": "1.0.0",
  "status": "ACTIVE",
  "domain": "ecommerce",
  "owner_id": "user-123",
  "owner_type": "USER",
  "tags": [
    "tag-550e8400-e29b-41d4-a716-446655440001",
    "tag-550e8400-e29b-41d4-a716-446655440002",
    "tag-550e8400-e29b-41d4-a716-446655440003",
    "tag-550e8400-e29b-41d4-a716-446655440007"
  ],
  "model_config": {
    "provider": "anthropic",
    "modelId": "claude-3-opus",
    "temperature": 0.7,
    "maxTokens": 4096
  },
  "metadata": {
    "priority": 10,
    "maxConcurrentRequests": 100,
    "requestTimeout": "PT30S",
    "greetingMessage": "Hello! How can I help you today?",
    "featureFlags": {
      "enableStreaming": true,
      "enableCaching": false
    }
  }
}
```

5. **Update tag usage counts**
   - Call `incrementUsage(selectedTagIds)` to track tag usage

---

## Summary

**Simplified Architecture:**
- **4 tables**: `agents`, `agent_tags`, `agent_policy_mappings`, `policy_bundles`
- **Profile** = Core identity (structured columns)
- **Tags** = Standardized, reusable capabilities/categorization (many-to-many via JSONB)
- **Policy Mapping** = One-to-Many relationship (one agent can have multiple policy bundles)
- **Policy** = Governance (separate table, properly governed)
- **ModelConfig** = Model configuration (JSONB - provider, modelId, temperature, etc.)
- **Metadata** = Flexible key-value customization (JSONB - any custom fields)

**Agent-Tag Relationship (Many-to-Many via JSONB):**
- Agents store tag IDs in JSONB array: `tags = ["tag-550e8400-...", "tag-660f9511-..."]`
- agent_tags.tag_id is primary key (UUID)
- agent_tags.tag_name is unique (for lookups)
- No junction table needed
- Validation at application layer
- Fast queries with GIN index

**Agent-Policy Relationship (One-to-Many via mapping table):**
- One agent can have multiple policy bundles
- `agent_policy_mappings` table links agents to policy bundles
- Supports versioning and active status tracking
- Each mapping tracks who applied the policy and when

**Key Improvements:**
- ✅ Removed OperationalMetrics (not needed in profile)
- ✅ Removed ToolConfig (use custom metadata instead)
- ✅ Removed parent_tag_id (unnecessary hierarchy)
- ✅ **Removed AgentType** (too rigid, use tags or metadata instead)
- ✅ **Renamed TagRegistry to AgentTag** (clearer naming)
- ✅ **tag_id is now primary key** (stable for dynamic creation, allows tag renaming)
- ✅ **tag_name is unique constraint** (human-readable lookups)
- ✅ **ModelConfig separated from metadata** (straightforward model configuration)
- ✅ **Metadata is now simple key-value** (flexible customization without structure)
- ✅ **Agent-Policy now One-to-Many** (via agent_policy_mappings table)
- ✅ Added robust Tag Registry for standardization
- ✅ Tags validated, categorized, and reusable across all agents
- ✅ Clear many-to-many relationship without junction table
- ✅ **Dynamic tag creation** - Auto-generated UUIDs work perfectly for on-the-fly tags
