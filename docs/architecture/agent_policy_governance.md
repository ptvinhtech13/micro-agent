# Agent Policy Governance System

## Overview

The **Agent Policy Governance System** provides a unified, flexible framework for controlling agent behavior through user-defined policies. This system ensures agents operate within defined boundaries, comply with regulations, and align with organizational values.

## Design Philosophy

**Simplicity**: One unified Policy model instead of multiple complex policy types
**Flexibility**: Users define policy content in natural language
**Tag-Based Organization**: User-created tags for flexible categorization
**System-Managed Lifecycle**: System governs policy status and transitions
**Enforcement-Focused**: Clear enforcement mechanisms at every stage of agent execution

## Policy Enforcement Flow in Agent

```
┌─────────────────────────────────────────────────────────────────────────┐
│                    AGENT REQUEST PROCESSING WITH POLICIES                │
│                                                                           │
│  User Request                                                             │
│       │                                                                   │
│       ▼                                                                   │
│  ┌────────────────────────────────────────────────────────────────────┐ │
│  │  PHASE 1: PRE-REQUEST VALIDATION                                   │ │
│  │  ────────────────────────────────                                   │ │
│  │                                                                      │ │
│  │  1. Load Active Policies for Agent                                  │ │
│  │     PolicyRepository.getActivePoliciesForAgent(agentId)             │ │
│  │     → Returns: List<Policy>                                         │ │
│  │                                                                      │ │
│  │  2. Validate Request Against Policies                               │ │
│  │     For each policy with PRE_REQUEST enforcement:                   │ │
│  │       - Check if request violates policy rules                      │ │
│  │       - Validate input content                                      │ │
│  │       - Check prohibited operations                                 │ │
│  │                                                                      │ │
│  │  3. Decision:                                                        │ │
│  │     ✅ PASS → Continue to Phase 2                                   │ │
│  │     ❌ BLOCK → Return error with policy violation message           │ │
│  │     ⚠️  WARN → Log warning, continue                                │ │
│  └────────────────────────────────────────────────────────────────────┘ │
│       │                                                                   │
│       ▼                                                                   │
│  ┌────────────────────────────────────────────────────────────────────┐ │
│  │  PHASE 2: POLICY-ENHANCED PROMPT INJECTION                          │ │
│  │  ──────────────────────────────────────                             │ │
│  │                                                                      │ │
│  │  1. Build System Prompt with Policy Guidance                        │ │
│  │                                                                      │ │
│  │     System Prompt = Base Agent Prompt                               │ │
│  │                   + Policy Prompts (from all active policies)       │ │
│  │                                                                      │ │
│  │     For each policy with DURING_GENERATION enforcement:             │ │
│  │       Append: policy.content.llmPrompt                              │ │
│  │                                                                      │ │
│  │     Example Combined Prompt:                                        │ │
│  │     ┌──────────────────────────────────────────────────┐           │ │
│  │     │ You are a helpful banking assistant.             │           │ │
│  │     │                                                   │           │ │
│  │     │ [POLICY: No PII Storage]                         │           │ │
│  │     │ You must never store, log, or transmit PII...    │           │ │
│  │     │                                                   │           │ │
│  │     │ [POLICY: Professional Communication]             │           │ │
│  │     │ Always use professional tone. Keep responses...  │           │ │
│  │     │                                                   │           │ │
│  │     │ [POLICY: Banking Operations Only]                │           │ │
│  │     │ You can only check balances and search...        │           │ │
│  │     └──────────────────────────────────────────────────┘           │ │
│  │                                                                      │ │
│  │  2. Send to LLM with Policy-Enhanced Prompt                         │ │
│  │     LLM generates response guided by policies                       │ │
│  └────────────────────────────────────────────────────────────────────┘ │
│       │                                                                   │
│       ▼                                                                   │
│  ┌────────────────────────────────────────────────────────────────────┐ │
│  │  PHASE 3: POST-RESPONSE VALIDATION & ENFORCEMENT                    │ │
│  │  ────────────────────────────────────────────────                   │ │
│  │                                                                      │ │
│  │  1. Validate LLM Response Against Policies                          │ │
│  │     For each policy with POST_RESPONSE enforcement:                 │ │
│  │       - Scan response for policy violations                         │ │
│  │       - Check prohibited content                                    │ │
│  │       - Detect PII patterns (if privacy policy exists)              │ │
│  │       - Validate tone/brand guidelines                              │ │
│  │                                                                      │ │
│  │  2. Apply Remediation Based on Violation Action                     │ │
│  │                                                                      │ │
│  │     BLOCK:                                                           │ │
│  │       → Discard violating response                                   │ │
│  │       → Return safe fallback message to user                         │ │
│  │       → Example: "I cannot provide that information due to           │ │
│  │          policy restrictions. How else can I help you?"              │ │
│  │                                                                      │ │
│  │     REDACT:                                                          │ │
│  │       → Remove violating content (e.g., PII) from response           │ │
│  │       → Return sanitized response to user                            │ │
│  │       → Example: "Your account at [REDACTED] is active"              │ │
│  │                                                                      │ │
│  │     WARN:                                                            │ │
│  │       → Log violation for manual review                              │ │
│  │       → Return original response (non-blocking)                      │ │
│  │                                                                      │ │
│  │  3. Record Audit Trail                                              │ │
│  │     - Log policy checks performed                                   │ │
│  │     - Record any violations                                         │ │
│  │     - Track enforcement actions taken                               │ │
│  └────────────────────────────────────────────────────────────────────┘ │
│       │                                                                   │
│       ▼                                                                   │
│  Return Final Response to User                                            │
└─────────────────────────────────────────────────────────────────────────┘
```

