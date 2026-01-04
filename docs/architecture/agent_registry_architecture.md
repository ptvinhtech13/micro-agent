# Agent Registry Architecture

## Overview

The **Agent Registry** is a central catalog system that manages multiple agent instances, their profiles, metadata, and configurations. It enables multi-agent orchestration, agent discovery, lifecycle management, and dynamic routing.

## Why Agent Registry?

### Use Cases

1. **Multi-Agent Systems**: Manage multiple specialized agents (e.g., Banking Agent, Healthcare Agent, General Agent)
2. **Agent Versioning**: Deploy multiple versions of agents simultaneously (A/B testing, gradual rollout)
3. **Dynamic Routing**: Route requests to the appropriate agent based on domain, capabilities, or load
4. **Configuration Management**: Centralize agent configurations, tools, and capabilities
5. **Lifecycle Management**: Enable/disable agents, monitor health, track usage
6. **Agent Discovery**: Allow agents to discover and collaborate with other agents

## Architecture

```
┌─────────────────────────────────────────────────────────────────────────┐
│                          AGENT REGISTRY SYSTEM                           │
│                                                                           │
│  ┌────────────────────────────────────────────────────────────────────┐ │
│  │                       Agent Registry                                │ │
│  │  ┌──────────────────────────────────────────────────────────────┐  │ │
│  │  │  Agent Catalog (Storage)                                     │  │ │
│  │  │  ┌────────────┐  ┌────────────┐  ┌────────────┐            │  │ │
│  │  │  │  Agent 1   │  │  Agent 2   │  │  Agent 3   │            │  │ │
│  │  │  │ (Banking)  │  │(Healthcare)│  │ (General)  │  ...       │  │ │
│  │  │  └────────────┘  └────────────┘  └────────────┘            │  │ │
│  │  └──────────────────────────────────────────────────────────────┘  │ │
│  │                                                                      │ │
│  │  ┌──────────────────────────────────────────────────────────────┐  │ │
│  │  │  Operations                                                   │  │ │
│  │  │  • register(AgentProfile)                                     │  │ │
│  │  │  • getAgent(agentId)                                          │  │ │
│  │  │  • findAgentsByDomain(domain)                                 │  │ │
│  │  │  • findAgentsByCapability(capability)                         │  │ │
│  │  │  • updateAgent(agentId, updates)                              │  │ │
│  │  │  • enableAgent(agentId) / disableAgent(agentId)               │  │ │
│  │  │  • listAgents(filters)                                        │  │ │
│  │  │  • selectAgent(request) → AgentProfile (routing)              │  │ │
│  │  └──────────────────────────────────────────────────────────────┘  │ │
│  └────────────────────────────────────────────────────────────────────┘ │
│                                                                           │
│  ┌────────────────────────────────────────────────────────────────────┐ │
│  │                       AgentProfile (Data Model)                     │ │
│  │  • agentId: String                                                  │ │
│  │  • name: String                                                     │ │
│  │  • description: String                                              │ │
│  │  • version: String                                                  │ │
│  │  • status: AgentStatus (ACTIVE, INACTIVE, MAINTENANCE)              │ │
│  │  • domain: String (banking, healthcare, general, etc.)              │ │
│  │  • capabilities: Set<Capability>                                    │ │
│  │  • configuration: AgentConfiguration                                │ │
│  │  • toolConfigurations: List<ToolConfiguration>                      │ │
│  │  • modelConfig: ModelConfiguration                                  │ │
│  │  • metadata: AgentMetadata                                          │ │
│  │  • createdAt: Instant                                               │ │
│  │  • updatedAt: Instant                                               │ │
│  └────────────────────────────────────────────────────────────────────┘ │
└─────────────────────────────────────────────────────────────────────────┘
```

## Data Models

### 1. AgentProfile

Complete agent definition with configuration, policies, and metadata.

```java
@Data
@Builder
public class AgentProfile {
    // Identity
    private String agentId;              // Unique identifier
    private String name;                 // Display name
    private String description;          // Agent description
    private String version;              // Version (e.g., "1.0.0")
    private AgentStatus status;          // ACTIVE, INACTIVE, MAINTENANCE, DEPRECATED

    // Classification
    private String domain;               // Primary domain (banking, healthcare, general)
    private Set<String> subDomains;      // Secondary domains
    private Set<Capability> capabilities; // What can this agent do?
    private AgentType type;              // GENERALIST, SPECIALIST, COORDINATOR

    // Configuration
    private AgentConfiguration configuration;
    private List<ToolConfiguration> toolConfigurations;
    private ModelConfiguration modelConfig;

    // POLICY GOVERNANCE ⭐ NEW
    private PolicyBundle policyBundle;   // Comprehensive policy control

    // Ownership & Version Control ⭐ NEW
    private OwnershipInfo ownershipInfo; // Ownership and access details
    private VersionHistory versionHistory; // Version tracking

    // Metadata
    private AgentMetadata metadata;

    // Lifecycle
    private Instant createdAt;
    private Instant updatedAt;
    private String createdBy;
    private String updatedBy;
}
```

