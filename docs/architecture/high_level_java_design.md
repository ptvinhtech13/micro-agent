# High-Level Java Design - AI Agent Architecture

## PART 1: SYSTEM OVERVIEW - Component Summary

```
┌─────────────────────────────────────────────────────────────────────┐
│                     AI AGENT SYSTEM COMPONENTS                       │
└─────────────────────────────────────────────────────────────────────┘

1. CORE ORCHESTRATION LAYER
   ├── AgentBrain (Main orchestrator)
   ├── IntentClassifier (Routing decision)
   └── ExecutionRouter (Path selector)

2. CONTEXT & MEMORY LAYER
   ├── ContextEngine (Context builder)
   ├── MemoryManager (Multi-tier memory)
   ├── MemoryRetriever (Relevance scorer + retriever)
   └── ContextWindowOptimizer (Token budget manager)

3. TASK MANAGEMENT LAYER
   ├── TaskDecomposer (DAG creator)
   ├── TaskPlanExecutor (Programmatic executor)
   ├── CollaboratorSelector (Tool/Agent selector)
   └── ParameterSubstitutionEngine (Parameter resolver)

4. TOOL & MCP LAYER
   ├── ToolRegistry (Tool catalog)
   ├── MCPServerManager (MCP integration)
   └── ToolExecutor (Tool invocation)

5. COMMUNICATION LAYER
   ├── AgentController (REST API)
   ├── EventPublisher (Kafka/Event bus)
   └── ResponseFormatter (Response builder)

6. PREDEFINED FLOW LAYER
   ├── FlowRepository (Template storage)
   ├── FlowMatcher (Intent → Flow matching)
   └── FlowExecutor (Template execution)
```

---

## PART 2: DATA STRUCTURES

### 2.1 Core Domain Models

```java
// ============================================
// AGENT REQUEST/RESPONSE
// ============================================

package com.agent.core.model;

import java.time.Instant;
import java.util.Map;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AgentRequest {
    private String conversationId;
    private String userId;
    private String sessionId;
    private String message;
    private Map<String, Object> context;
    private Instant timestamp;
}

@Data
@Builder
public class AgentResponse {
    private String conversationId;
    private String responseId;
    private String content;
    private Float confidence;
    private ReasoningTrace reasoningTrace;
    private TaskPlan taskPlan;              // If complex path
    private List<ToolExecution> toolExecutions;
    private ResponseMetadata metadata;
}

@Data
@Builder
public class ResponseMetadata {
    private TokenUsage tokenUsage;
    private Duration latency;
    private String modelUsed;
    private String executionPath;           // PREDEFINED | SIMPLE | MEDIUM | COMPLEX
}
```

### 2.2 Context Models

```java
// ============================================
// CONTEXT & MEMORY
// ============================================

package com.agent.core.model.context;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AgentContext {
    private UserContext userContext;
    private EnvironmentContext environmentContext;
    private DomainContext domainContext;
    private TechnicalContext technicalContext;
}

@Data
@Builder
public class UserContext {
    private String userId;
    private UserProfile profile;
    private SessionInfo sessionInfo;
    private Set<Permission> permissions;
}

@Data
@Builder
public class MemorySnapshot {
    private WorkingMemory workingMemory;
    private EpisodicMemory episodicMemory;
    private SemanticMemory semanticMemory;
    private ProceduralMemory proceduralMemory;
}

@Data
@Builder
public class MemoryItem {
    private String memoryId;
    private String content;
    private float[] embedding;              // Vector representation
    private MemoryMetadata metadata;
    private Float importanceScore;
    private Instant timestamp;
}

@Data
@Builder
public class MemoryMetadata {
    private String userId;
    private String conversationId;
    private String intent;
    private String domain;
    private List<String> toolsUsed;
    private Integer referenceCount;         // Frequency
}
```

### 2.3 Intent & Routing Models