## Core Data Models

### 1. Policy (Unified Model)

The single, flexible policy model for all governance requirements.

```java
@Data
@Builder
public class Policy {
    // Identity
    private String policyId;                      // Unique identifier
    private String name;                          // Policy name
    private String description;                   // Brief description

    // Content (User-Defined)
    private PolicyContent content;                // Policy rules and guidance

    // Categorization (User-Defined Tags)
    private Set<String> tags;                     // Tags: ["privacy", "brand", "ethics"]

    // Status (System-Managed)
    private PolicyStatus status;                  // Lifecycle state

    // Enforcement Configuration
    private EnforcementConfig enforcement;        // How to enforce this policy

    // Metadata
    private PolicyMetadata metadata;              // Ownership, versioning, audit

    // Lifecycle timestamps
    private Instant createdAt;
    private String createdBy;
    private Instant updatedAt;
    private String updatedBy;
    private Instant activatedAt;
    private String activatedBy;
}
```

### 2. PolicyContent (User-Defined)

All policy content is defined by users in natural language.

```java
@Data
@Builder
public class PolicyContent {
    // Primary Policy Statement (What is the policy?)
    private String policyStatement;
    // Example: "This agent must never store, log, or transmit personally
    //           identifiable information (PII) including names, emails,
    //           phone numbers, SSN, or credit card numbers."

    // LLM Guidance (How to tell the LLM?)
    // Include examples directly in this prompt
    private String llmPrompt;
    // Example: "You must not store or transmit any PII. If a user shares
    //           sensitive information like email or phone number, politely
    //           remind them not to share it and do not include it in your
    //           response or save it to memory.
    //
    //           Example - CORRECT behavior:
    //           User: 'My email is john@example.com'
    //           You: 'For your privacy, please don't share personal info. How else can I help?'
    //
    //           Example - WRONG behavior:
    //           User: 'Call me at 555-1234'
    //           You: 'I've saved your number' ❌ NEVER DO THIS"

    // Detailed Rules (Optional granular rules)
    private List<PolicyRule> rules;

    // Additional flexible data
    private Map<String, Object> additionalDetails;
}
```

### 3. PolicyRule (Granular Enforcement Rules)

Optional specific rules for automated enforcement.

**When to use rules:**
- **PRE_REQUEST**: Check user input before processing (e.g., detect PII in user message)
- **POST_RESPONSE**: Check agent output after generation (e.g., ensure no PII in response)
- **Both**: Apply rule to both input and output (e.g., PII detection everywhere)

**Note:** Rules are for automated pattern matching (regex, keywords). The `llmPrompt` guides the LLM's behavior during generation (DURING_GENERATION phase).