### 2. AgentConfiguration

Agent behavior configuration.

```java
@Data
@Builder
public class AgentConfiguration {
    // Routing & Selection
    private Integer priority;            // Higher priority agents selected first
    private Float minConfidenceThreshold; // Minimum confidence to accept request
    private Set<String> allowedUserRoles; // Role-based access control

    // Resource Limits
    private Integer maxConcurrentRequests; // Load limit
    private Duration requestTimeout;      // Max processing time
    private Integer maxTokensPerRequest;  // Token budget

    // Memory Configuration
    private MemoryConfiguration memoryConfig;

    // Execution Configuration
    private ExecutionConfiguration executionConfig;

    // Feature Flags
    private Map<String, Boolean> featureFlags;

    // Custom Settings
    private Map<String, Object> customSettings;
}
```

### 3. Capability

What the agent can do.

```java
@Data
@Builder
public class Capability {
    private String capabilityId;
    private String name;
    private String description;
    private CapabilityType type;         // TASK, KNOWLEDGE_DOMAIN, TOOL_ACCESS
    private Set<String> requiredTools;   // Tools needed for this capability
    private Set<String> requiredPermissions;
    private Map<String, Object> parameters;
}
```

### 4. AgentMetadata

Operational and observability metadata.

```java
@Data
@Builder
public class AgentMetadata {
    // Operational Metrics
    private Long totalRequestsHandled;
    private Long successfulRequests;
    private Long failedRequests;
    private Float successRate;

    // Performance
    private Duration averageResponseTime;
    private Duration p95ResponseTime;
    private Duration p99ResponseTime;

    // Resource Usage
    private Long totalTokensUsed;
    private Float averageTokensPerRequest;

    // Health
    private AgentHealth health;          // HEALTHY, DEGRADED, UNHEALTHY
    private Instant lastHealthCheck;
    private String healthMessage;

    // Usage Tracking
    private Instant lastUsedAt;
    private Integer activeConversations;

    // Cost (if applicable)
    private MonetaryAmount estimatedCostPerRequest;
    private MonetaryAmount totalCost;

    // Tags & Labels
    private Set<String> tags;
    private Map<String, String> labels;
}
```

### 5. ToolConfiguration

Tool-specific configuration for this agent.

```java
@Data
@Builder
public class ToolConfiguration {
    private String toolId;
    private Boolean enabled;
    private Integer priority;            // Order of tool selection
    private Map<String, Object> defaultParameters;
    private Set<String> allowedOperations; // Restrict tool operations
    private Duration timeout;
    private Integer maxRetries;
}
```

### 6. ModelConfiguration

LLM model configuration.

```java
@Data
@Builder
public class ModelConfiguration {
    private String modelProvider;        // anthropic, openai, etc.
    private String modelId;              // claude-3-opus, gpt-4, etc.
    private Float temperature;
    private Integer maxTokens;
    private Float topP;
    private Integer topK;
    private List<String> stopSequences;
    private Map<String, Object> additionalParams;
}
```

### 7. PolicyBundle ⭐ NEW

Comprehensive policy governance for agent behavior control.

```java
@Data
@Builder
public class PolicyBundle {
    private String bundleId;
    private String bundleVersion;

    // Four Core Policy Types
    private AgentPurposePolicy purposePolicy;         // What CAN/CANNOT the agent do
    private AgentCorporatePolicy corporatePolicy;     // Corporate values & standards
    private AgentEthicalPolicy ethicalPolicy;         // Ethical principles & fairness
    private AgentRegulatoryPolicy regulatoryPolicy;   // Legal & compliance requirements

    // Policy Enforcement
    private PolicyEnforcementConfig enforcementConfig;

    // Metadata
    private Instant createdAt;
    private Instant updatedAt;
    private String createdBy;
    private String approvedBy;
    private Instant approvedAt;
}

// See agent_policy_governance.md for complete policy definitions:
// - AgentPurposePolicy: Allowed/prohibited operations, tools, domains
// - AgentCorporatePolicy: Brand guidelines, communication standards, corporate values
// - AgentEthicalPolicy: Fairness, transparency, user rights, harm prevention
// - AgentRegulatoryPolicy: GDPR/HIPAA/CCPA compliance, data privacy, security
```