```java
// ============================================
// INTENT CLASSIFICATION
// ============================================

package com.agent.core.model.intent;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Intent {
    private String intentId;
    private IntentType type;                // INFORMATIONAL, TRANSACTIONAL, CONVERSATIONAL
    private String domain;                  // BANKING, F&B, GENERAL, etc.
    private Float complexityScore;          // 0.0 - 1.0
    private List<Entity> entities;
    private Float confidence;
}

public enum IntentType {
    INFORMATIONAL,      // Query, explain
    TRANSACTIONAL,      // Transfer, create, delete
    CONVERSATIONAL,     // Chat, discuss
    ANALYTICAL          // Analyze, compare, report
}

@Data
@Builder
public class ComplexityScore {
    private Float intentIndicatorScore;     // 0-1
    private Float toolRequirementScore;     // 0-1
    private Float domainComplexityScore;    // 0-1
    private Float stateDependencyScore;     // 0-1
    private Float finalScore;               // Weighted average
    
    // Weights
    public static final float W_INTENT = 0.3f;
    public static final float W_TOOL = 0.4f;
    public static final float W_DOMAIN = 0.2f;
    public static final float W_STATE = 0.1f;
}

@Data
@Builder
public class RoutingDecision {
    private ExecutionPath path;
    private Float confidence;
    private String reasoning;
    private FlowDefinition matchedFlow;     // If PREDEFINED path
}

public enum ExecutionPath {
    PREDEFINED,     // Use template flow
    SIMPLE,         // Direct LLM
    MEDIUM,         // Single tool (ReAct)
    COMPLEX         // Task decomposition (DAG)
}
```

### 2.4 Task Management Models

```java
// ============================================
// TASK PLAN & DAG
// ============================================

package com.agent.core.model.task;

import lombok.Builder;
import lombok.Data;
import java.util.Set;

@Data
@Builder
public class TaskPlan {
    private String uuid;
    private String conversationId;
    private TaskPlanMetadata metadata;
    private DAG<TaskStep> executionGraph;
    private TaskPlanStatus status;
    private Instant createdAt;
    private Instant completedAt;
}

@Data
@Builder
public class TaskStep {
    // Identity
    private String stepId;
    private String description;
    private StepType type;
    
    // Execution
    private CollaboratorSelection collaborator;
    private Map<String, ParameterBinding> parameterBindings;
    
    // DAG Structure
    private Set<String> dependencies;       // Parent steps
    private Set<String> dependents;         // Child steps
    private ExecutionMode executionMode;
    
    // State
    private StepStatus status;
    private StepResult result;
    private List<StepExecution> executionHistory;
    
    // Constraints
    private Duration timeout;
    private Integer maxRetries;
}

public enum StepType {
    TOOL_CALL,
    AGENT_CALL,
    DECISION,
    DATA_TRANSFORM,
    VALIDATION
}

public enum ExecutionMode {
    SEQUENTIAL,
    PARALLEL,
    CONDITIONAL
}

public enum StepStatus {
    PENDING,
    RUNNING,
    COMPLETED,
    FAILED,
    SKIPPED
}

@Data
@Builder
public class CollaboratorSelection {
    private CollaboratorType type;
    private String collaboratorId;
    private String capability;
    private Map<String, Object> config;
}

public enum CollaboratorType {
    TOOL,
    AGENT,
    MCP_SERVER
}

@Data
@Builder
public class ParameterBinding {
    private String parameterName;
    private ParameterSource source;
    private String sourceReference;         // e.g., "step_1.result.accountNumber"
    private Object defaultValue;
}

public enum ParameterSource {
    CONTEXT,
    PREVIOUS_STEP,
    USER_INPUT,
    MEMORY,
    CONSTANT,
    COMPUTED
}
```

### 2.5 Predefined Flow Models

```java
// ============================================
// PREDEFINED FLOW
// ============================================

package com.agent.core.model.flow;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class FlowDefinition {
    private String flowId;
    private String name;
    private IntentPattern intentPattern;
    private String domain;
    private TaskTemplate taskTemplate;      // Pre-built DAG
    private List<ParameterDefinition> parameters;
    private Duration slaTarget;
    private FlowMetrics metrics;
}

@Data
@Builder
public class IntentPattern {
    private String exactMatch;              // For hash lookup
    private String regexPattern;            // For regex matching
    private float[] semanticEmbedding;      // For vector similarity
}

@Data
@Builder
public class FlowMetrics {
    private Float successRate;
    private Integer usageCount;
    private Duration averageLatency;
    private Instant lastUsed;
}

@Data
@Builder
public class TaskTemplate {
    private String templateId;
    private List<StepTemplate> steps;
    private Map<String, Set<String>> dependencies;  // stepId -> dependencyIds
}

@Data
@Builder
public class StepTemplate {
    private String stepId;
    private String description;
    private StepType type;
    private String toolId;
    private Set<String> dependencies;
    private ExecutionMode executionMode;
}
```