```java
@Data
@Builder
public class PolicyRule {
    private String ruleId;
    private String ruleName;                      // e.g., "Detect Email Addresses"
    private String ruleDescription;               // What this rule does

    // When to apply this rule
    private Set<EnforcementPhase> applicablePhases; // PRE_REQUEST, POST_RESPONSE, or both

    // Detection Patterns (for automated scanning)
    private List<String> detectionPatterns;       // Regex patterns
    private List<String> keywords;                // Keywords to detect

    // What to do when violation detected
    private EnforcementAction enforcementAction;   // BLOCK, REDACT, WARN
}

public enum EnforcementAction {
    BLOCK,       // Block request/response completely - return safe fallback
    REDACT,      // Remove violating content, return sanitized response
    WARN         // Log violation, return original response
}
```

**How enforcement works:**
- Rule defines WHAT to do: `enforcementAction` (BLOCK, REDACT, WARN)
- The enforcement action is always applied as defined
- **All violations are automatically logged to the audit trail regardless of action**

### POST_RESPONSE Remediation Strategy

When a policy violation is detected in the agent's response (POST_RESPONSE phase), here's what happens for each action:

#### BLOCK
```java
// Agent generated response contains violation
AgentResponse violatingResponse = "Your SSN is 123-45-6789";

// POST_RESPONSE detects violation → BLOCK
// Result returned to user:
AgentResponse blocked = AgentResponse.builder()
    .content("I cannot provide that information due to policy restrictions. How else can I help you?")
    .blocked(true)
    .violation(violationInfo)
    .build();
```

#### REDACT
```java
// Agent generated response contains PII
AgentResponse original = "Your email john@example.com is verified";

// POST_RESPONSE detects email → REDACT
// Result returned to user:
AgentResponse redacted = AgentResponse.builder()
    .content("Your email [REDACTED] is verified")
    .redacted(true)
    .redactedContent(List.of("john@example.com"))
    .build();
```

#### WARN
```java
// Agent response has minor issue (logged but allowed)
AgentResponse original = "... response content ...";

// POST_RESPONSE detects minor violation → WARN
// Result: Original response returned, violation logged for review
```

### Enforcement Actions Summary

All enforcement actions are applied as defined in the policy rules:

| Rule Action | Behavior | Result |
|-------------|----------|--------|
| **BLOCK**   | Block request/response | User receives error message, violation logged to audit trail |
| **REDACT**  | Remove violating content | User receives sanitized response, violation logged to audit trail |
| **WARN**    | Log warning | User receives original response, violation logged to audit trail |

**All violations are automatically logged to the audit trail for compliance and monitoring.**

### 4. EnforcementConfig (Enforcement Behavior)

Defines when to enforce the policy.

```java
@Data
@Builder
public class EnforcementConfig {
    // When to Enforce (can be multiple phases)
    private Set<EnforcementPhase> phases;         // PRE_REQUEST, DURING_GENERATION, POST_RESPONSE
}

public enum EnforcementPhase {
    PRE_REQUEST,         // Before processing (validate input)
    DURING_GENERATION,   // During LLM generation (inject into prompt)
    POST_RESPONSE        // After generation (validate output)
}
```

**How enforcement works:**
- Policies are always actively enforced based on their defined rules
- BLOCK → blocks request/response completely
- REDACT → removes violating content from response
- WARN → logs warning and allows response
- **All violations are automatically logged to the audit trail**

### 5. PolicyStatus (System-Managed Lifecycle)

System-managed policy lifecycle states.

```java
public enum PolicyStatus {
    DRAFT,           // Being created/edited, not enforced
    REVIEW,          // Submitted for review
    APPROVED,        // Approved, not yet active
    ACTIVE,          // Currently enforced on agents
    INACTIVE,        // Temporarily disabled
    DEPRECATED,      // Replaced by newer version
    ARCHIVED         // Historical record
}
```

### 6. Tag (User-Defined Categories)

Users create tags to organize and categorize policies.