### 8. OwnershipInfo ⭐ NEW

Ownership and access control information.

```java
@Data
@Builder
public class OwnershipInfo {
    // Ownership
    private String ownerId;                           // User/team who owns this agent
    private String ownerName;
    private String ownerEmail;
    private String ownerDepartment;

    // Team/Group
    private String teamId;
    private String teamName;
    private Set<String> teamMemberIds;

    // Access Control
    private AccessControlPolicy accessControlPolicy;

    // Security Roles
    private Set<SecurityRole> securityRoles;

    // Approval Chain
    private List<ApprovalRecord> approvalHistory;

    // Cost Center
    private String costCenter;                        // For billing/chargeback
    private String budgetCode;
}

@Data
@Builder
public class AccessControlPolicy {
    // Who can view this agent
    private Set<String> viewerRoles;
    private Set<String> viewerUserIds;

    // Who can use this agent
    private Set<String> userRoles;
    private Set<String> userUserIds;

    // Who can modify this agent
    private Set<String> editorRoles;
    private Set<String> editorUserIds;

    // Who can approve changes
    private Set<String> approverRoles;
    private Set<String> approverUserIds;

    // Public/Private
    private boolean isPublic;                         // Accessible to all users
    private boolean requiresApproval;                 // Requires approval to use
}

@Data
@Builder
public class SecurityRole {
    private String roleId;
    private String roleName;                          // "ADMIN", "OPERATOR", "VIEWER"
    private Set<Permission> permissions;
    private String description;
}

public enum Permission {
    VIEW_AGENT,
    USE_AGENT,
    EDIT_AGENT,
    DELETE_AGENT,
    MANAGE_POLICIES,
    VIEW_METRICS,
    EXPORT_DATA,
    MANAGE_ACCESS
}
```

### 9. VersionHistory ⭐ NEW

Complete version tracking and history.

```java
@Data
@Builder
public class VersionHistory {
    // Current version
    private String currentVersion;                    // Semantic version: "1.2.3"

    // Version records
    private List<VersionRecord> versions;

    // Rollback capability
    private boolean rollbackEnabled;
    private Integer maxVersionsToKeep;                // Retention policy
}

@Data
@Builder
public class VersionRecord {
    // Version info
    private String versionId;
    private String versionNumber;                     // "1.2.3"
    private VersionType versionType;                  // MAJOR, MINOR, PATCH

    // Change information
    private String changeDescription;
    private String changeReason;
    private ChangeCategory changeCategory;            // FEATURE, BUGFIX, SECURITY, POLICY

    // Change details
    private AgentProfileDiff diff;                    // What changed
    private Set<String> affectedComponents;           // Which parts changed

    // Lifecycle
    private VersionStatus status;                     // DRAFT, ACTIVE, DEPRECATED, ARCHIVED
    private Instant createdAt;
    private String createdBy;
    private Instant activatedAt;
    private String activatedBy;
    private Instant deprecatedAt;

    // Approval
    private ApprovalStatus approvalStatus;            // PENDING, APPROVED, REJECTED
    private List<ApprovalRecord> approvals;

    // Rollback info
    private boolean canRollback;
    private String previousVersionId;
}

public enum VersionType {
    MAJOR,      // Breaking changes (1.0.0 → 2.0.0)
    MINOR,      // New features, backward compatible (1.0.0 → 1.1.0)
    PATCH       // Bug fixes, patches (1.0.0 → 1.0.1)
}

public enum ChangeCategory {
    FEATURE,              // New capability added
    BUGFIX,               // Bug fix
    SECURITY,             // Security patch
    POLICY,               // Policy change
    PERFORMANCE,          // Performance improvement
    CONFIGURATION,        // Configuration change
    DEPRECATION           // Feature deprecation
}

@Data
@Builder
public class ApprovalRecord {
    private String approvalId;
    private String approverId;
    private String approverName;
    private String approverRole;
    private ApprovalDecision decision;                // APPROVED, REJECTED, PENDING
    private String comments;
    private Instant timestamp;
}

@Data
@Builder
public class AgentProfileDiff {
    // What changed between versions
    private Map<String, FieldChange> fieldChanges;

    // Policy changes
    private PolicyDiff policyDiff;

    // Configuration changes
    private ConfigurationDiff configurationDiff;

    // Tool changes
    private ToolConfigurationDiff toolConfigDiff;
}

@Data
@Builder
public class FieldChange {
    private String fieldName;
    private Object oldValue;
    private Object newValue;
    private ChangeType changeType;                    // ADDED, MODIFIED, REMOVED
}
```