### 2.6 Tool & MCP Models

```java
// ============================================
// TOOL SYSTEM
// ============================================

package com.agent.core.model.tool;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ToolMetadata {
    private String toolId;
    private String name;
    private String description;
    private String version;
    private ToolCategory category;
    private Set<String> capabilities;
}

public enum ToolCategory {
    DATABASE,
    API,
    FILE_SYSTEM,
    NOTIFICATION,
    CALCULATION,
    MCP_SERVER
}

@Data
@Builder
public class ToolSchema {
    private String name;
    private Map<String, ParameterDefinition> inputParameters;
    private Schema outputSchema;
    private Set<Permission> requiredPermissions;
}

@Data
@Builder
public class ParameterDefinition {
    private String name;
    private DataType type;
    private Boolean required;
    private String description;
    private Object defaultValue;
}

@Data
@Builder
public class ToolResult {
    private Boolean success;
    private Object data;
    private ErrorInfo error;
    private Map<String, Object> metadata;
}

@Data
@Builder
public class ToolExecution {
    private String executionId;
    private String toolName;
    private Map<String, Object> input;
    private ToolResult output;
    private Instant startTime;
    private Instant endTime;
}
```

### 2.7 Memory Retrieval Models

```java
// ============================================
// MEMORY RETRIEVAL
// ============================================

package com.agent.core.model.memory;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class MemoryQuery {
    private String userId;
    private String conversationId;
    private String queryText;
    private float[] queryEmbedding;
    private MemoryFilter filter;
    private Integer topK;
}

@Data
@Builder
public class MemoryFilter {
    private String domain;
    private Instant afterTimestamp;
    private Instant beforeTimestamp;
    private Set<String> requiredCapabilities;
    private Float minRelevanceScore;
}

@Data
@Builder
public class RelevanceScore {
    private Float recency;          // 0-1
    private Float frequency;        // 0-1
    private Float similarity;       // 0-1
    private Float taskRelevance;    // 0-1
    private Float finalScore;       // Weighted average
    
    // Weights
    public static final float W_RECENCY = 0.25f;
    public static final float W_FREQUENCY = 0.25f;
    public static final float W_SIMILARITY = 0.3f;
    public static final float W_TASK_RELEVANCE = 0.2f;
}

@Data
@Builder
public class TokenBudget {
    private Integer totalTokens;
    private Map<String, Integer> allocation;    // Component -> tokens
    private Integer used;
    private Integer remaining;
}
```

---

## PART 3: CORE INTERFACES

### 3.1 Agent Brain Interface

```java
// ============================================
// MAIN ORCHESTRATOR
// ============================================

package com.agent.core;

public interface AgentBrain {
    /**
     * Main entry point - processes user request and returns response
     */
    AgentResponse process(AgentRequest request);
    
    /**
     * Learn from feedback (optional for future enhancement)
     */
    void learn(Feedback feedback);
}
```

### 3.2 Context & Memory Interfaces

```java
// ============================================
// CONTEXT ENGINEERING
// ============================================

package com.agent.core.context;

public interface ContextEngine {
    /**
     * Build full agent context from request
     */
    AgentContext buildContext(AgentRequest request);
}

public interface MemoryManager {
    /**
     * Retrieve relevant memories within token budget
     */
    MemorySnapshot retrieve(String conversationId, AgentContext context);
    
    /**
     * Store new memory after execution
     */
    void store(AgentRequest request, AgentResponse response, TaskPlan taskPlan);
    
    /**
     * Consolidate memories (background job)
     */
    void consolidate(String userId);
}

public interface MemoryRetriever {
    /**
     * Multi-tier retrieval with relevance scoring
     */
    List<MemoryItem> retrieve(MemoryQuery query);
    
    /**
     * Calculate relevance score for memory item
     */
    RelevanceScore calculateRelevance(MemoryItem item, MemoryQuery query);
}

public interface ContextWindowOptimizer {
    /**
     * Optimize memory snapshot to fit token budget
     */
    MemorySnapshot optimize(MemorySnapshot snapshot, TokenBudget budget);
    
    /**
     * Calculate token count for content
     */
    Integer countTokens(String content);
}
```