```java
@Data
@Builder
public class Tag {
    private String tagId;
    private String tagName;                       // e.g., "privacy", "brand"
    private String description;                   // What this tag means
    private TagCategory category;                 // Optional grouping
    private String createdBy;
    private Instant createdAt;

    // UI Customization (optional)
    private String iconName;
    private String colorCode;
}

public enum TagCategory {
    GOVERNANCE,      // Privacy, security, compliance
    OPERATIONAL,     // Capability, restriction, access
    BEHAVIORAL,      // Communication, ethics, brand
    CUSTOM           // User-defined
}
```

### 7. PolicyMetadata (Ownership & Versioning)

Simple metadata for policy ownership and versioning.

```java
@Data
@Builder
public class PolicyMetadata {
    // Ownership
    private String ownerId;
    private String ownerName;
    private String department;

    // Versioning
    private String version;                       // Semantic: "1.2.0"
    private String previousVersionId;

    // Custom metadata (flexible)
    private Map<String, Object> customMetadata;
}
```

## Policy Application in Agent

### AgentProfile Integration

How agents reference and use policies:

```java
@Data
@Builder
public class AgentProfile {
    private String agentId;
    private String name;
    // ... other agent fields ...

    // Policy References (Simple list of policy IDs)
    private Set<String> policyIds;

    // Policy Loading Strategy
    private PolicyLoadingStrategy policyLoadingStrategy;

    // ... other fields ...
}

public enum PolicyLoadingStrategy {
    EAGER,       // Load all policies at agent initialization
    LAZY,        // Load policies on first use
    ON_DEMAND    // Load policies per request (most flexible)
}
```

### Policy Application Example in Agent Execution

```java
@Service
@RequiredArgsConstructor
public class AgentExecutionService {

    private final PolicyRepository policyRepository;
    private final PolicyEnforcementEngine policyEngine;
    private final AgentBrain agentBrain;

    public AgentResponse executeRequest(AgentRequest request, AgentProfile agent) {

        // PHASE 1: PRE-REQUEST VALIDATION
        // ────────────────────────────────
        List<Policy> policies = policyRepository.getActivePoliciesForAgent(agent.getAgentId());

        PolicyValidationResult preValidation =
            policyEngine.validateRequest(request, policies);

        if (!preValidation.isValid()) {
            // Violation detected - handle based on policy enforcement action
            return handlePreRequestViolation(preValidation);
        }

        // PHASE 2: POLICY-ENHANCED PROMPT INJECTION
        // ──────────────────────────────────────────
        String basePrompt = agent.getSystemPrompt();
        String enhancedPrompt = policyEngine.buildPolicyEnhancedPrompt(
            basePrompt,
            policies
        );

        // Execute with enhanced prompt
        AgentResponse response = agentBrain.process(
            request,
            enhancedPrompt
        );

        // PHASE 3: POST-RESPONSE VALIDATION
        // ──────────────────────────────────
        PolicyComplianceResult postValidation =
            policyEngine.validateResponse(response, policies);

        if (!postValidation.isCompliant()) {
            // Apply remediation
            response = policyEngine.applyRemediation(response, postValidation);

            // Record violation
            policyEngine.recordViolation(
                createViolationRecord(agent, postValidation)
            );
        }

        return response;
    }

    private AgentResponse handlePreRequestViolation(PolicyValidationResult validation) {
        // Always enforce violations - return blocked response
        return AgentResponse.builder()
            .content(validation.getUserMessage())
            .blocked(true)
            .violation(validation)
            .build();
    }
}
```

## Policy Enforcement Engine

The engine that applies policies throughout agent execution.