## Agent Registry Interface

```java
/**
 * Agent Registry - Central catalog for managing agent profiles
 */
public interface AgentRegistry {

    /**
     * Register a new agent profile
     */
    AgentProfile register(AgentProfile profile);

    /**
     * Get agent profile by ID
     */
    Optional<AgentProfile> getAgent(String agentId);

    /**
     * Find agents by domain
     */
    List<AgentProfile> findAgentsByDomain(String domain);

    /**
     * Find agents by capability
     */
    List<AgentProfile> findAgentsByCapability(String capability);

    /**
     * Find agents matching criteria
     */
    List<AgentProfile> findAgents(AgentSearchCriteria criteria);

    /**
     * Update agent profile
     */
    AgentProfile updateAgent(String agentId, AgentProfileUpdate update);

    /**
     * Enable/disable agent
     */
    void enableAgent(String agentId);
    void disableAgent(String agentId);

    /**
     * Remove agent (soft delete)
     */
    void removeAgent(String agentId);

    /**
     * List all active agents
     */
    List<AgentProfile> listActiveAgents();

    /**
     * Select best agent for request (intelligent routing)
     */
    Optional<AgentProfile> selectAgent(AgentRequest request);

    /**
     * Health check - update agent metadata
     */
    void updateHealth(String agentId, AgentHealth health);

    /**
     * Record usage metrics
     */
    void recordMetrics(String agentId, AgentMetrics metrics);
}
```

## Agent Selection Strategy

### Routing Flow

```
AgentRequest
     │
     ▼
┌────────────────────┐
│ Agent Selector     │
│ (Part of Registry) │
└────────┬───────────┘
         │
         ▼
┌────────────────────────────────────────────────┐
│  Selection Criteria (in order):                │
│                                                 │
│  1. Domain Match                                │
│     • Exact domain match preferred             │
│     • Sub-domain match acceptable              │
│                                                 │
│  2. Capability Match                            │
│     • Agent has required capabilities          │
│     • Tool availability                        │
│                                                 │
│  3. Agent Status                                │
│     • ACTIVE agents only                       │
│     • Health = HEALTHY or DEGRADED             │
│                                                 │
│  4. Load & Availability                         │
│     • Not exceeding maxConcurrentRequests      │
│     • Response time within acceptable range    │
│                                                 │
│  5. Access Control                              │
│     • User role matches allowedUserRoles       │
│     • User has required permissions            │
│                                                 │
│  6. Scoring & Ranking                           │
│     • Calculate score for each candidate       │
│     • Sort by priority, success rate, latency  │
│                                                 │
│  7. Select Top Agent                            │
│     • Return highest-scoring agent             │
│     • Fallback to general agent if no match    │
└────────────────────────────────────────────────┘
         │
         ▼
   Selected AgentProfile
```

### Selection Algorithm

```java
public Optional<AgentProfile> selectAgent(AgentRequest request) {
    // 1. Extract criteria from request
    String domain = extractDomain(request);
    Set<String> requiredCapabilities = extractCapabilities(request);
    String userRole = extractUserRole(request);

    // 2. Find candidates
    List<AgentProfile> candidates = findAgents(
        AgentSearchCriteria.builder()
            .domain(domain)
            .capabilities(requiredCapabilities)
            .status(AgentStatus.ACTIVE)
            .build()
    );

    // 3. Filter by access control
    candidates = candidates.stream()
        .filter(agent -> hasAccess(agent, userRole))
        .collect(Collectors.toList());

    // 4. Filter by availability
    candidates = candidates.stream()
        .filter(agent -> isAvailable(agent))
        .collect(Collectors.toList());

    // 5. Score and rank
    List<ScoredAgent> scored = candidates.stream()
        .map(agent -> new ScoredAgent(agent, calculateScore(agent, request)))
        .sorted(Comparator.comparing(ScoredAgent::getScore).reversed())
        .collect(Collectors.toList());

    // 6. Return top agent
    return scored.isEmpty()
        ? Optional.empty()
        : Optional.of(scored.get(0).getAgent());
}

private Float calculateScore(AgentProfile agent, AgentRequest request) {
    float score = 0.0f;

    // Domain match (40%)
    score += domainMatchScore(agent, request) * 0.4f;

    // Capability match (30%)
    score += capabilityMatchScore(agent, request) * 0.3f;

    // Performance (20%)
    score += performanceScore(agent) * 0.2f;

    // Priority (10%)
    score += priorityScore(agent) * 0.1f;

    return score;
}
```