### 3.3 Intent & Routing Interfaces

```java
// ============================================
// INTENT CLASSIFICATION & ROUTING
// ============================================

package com.agent.core.intent;

public interface IntentClassifier {
    /**
     * Classify user intent from request
     */
    Intent classify(AgentRequest request, MemorySnapshot memory);
    
    /**
     * Calculate complexity score
     */
    ComplexityScore calculateComplexity(AgentRequest request, Intent intent);
}

public interface ExecutionRouter {
    /**
     * Decide which execution path to take
     */
    RoutingDecision route(Intent intent, ComplexityScore complexity);
}
```

### 3.4 Task Management Interfaces

```java
// ============================================
// TASK DECOMPOSITION & EXECUTION
// ============================================

package com.agent.core.task;

public interface TaskDecomposer {
    /**
     * Decompose request into task plan (DAG)
     */
    TaskPlan decompose(AgentRequest request, AgentContext context);
}

public interface TaskPlanExecutor {
    /**
     * Execute task plan programmatically
     */
    TaskPlanResult execute(TaskPlan plan, AgentContext context);
}

public interface CollaboratorSelector {
    /**
     * Select best tool/agent for task step
     */
    CollaboratorSelection select(TaskStep step, AgentContext context);
}

public interface ParameterSubstitutionEngine {
    /**
     * Resolve parameters from bindings
     */
    Map<String, Object> substitute(
        Map<String, ParameterBinding> bindings,
        TaskPlanExecutionContext context
    );
}
```

### 3.5 Predefined Flow Interfaces

```java
// ============================================
// PREDEFINED FLOW SYSTEM
// ============================================

package com.agent.core.flow;

public interface FlowRepository {
    /**
     * Find flow matching intent
     */
    Optional<FlowDefinition> findByIntent(Intent intent);
    
    /**
     * Register new flow
     */
    void register(FlowDefinition flow);
    
    /**
     * Get all flows for domain
     */
    List<FlowDefinition> findByDomain(String domain);
}

public interface FlowMatcher {
    /**
     * Match intent to flow using multiple strategies
     */
    Optional<FlowDefinition> match(Intent intent, String userMessage);
}

public interface FlowExecutor {
    /**
     * Execute predefined flow with parameter binding
     */
    TaskPlanResult execute(FlowDefinition flow, AgentRequest request, AgentContext context);
}
```

### 3.6 Tool & MCP Interfaces

```java
// ============================================
// TOOL SYSTEM
// ============================================

package com.agent.core.tool;

public interface Tool {
    /**
     * Get tool metadata
     */
    ToolMetadata getMetadata();
    
    /**
     * Get tool schema
     */
    ToolSchema getSchema();
    
    /**
     * Execute tool with parameters
     */
    ToolResult execute(Map<String, Object> parameters);
}

public interface ToolRegistry {
    /**
     * Register tool
     */
    void register(Tool tool);
    
    /**
     * Get tool by ID
     */
    Optional<Tool> getTool(String toolId);
    
    /**
     * Get tools available in context
     */
    List<Tool> getAvailableTools(AgentContext context);
}

public interface ToolExecutor {
    /**
     * Execute tool with full lifecycle management
     */
    ToolResult execute(Tool tool, Map<String, Object> parameters);
}

public interface MCPServerManager {
    /**
     * Register MCP server
     */
    void registerServer(MCPServerConfig config);
    
    /**
     * List tools from MCP server
     */
    List<ToolSchema> listTools(String serverId);
    
    /**
     * Call tool on MCP server
     */
    ToolResult callTool(String serverId, String toolName, Map<String, Object> params);
}
```