```java
/**
 * Policy Enforcement Engine
 * Applies policies at all stages of agent execution
 */
public interface PolicyEnforcementEngine {

    /**
     * PHASE 1: Validate request against policies (PRE_REQUEST)
     * Checks if the incoming request violates any policies
     */
    PolicyValidationResult validateRequest(
        AgentRequest request,
        List<Policy> policies
    );

    /**
     * PHASE 2: Build policy-enhanced system prompt (DURING_GENERATION)
     * Injects all policy LLM prompts into the system prompt
     */
    String buildPolicyEnhancedPrompt(
        String basePrompt,
        List<Policy> policies
    );

    /**
     * PHASE 3: Validate response against policies (POST_RESPONSE)
     * Scans response for policy violations
     */
    PolicyComplianceResult validateResponse(
        AgentResponse response,
        List<Policy> policies
    );

    /**
     * Apply remediation to policy violations
     * Handles BLOCK, REDACT, WARN actions based on rule definitions
     * All violations are automatically logged to audit trail
     */
    AgentResponse applyRemediation(
        AgentResponse response,
        PolicyComplianceResult complianceResult
    );

    /**
     * Record policy violation for audit
     */
    void recordViolation(PolicyViolation violation);

    /**
     * Get compliance report for agent
     */
    ComplianceReport getComplianceReport(
        String agentId,
        Instant from,
        Instant to
    );
}

// Result objects

@Data
@Builder
public class PolicyValidationResult {
    private boolean isValid;
    private Policy violatedPolicy;                // Which policy was violated
    private String violationReason;               // Why it was violated
    private String userMessage;                   // Message to show user
    private ViolationSeverity severity;
}

@Data
@Builder
public class PolicyComplianceResult {
    private boolean isCompliant;
    private List<PolicyViolation> violations;     // All violations found
    private ViolationSeverity highestSeverity;
}

@Data
@Builder
public class PolicyViolation {
    private String violationId;
    private String policyId;
    private String policyName;
    private String agentId;
    private String violationType;                 // What was violated
    private String violationDescription;
    private ViolationSeverity severity;
    private EnforcementAction actionTaken;        // What was done
    private Instant timestamp;
    private Map<String, Object> context;          // Additional context
}

public enum ViolationSeverity {
    CRITICAL,    // Severe violation, must block
    HIGH,        // Significant violation
    MEDIUM,      // Moderate violation
    LOW,         // Minor violation
    INFO         // Informational only
}
```

## Policy Repository

Service for managing policies.

```java
/**
 * Policy Repository
 * Central storage and management for policies
 */
public interface PolicyRepository {

    // CRUD Operations
    Policy createPolicy(Policy policy);
    Optional<Policy> getPolicy(String policyId);
    Policy updatePolicy(String policyId, PolicyUpdate update);
    void deletePolicy(String policyId);

    // Query Operations
    List<Policy> findPoliciesByTag(String tag);
    List<Policy> findPoliciesByTags(Set<String> tags);
    List<Policy> findActivePolicies();
    List<Policy> searchPolicies(PolicySearchCriteria criteria);

    // Agent-Specific Queries (for enforcement)
    List<Policy> getActivePoliciesForAgent(String agentId);
    List<Policy> getPoliciesByPhase(String agentId, EnforcementPhase phase);

    // Status Management
    void changeStatus(String policyId, PolicyStatus newStatus, String reason);
    void activatePolicy(String policyId);
    void deactivatePolicy(String policyId);

    // Versioning
    Policy createVersion(String policyId, PolicyUpdate update);
    List<Policy> getPolicyHistory(String policyId);
}
```

## Tag Management

Service for managing user-defined tags.

```java
/**
 * Tag Manager
 * User-defined categorization system
 */
public interface TagManager {

    // Tag CRUD
    Tag createTag(Tag tag);
    Optional<Tag> getTag(String tagName);
    Tag updateTag(String tagId, TagUpdate update);
    void deleteTag(String tagId);

    // Query Operations
    List<Tag> listAllTags();
    List<Tag> listTagsByCategory(TagCategory category);
    List<Policy> getPoliciesWithTag(String tagName);

    // Tag Assignment
    void addTagToPolicy(String policyId, String tagName);
    void removeTagFromPolicy(String policyId, String tagName);
}
```

## Complete Example: Privacy Policy