## Integration with Agent Brain

### Updated Flow

```
1. User Request → Communication Layer
2. AgentRegistry.selectAgent(request) → AgentProfile
3. AgentBrainFactory.create(agentProfile) → AgentBrain
4. AgentBrain.process(request) → AgentResponse
5. AgentRegistry.recordMetrics(agentId, metrics)
6. Return response to user
```

### Example Usage

```java
@Service
@RequiredArgsConstructor
public class AgentOrchestrator {

    private final AgentRegistry agentRegistry;
    private final AgentBrainFactory agentBrainFactory;

    public AgentResponse handleRequest(AgentRequest request) {
        // 1. Select appropriate agent
        AgentProfile agentProfile = agentRegistry.selectAgent(request)
            .orElseThrow(() -> new NoAgentAvailableException());

        // 2. Create agent brain with configuration
        AgentBrain brain = agentBrainFactory.create(agentProfile);

        // 3. Process request
        Instant startTime = Instant.now();
        AgentResponse response = brain.process(request);
        Duration duration = Duration.between(startTime, Instant.now());

        // 4. Record metrics
        agentRegistry.recordMetrics(agentProfile.getAgentId(),
            AgentMetrics.builder()
                .requestCount(1L)
                .successCount(response.getMetadata().getSuccess() ? 1L : 0L)
                .responseTime(duration)
                .tokensUsed(response.getMetadata().getTokenUsage().getTotalTokens())
                .build()
        );

        return response;
    }
}
```

## Storage Considerations

### Database Schema

```sql
-- Agent Profiles Table
CREATE TABLE agent_profiles (
    agent_id VARCHAR(255) PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    description TEXT,
    version VARCHAR(50),
    status VARCHAR(50) NOT NULL,
    domain VARCHAR(100),
    type VARCHAR(50),
    configuration JSONB,
    model_config JSONB,
    metadata JSONB,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL,
    created_by VARCHAR(255),
    updated_by VARCHAR(255)
);

-- Agent Capabilities Table
CREATE TABLE agent_capabilities (
    id BIGSERIAL PRIMARY KEY,
    agent_id VARCHAR(255) REFERENCES agent_profiles(agent_id),
    capability_id VARCHAR(255),
    name VARCHAR(255),
    type VARCHAR(50),
    parameters JSONB,
    created_at TIMESTAMP NOT NULL
);

-- Agent Tool Configurations Table
CREATE TABLE agent_tool_configurations (
    id BIGSERIAL PRIMARY KEY,
    agent_id VARCHAR(255) REFERENCES agent_profiles(agent_id),
    tool_id VARCHAR(255),
    enabled BOOLEAN DEFAULT true,
    priority INTEGER,
    config JSONB,
    created_at TIMESTAMP NOT NULL
);

-- Indexes
CREATE INDEX idx_agent_domain ON agent_profiles(domain);
CREATE INDEX idx_agent_status ON agent_profiles(status);
CREATE INDEX idx_agent_type ON agent_profiles(type);
CREATE INDEX idx_capability_agent ON agent_capabilities(agent_id);
CREATE INDEX idx_tool_config_agent ON agent_tool_configurations(agent_id);
```

## Benefits

1. **Centralized Management**: Single source of truth for all agent configurations
2. **Dynamic Routing**: Intelligent request routing based on capabilities and load
3. **Multi-Tenancy**: Support multiple agents with different configurations
4. **Versioning**: Deploy and manage multiple agent versions
5. **Observability**: Track metrics and health per agent
6. **Scalability**: Add/remove agents without code changes
7. **Flexibility**: Configure agents without redeployment

## Future Enhancements

1. **Agent Collaboration**: Agents can discover and delegate to other agents
2. **Auto-Scaling**: Automatically spin up/down agent instances based on load
3. **A/B Testing**: Route percentage of traffic to different agent versions
4. **Circuit Breaker**: Automatically disable unhealthy agents
5. **Cost Optimization**: Route to cheaper agents when possible
6. **Learning**: Improve selection based on historical performance