---

## PART 4: HIGH-LEVEL COMPONENT CLASSES

### 4.1 Core Orchestrator

```java
// ============================================
// MAIN AGENT BRAIN IMPLEMENTATION
// ============================================

package com.agent.core.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class DefaultAgentBrain implements AgentBrain {
    
    private final ContextEngine contextEngine;
    private final MemoryManager memoryManager;
    private final IntentClassifier intentClassifier;
    private final ExecutionRouter executionRouter;
    
    // Path executors
    private final FlowExecutor flowExecutor;
    private final SimpleLLMExecutor simpleLLMExecutor;
    private final ReActExecutor reactExecutor;
    private final TaskPlanExecutor taskPlanExecutor;
    
    @Override
    public AgentResponse process(AgentRequest request) {
        // 1. Build context
        AgentContext context = contextEngine.buildContext(request);
        
        // 2. Retrieve memory
        MemorySnapshot memory = memoryManager.retrieve(
            request.getConversationId(), 
            context
        );
        
        // 3. Classify intent
        Intent intent = intentClassifier.classify(request, memory);
        ComplexityScore complexity = intentClassifier.calculateComplexity(request, intent);
        
        // 4. Route to execution path
        RoutingDecision routing = executionRouter.route(intent, complexity);
        
        // 5. Execute based on path
        AgentResponse response = executeByPath(routing, request, context, memory);
        
        // 6. Store memory
        memoryManager.store(request, response, response.getTaskPlan());
        
        return response;
    }
    
    private AgentResponse executeByPath(
        RoutingDecision routing,
        AgentRequest request,
        AgentContext context,
        MemorySnapshot memory
    ) {
        switch (routing.getPath()) {
            case PREDEFINED:
                return flowExecutor.execute(routing.getMatchedFlow(), request, context);
                
            case SIMPLE:
                return simpleLLMExecutor.execute(request, context, memory);
                
            case MEDIUM:
                return reactExecutor.execute(request, context, memory);
                
            case COMPLEX:
                TaskPlan plan = taskDecomposer.decompose(request, context);
                TaskPlanResult result = taskPlanExecutor.execute(plan, context);
                return buildResponse(result);
                
            default:
                throw new UnsupportedExecutionPathException(routing.getPath());
        }
    }
    
    @Override
    public void learn(Feedback feedback) {
        // Future enhancement - online learning
    }
}
```

### 4.2 Intent Classifier

```java
// ============================================
// INTENT CLASSIFICATION
// ============================================

package com.agent.core.intent.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class MultiDimensionalIntentClassifier implements IntentClassifier {
    
    private final IntentPatternMatcher patternMatcher;
    private final EntityExtractor entityExtractor;
    
    @Override
    public Intent classify(AgentRequest request, MemorySnapshot memory) {
        // Pattern matching + entity extraction
        // Returns intent with type, domain, entities
        return null; // Implementation placeholder
    }
    
    @Override
    public ComplexityScore calculateComplexity(AgentRequest request, Intent intent) {
        Float intentScore = calculateIntentIndicatorScore(request);
        Float toolScore = calculateToolRequirementScore(intent);
        Float domainScore = calculateDomainComplexityScore(intent);
        Float stateScore = calculateStateDependencyScore(request);
        
        Float finalScore = 
            ComplexityScore.W_INTENT * intentScore +
            ComplexityScore.W_TOOL * toolScore +
            ComplexityScore.W_DOMAIN * domainScore +
            ComplexityScore.W_STATE * stateScore;
        
        return ComplexityScore.builder()
            .intentIndicatorScore(intentScore)
            .toolRequirementScore(toolScore)
            .domainComplexityScore(domainScore)
            .stateDependencyScore(stateScore)
            .finalScore(finalScore)
            .build();
    }
    
    private Float calculateIntentIndicatorScore(AgentRequest request) {
        // Analyze keywords: simple vs complex indicators
        return null; // Implementation placeholder
    }
    
    private Float calculateToolRequirementScore(Intent intent) {
        // Determine if tools needed and how many
        return null; // Implementation placeholder
    }
    
    private Float calculateDomainComplexityScore(Intent intent) {
        // Domain-based complexity (banking=high, general=low)
        return null; // Implementation placeholder
    }
    
    private Float calculateStateDependencyScore(AgentRequest request) {
        // Check if request depends on conversation history
        return null; // Implementation placeholder
    }
}
```