```java
// 1. Create a comprehensive privacy policy
Policy privacyPolicy = Policy.builder()
    .policyId(UUID.randomUUID().toString())
    .name("No PII Storage and Transmission")
    .description("Prevent storage, logging, and transmission of personally identifiable information")
    .content(PolicyContent.builder()
        .policyStatement(
            "This agent must never store, log, or transmit personally identifiable " +
            "information (PII) including: names, email addresses, phone numbers, " +
            "social security numbers, addresses, credit card numbers, or any other " +
            "sensitive personal data."
        )
        .llmPrompt(
            "CRITICAL POLICY: You must NEVER store, log, or transmit any personally " +
            "identifiable information (PII).\n\n" +
            "If a user shares PII such as:\n" +
            "- Email address\n" +
            "- Phone number\n" +
            "- Home address\n" +
            "- Credit card number\n" +
            "- Social security number\n\n" +
            "You MUST:\n" +
            "1. Politely ask them not to share sensitive information\n" +
            "2. NOT include the PII in your response\n" +
            "3. NOT save it to memory\n" +
            "4. Suggest alternative ways to help without PII\n\n" +
            "EXAMPLES:\n\n" +
            "✅ CORRECT Behavior:\n" +
            "User: 'My email is john@example.com'\n" +
            "You: 'For your privacy, please don't share personal information like email addresses. How else can I help you?'\n\n" +
            "❌ WRONG Behavior (NEVER DO THIS):\n" +
            "User: 'Call me at 555-1234'\n" +
            "You: 'I've saved your number 555-1234' ← VIOLATION! Never store or confirm PII"
        )
        .rules(List.of(
            PolicyRule.builder()
                .ruleId("detect-email")
                .ruleName("Detect and Redact Email Addresses")
                .ruleDescription("Detect email patterns in input and output")
                .applicablePhases(Set.of(
                    EnforcementPhase.PRE_REQUEST,    // Check user input
                    EnforcementPhase.POST_RESPONSE   // Check agent response
                ))
                .detectionPatterns(List.of(
                    "[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}"
                ))
                .enforcementAction(EnforcementAction.REDACT)
                .build(),
            PolicyRule.builder()
                .ruleId("detect-phone")
                .ruleName("Detect and Redact Phone Numbers")
                .ruleDescription("Detect phone number patterns")
                .applicablePhases(Set.of(
                    EnforcementPhase.PRE_REQUEST,    // Check user input
                    EnforcementPhase.POST_RESPONSE   // Check agent response
                ))
                .detectionPatterns(List.of(
                    "\\b\\d{3}[-.]?\\d{3}[-.]?\\d{4}\\b",
                    "\\(\\d{3}\\)\\s?\\d{3}[-.]?\\d{4}"
                ))
                .enforcementAction(EnforcementAction.REDACT)
                .build(),
            PolicyRule.builder()
                .ruleId("detect-ssn")
                .ruleName("Detect and Block SSN")
                .ruleDescription("Detect social security numbers in user input")
                .applicablePhases(Set.of(
                    EnforcementPhase.PRE_REQUEST     // Only check user input
                ))
                .detectionPatterns(List.of(
                    "\\b\\d{3}-\\d{2}-\\d{4}\\b"
                ))
                .enforcementAction(EnforcementAction.BLOCK)
                .build()
        ))
        .build())
    .tags(Set.of("privacy", "security", "regulatory", "gdpr", "pii"))
    .status(PolicyStatus.ACTIVE)
    .enforcement(EnforcementConfig.builder()
        .phases(Set.of(
            EnforcementPhase.PRE_REQUEST,
            EnforcementPhase.DURING_GENERATION,
            EnforcementPhase.POST_RESPONSE
        ))
        .build())
    .metadata(PolicyMetadata.builder()
        .ownerId("security-team")
        .ownerName("Security Team")
        .department("Information Security")
        .version("1.0.0")
        .build())
    .createdBy("security-admin")
    .createdAt(Instant.now())
    .build();

// 2. Save policy
policyRepository.createPolicy(privacyPolicy);

// 3. Apply to agent
agent.setPolicyIds(Set.of(privacyPolicy.getPolicyId()));

// 4. When agent executes, policies are automatically enforced
```

## Database Schema

```sql
-- Policies Table
CREATE TABLE policies (
    policy_id VARCHAR(255) PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    description TEXT,
    content JSONB NOT NULL,              -- PolicyContent as JSON
    status VARCHAR(50) NOT NULL,         -- PolicyStatus enum
    enforcement JSONB,                   -- EnforcementConfig as JSON
    metadata JSONB,                      -- PolicyMetadata as JSON
    created_at TIMESTAMP NOT NULL,
    created_by VARCHAR(255),
    updated_at TIMESTAMP,
    updated_by VARCHAR(255),
    activated_at TIMESTAMP,
    activated_by VARCHAR(255),
    INDEX idx_status (status),
    INDEX idx_created_by (created_by),
    INDEX idx_activated_at (activated_at)
);

-- Tags Table
CREATE TABLE tags (
    tag_id VARCHAR(255) PRIMARY KEY,
    tag_name VARCHAR(100) NOT NULL UNIQUE,
    description TEXT,
    category VARCHAR(50),                -- TagCategory enum
    icon_name VARCHAR(50),
    color_code VARCHAR(20),
    created_by VARCHAR(255),
    created_at TIMESTAMP NOT NULL,
    INDEX idx_category (category),
    INDEX idx_tag_name (tag_name)
);

-- Policy-Tag Mapping (Many-to-Many)
CREATE TABLE policy_tags (
    policy_id VARCHAR(255) REFERENCES policies(policy_id) ON DELETE CASCADE,
    tag_name VARCHAR(100) REFERENCES tags(tag_name) ON DELETE CASCADE,
    created_at TIMESTAMP NOT NULL,
    PRIMARY KEY (policy_id, tag_name),
    INDEX idx_policy_id (policy_id),
    INDEX idx_tag_name (tag_name)
);

-- Agent-Policy Mapping (Many-to-Many)
CREATE TABLE agent_policies (
    agent_id VARCHAR(255) NOT NULL,
    policy_id VARCHAR(255) REFERENCES policies(policy_id) ON DELETE CASCADE,
    assigned_at TIMESTAMP NOT NULL,
    assigned_by VARCHAR(255),
    PRIMARY KEY (agent_id, policy_id),
    INDEX idx_agent_id (agent_id),
    INDEX idx_policy_id (policy_id)
);

-- Policy Violations (Audit Trail)
CREATE TABLE policy_violations (
    violation_id VARCHAR(255) PRIMARY KEY,
    policy_id VARCHAR(255) REFERENCES policies(policy_id),
    policy_name VARCHAR(255),
    agent_id VARCHAR(255),
    violation_type VARCHAR(100),
    violation_description TEXT,
    severity VARCHAR(50),                -- ViolationSeverity enum
    action_taken VARCHAR(50),            -- EnforcementAction enum
    context JSONB,                       -- Additional context
    timestamp TIMESTAMP NOT NULL,
    INDEX idx_policy_id (policy_id),
    INDEX idx_agent_id (agent_id),
    INDEX idx_timestamp (timestamp),
    INDEX idx_severity (severity)
);

-- Policy Audit Log
CREATE TABLE policy_audit_log (
    event_id VARCHAR(255) PRIMARY KEY,
    policy_id VARCHAR(255) REFERENCES policies(policy_id),
    event_type VARCHAR(50) NOT NULL,     -- CREATED, UPDATED, ACTIVATED, etc.
    performed_by VARCHAR(255),
    event_details JSONB,
    timestamp TIMESTAMP NOT NULL,
    INDEX idx_policy_id (policy_id),
    INDEX idx_event_type (event_type),
    INDEX idx_timestamp (timestamp)
);
```

## Benefits

1. **Unified Model**: One simple Policy model instead of four complex types
2. **User Control**: Users define policies in natural language, not code
3. **Flexible Categorization**: Tag-based organization adapts to any need
4. **Clear Enforcement**: Three-phase enforcement (pre/during/post) with clear actions
5. **System-Managed Lifecycle**: System handles status transitions and governance
6. **Complete Audit Trail**: Full tracking of policy enforcement and violations
7. **Maximum Flexibility**: Add any policy type without code changes
8. **Easy to Understand**: Simple, clear model focused on enforcement

## Summary

The Policy Governance System provides comprehensive control over agent behavior through:

- **One Unified Policy Model** - Simple, flexible, user-defined
- **Three-Phase Enforcement** - PRE_REQUEST, DURING_GENERATION, POST_RESPONSE
- **Tag-Based Organization** - User-created tags for flexible categorization
- **System-Managed Lifecycle** - Automated status governance
- **Complete Enforcement Engine** - Validation, remediation, and audit trail
- **Natural Language Policies** - Users write policies in plain language
- **Flexible and Scalable** - Adapts to any governance requirement