### 4.3 Execution Router

```java
// ============================================
// ROUTING DECISION
// ============================================

package com.agent.core.intent.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class SmartExecutionRouter implements ExecutionRouter {
    
    private final FlowMatcher flowMatcher;
    
    @Override
    public RoutingDecision route(Intent intent, ComplexityScore complexity) {
        // Step 1: Try predefined flow first
        Optional<FlowDefinition> flow = flowMatcher.match(intent, intent.getUserMessage());
        if (flow.isPresent()) {
            return RoutingDecision.builder()
                .path(ExecutionPath.PREDEFINED)
                .matchedFlow(flow.get())
                .confidence(0.95f)
                .reasoning("Matched predefined flow: " + flow.get().getName())
                .build();
        }
        
        // Step 2: Route based on complexity
        ExecutionPath path = determinePathByComplexity(complexity.getFinalScore());
        
        return RoutingDecision.builder()
            .path(path)
            .confidence(0.8f)
            .reasoning("Complexity-based routing: " + complexity.getFinalScore())
            .build();
    }
    
    private ExecutionPath determinePathByComplexity(Float score) {
        if (score < 0.3f) {
            return ExecutionPath.SIMPLE;
        } else if (score < 0.7f) {
            return ExecutionPath.MEDIUM;
        } else {
            return ExecutionPath.COMPLEX;
        }
    }
}
```

### 4.4 Memory Manager

```java
// ============================================
// MEMORY MANAGEMENT
// ============================================

package com.agent.core.context.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MultiTierMemoryManager implements MemoryManager {
    
    private final MemoryRetriever memoryRetriever;
    private final ContextWindowOptimizer contextOptimizer;
    private final WorkingMemoryStore workingMemoryStore;     // Redis
    private final EpisodicMemoryStore episodicMemoryStore;   // Vector DB
    private final SemanticMemoryStore semanticMemoryStore;   // PostgreSQL
    
    @Override
    public MemorySnapshot retrieve(String conversationId, AgentContext context) {
        // L1: Working memory (Redis)
        WorkingMemory working = workingMemoryStore.get(conversationId);
        
        // L2: Episodic memory (Vector DB - semantic search)
        MemoryQuery query = buildQuery(conversationId, context);
        List<MemoryItem> episodes = memoryRetriever.retrieve(query);
        
        // L3: Semantic memory (PostgreSQL - facts)
        SemanticMemory semantic = semanticMemoryStore.getFacts(context.getUserContext().getUserId());
        
        // Build snapshot
        MemorySnapshot snapshot = MemorySnapshot.builder()
            .workingMemory(working)
            .episodicMemory(new EpisodicMemory(episodes))
            .semanticMemory(semantic)
            .build();
        
        // Optimize for token budget
        TokenBudget budget = calculateBudget(context);
        return contextOptimizer.optimize(snapshot, budget);
    }
    
    @Override
    public void store(AgentRequest request, AgentResponse response, TaskPlan taskPlan) {
        // Store in appropriate tiers
        // Implementation placeholder
    }
    
    @Override
    public void consolidate(String userId) {
        // Background job: summarization, fact extraction, pattern learning
        // Implementation placeholder
    }
    
    private MemoryQuery buildQuery(String conversationId, AgentContext context) {
        // Build query for episodic retrieval
        return null; // Implementation placeholder
    }
    
    private TokenBudget calculateBudget(AgentContext context) {
        // Calculate token budget allocation
        return null; // Implementation placeholder
    }
}
```

### 4.5 Task Plan Executor

```java
// ============================================
// PROGRAMMATIC TASK EXECUTION
// ============================================

package com.agent.core.task.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ProgrammaticTaskPlanExecutor implements TaskPlanExecutor {
    
    private final ParameterSubstitutionEngine parameterEngine;
    private final ToolExecutor toolExecutor;
    private final CollaboratorSelector collaboratorSelector;
    
    @Override
    public TaskPlanResult execute(TaskPlan plan, AgentContext context) {
        TaskPlanExecutionContext execContext = initContext(plan, context);
        DAG<TaskStep> dag = plan.getExecutionGraph();
        
        while (hasRemainingSteps(execContext)) {
            // Get executable steps (dependencies met)
            List<TaskStep> executable = dag.getExecutableSteps(execContext.getCompletedSteps());
            
            // Execute batch (supports parallel)
            executeBatch(executable, execContext);
        }
        
        return buildResult(execContext);
    }
    
    private void executeBatch(List<TaskStep> steps, TaskPlanExecutionContext context) {
        // Group by execution mode
        Map<ExecutionMode, List<TaskStep>> grouped = groupByMode(steps);
        
        // Sequential
        executeSequential(grouped.get(ExecutionMode.SEQUENTIAL), context);
        
        // Parallel
        executeParallel(grouped.get(ExecutionMode.PARALLEL), context);
    }
    
    private void executeSequential(List<TaskStep> steps, TaskPlanExecutionContext context) {
        // Execute one by one
        // Implementation placeholder
    }
    
    private void executeParallel(List<TaskStep> steps, TaskPlanExecutionContext context) {
        // Execute concurrently with CompletableFuture
        // Implementation placeholder
    }
    
    private TaskPlanExecutionContext initContext(TaskPlan plan, AgentContext context) {
        // Initialize execution context
        return null; // Implementation placeholder
    }
    
    private boolean hasRemainingSteps(TaskPlanExecutionContext context) {
        // Check if all steps completed
        return false; // Implementation placeholder
    }
    
    private TaskPlanResult buildResult(TaskPlanExecutionContext context) {
        // Build final result
        return null; // Implementation placeholder
    }
    
    private Map<ExecutionMode, List<TaskStep>> groupByMode(List<TaskStep> steps) {
        // Group steps by execution mode
        return null; // Implementation placeholder
    }
}
```

### 4.6 Flow Matcher

```java
// ============================================
// PREDEFINED FLOW MATCHING
// ============================================

package com.agent.core.flow.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class MultiStrategyFlowMatcher implements FlowMatcher {
    
    private final FlowRepository flowRepository;
    private final EmbeddingService embeddingService;
    
    @Override
    public Optional<FlowDefinition> match(Intent intent, String userMessage) {
        // Strategy 1: Exact intent match (fastest)
        Optional<FlowDefinition> exact = exactMatch(intent);
        if (exact.isPresent()) return exact;
        
        // Strategy 2: Regex pattern match
        Optional<FlowDefinition> regex = regexMatch(userMessage);
        if (regex.isPresent()) return regex;
        
        // Strategy 3: Semantic similarity
        Optional<FlowDefinition> semantic = semanticMatch(userMessage);
        if (semantic.isPresent()) return semantic;
        
        // No match found
        return Optional.empty();
    }
    
    private Optional<FlowDefinition> exactMatch(Intent intent) {
        // Hash lookup by intent ID
        return flowRepository.findByIntent(intent);
    }
    
    private Optional<FlowDefinition> regexMatch(String message) {
        // Regex pattern matching
        return null; // Implementation placeholder
    }
    
    private Optional<FlowDefinition> semanticMatch(String message) {
        // Vector similarity
        float[] embedding = embeddingService.embed(message);
        // Compare with flow embeddings
        return null; // Implementation placeholder
    }
}
```

---

## PART 5: CONFIGURATION & SPRING BEANS

```java
// ============================================
// SPRING CONFIGURATION
// ============================================

package com.agent.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;

@Configuration
public class AgentConfiguration {
    
    @Bean
    public RedisTemplate<String, Object> redisTemplate(
        RedisConnectionFactory connectionFactory
    ) {
        // Redis for working memory
        return null; // Configuration placeholder
    }
    
    @Bean
    public ExecutorService taskExecutorService() {
        // Thread pool for parallel execution
        return null; // Configuration placeholder
    }
    
    // Other beans...
}
```

This is the HIGH-LEVEL design, Vinh! Clear abstractions, no detailed implementations yet. Ready for your review! 🎯
