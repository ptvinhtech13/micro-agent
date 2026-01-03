# Enhanced AI Agent Data Flow - With Feedback Loop & Context Management

## 1. COMPLETE DATA FLOW WITH FEEDBACK LOOP & MEMORY REUSE

```
┌─────────────────────────────────────────────────────────────────────────────┐
│                          FEEDBACK LOOP SYSTEM                                │
│  ┌─────────────────────────────────────────────────────────────────────┐   │
│  │                     MEMORY CONSOLIDATION ENGINE                      │   │
│  │  • Episodic Memory Indexing (Vector DB)                             │   │
│  │  • Semantic Memory Extraction (Facts, Preferences)                  │   │
│  │  • Procedural Memory Learning (Successful Patterns)                 │   │
│  └─────────────────────────────────────────────────────────────────────┘   │
└─────────────────────────────────────────────────────────────────────────────┘
                                    ▲
                                    │
                         (7) Store execution trace,
                             results, learned patterns
                                    │
┌───────────────────────────────────┴─────────────────────────────────────────┐
│                                                                               │
│  USER REQUEST                                                                │
│       │                                                                       │
│       ▼                                                                       │
│  ┌──────────────────────────────┐                                           │
│  │   1. COMMUNICATION LAYER     │ ◄── HTTP/WebSocket/Kafka                  │
│  │   • Request Validation       │                                            │
│  │   • Rate Limiting            │                                            │
│  │   • Session Management       │                                            │
│  └──────────┬───────────────────┘                                           │
│             │                                                                 │
│             ▼                                                                 │
│  ┌──────────────────────────────┐                                           │
│  │   2. CONTEXT BUILDER         │                                           │
│  │   Build AgentContext from:   │                                           │
│  │   • User Profile             │                                            │
│  │   • Session Info             │                                            │
│  │   • Environment Data         │                                            │
│  │   • Available Tools          │                                            │
│  └──────────┬───────────────────┘                                           │
│             │                                                                 │
│             ▼                                                                 │
│  ┌──────────────────────────────────────────────────────────────────────┐  │
│  │   3. MEMORY RETRIEVAL LAYER (CONTEXT ENGINEERING)                    │  │
│  │   ┌────────────────────────────────────────────────────────────────┐ │  │
│  │   │ 3a. RELEVANCE SCORER                                           │ │  │
│  │   │  Score each memory by:                                         │ │  │
│  │   │  • Recency (temporal distance from now)                        │ │  │
│  │   │  • Frequency (how often referenced)                            │ │  │
│  │   │  • Semantic Similarity (vector cosine similarity)              │ │  │
│  │   │  • Task Relevance (matching intent/domain)                     │ │  │
│  │   │                                                                 │ │  │
│  │   │  Final Score = w1×Recency + w2×Frequency +                     │ │  │
│  │   │                w3×Similarity + w4×Relevance                    │ │  │
│  │   └────────────────────────────────────────────────────────────────┘ │  │
│  │                                                                         │  │
│  │   ┌────────────────────────────────────────────────────────────────┐ │  │
│  │   │ 3b. MEMORY RETRIEVAL STRATEGY (Multi-Tier)                    │ │  │
│  │   │                                                                 │ │  │
│  │   │  L1 - Working Memory (Redis - Hot Cache)                       │ │  │
│  │   │  ├─ Last 50 messages from current conversation                │ │  │
│  │   │  ├─ Active user context                                        │ │  │
│  │   │  └─ Recent tool results (TTL: 1 hour)                          │ │  │
│  │   │     ↓ Cache Hit = Instant (< 1ms)                              │ │  │
│  │   │                                                                 │ │  │
│  │   │  L2 - Episodic Memory (Vector DB - Warm Storage)               │ │  │
│  │   │  ├─ Semantic search for similar conversations                 │ │  │
│  │   │  ├─ Top-K retrieval (K=5 by default)                           │ │  │
│  │   │  └─ Filter by relevance score > threshold (0.7)               │ │  │
│  │   │     ↓ Vector Search (~10-50ms)                                 │ │  │
│  │   │                                                                 │ │  │
│  │   │  L3 - Semantic Memory (PostgreSQL - Facts)                     │ │  │
│  │   │  ├─ User preferences, profile facts                            │ │  │
│  │   │  ├─ Domain knowledge                                           │ │  │
│  │   │  └─ Indexed by userId, domain, category                        │ │  │
│  │   │     ↓ SQL Query (~5-20ms)                                      │ │  │
│  │   │                                                                 │ │  │
│  │   │  L4 - Procedural Memory (Templates)                            │ │  │
│  │   │  ├─ Successful task patterns                                   │ │  │
│  │   │  ├─ Common workflows by intent                                 │ │  │
│  │   │  └─ Cached in memory                                           │ │  │
│  │   │     ↓ In-Memory Lookup (< 1ms)                                 │ │  │
│  │   └────────────────────────────────────────────────────────────────┘ │  │
│  │                                                                         │  │
│  │   ┌────────────────────────────────────────────────────────────────┐ │  │
│  │   │ 3c. CONTEXT WINDOW OPTIMIZER (Token Budget Manager)           │ │  │
│  │   │                                                                 │ │  │
│  │   │  Token Budget Allocation:                                      │ │  │
│  │   │  ┌─────────────────────────────────────────────────┐          │ │  │
│  │   │  │ Total Context Window: 8,192 tokens (example)    │          │ │  │
│  │   │  ├─────────────────────────────────────────────────┤          │ │  │
│  │   │  │ System Prompt: 500 tokens (6%)                  │          │ │  │
│  │   │  │ User Message: 200 tokens (2%)                   │          │ │  │
│  │   │  │ Working Memory: 2,000 tokens (24%)              │          │ │  │
│  │   │  │ Episodic Retrieval: 2,000 tokens (24%)          │          │ │  │
│  │   │  │ Semantic Facts: 500 tokens (6%)                 │          │ │  │
│  │   │  │ Tool Schemas: 1,000 tokens (12%)                │          │ │  │
│  │   │  │ Response Buffer: 2,000 tokens (24%)             │          │ │  │
│  │   │  │ Safety Margin: 192 tokens (2%)                  │          │ │  │
│  │   │  └─────────────────────────────────────────────────┘          │ │  │
│  │   │                                                                 │ │  │
│  │   │  ADAPTIVE TRUNCATION STRATEGIES:                               │ │  │
│  │   │                                                                 │ │  │
│  │   │  Strategy 1: Sliding Window (for Working Memory)               │ │  │
│  │   │  ├─ Keep most recent N messages                                │ │  │
│  │   │  ├─ Summarize older messages                                   │ │  │
│  │   │  └─ Discard low-relevance messages                             │ │  │
│  │   │                                                                 │ │  │
│  │   │  Strategy 2: Hierarchical Summarization (for Episodes)         │ │  │
│  │   │  ├─ Original episode: 500 tokens                               │ │  │
│  │   │  ├─ Level 1 summary: 100 tokens (5:1 compression)              │ │  │
│  │   │  ├─ Level 2 summary: 20 tokens (25:1 compression)              │ │  │
│  │   │  └─ Use appropriate level based on relevance                   │ │  │
│  │   │                                                                 │ │  │
│  │   │  Strategy 3: Importance-Based Pruning                          │ │  │
│  │   │  ├─ Calculate importance score per memory                      │ │  │
│  │   │  ├─ Sort by score                                              │ │  │
│  │   │  ├─ Include until token budget exhausted                       │ │  │
│  │   │  └─ Maintain diversity (avoid duplicates)                      │ │  │
│  │   │                                                                 │ │  │
│  │   │  Strategy 4: Incremental Context Loading                       │ │  │
│  │   │  ├─ Start with minimal context                                 │ │  │
│  │   │  ├─ If LLM requests more info → expand context                 │ │  │
│  │   │  └─ Dynamic retrieval during reasoning                         │ │  │
│  │   └────────────────────────────────────────────────────────────────┘ │  │
│  │                                                                         │  │
│  │   OUTPUT: Optimized MemorySnapshot (within token budget)               │  │
│  └──────────┬──────────────────────────────────────────────────────────────┘  │
│             │                                                                 │
│             ▼                                                                 │
│  ┌──────────────────────────────────────────────────────────────────────┐  │
│  │   4. INTENT CLASSIFIER & ROUTING DECISION                            │  │
│  │                                                                         │  │
│  │   ┌─────────────────────────────────────────────────────────────┐    │  │
│  │   │ 4a. INTENT CLASSIFICATION                                   │    │  │
│  │   │                                                              │    │  │
│  │   │  Classification Dimensions:                                 │    │  │
│  │   │                                                              │    │  │
│  │   │  1. Complexity Score (0.0 - 1.0)                            │    │  │
│  │   │     ├─ Simple (0.0 - 0.3):                                  │    │  │
│  │   │     │  • Single question/answer                             │    │  │
│  │   │     │  • No tool needed                                      │    │  │
│  │   │     │  • Examples:                                           │    │  │
│  │   │     │    - "What is machine learning?"                       │    │  │
│  │   │     │    - "Explain SOLID principles"                        │    │  │
│  │   │     │    - "How are you?"                                    │    │  │
│  │   │     │                                                         │    │  │
│  │   │     ├─ Medium (0.3 - 0.7):                                   │    │  │
│  │   │     │  • Single tool call needed                             │    │  │
│  │   │     │  • 1-2 steps                                           │    │  │
│  │   │     │  • Examples:                                           │    │  │
│  │   │     │    - "Check my account balance"                        │    │  │
│  │   │     │    - "What's the weather in Tokyo?"                    │    │  │
│  │   │     │    - "Search for recent AI papers"                     │    │  │
│  │   │     │                                                         │    │  │
│  │   │     └─ Complex (0.7 - 1.0):                                  │    │  │
│  │   │        • Multiple steps with dependencies                    │    │  │
│  │   │        • Multiple tools/agents                               │    │  │
│  │   │        • Conditional logic                                   │    │  │
│  │   │        • Examples:                                           │    │  │
│  │   │          - "Transfer money and notify me"                    │    │  │
│  │   │          - "Analyze sales data and create report"            │    │  │
│  │   │          - "Book flight, hotel, and rental car"              │    │  │
│  │   │                                                               │    │  │
│  │   │  2. Domain Match                                             │    │  │
│  │   │     • Banking, F&B, Healthcare, General, etc.                │    │  │
│  │   │     • Determines available tools/agents                      │    │  │
│  │   │                                                               │    │  │
│  │   │  3. Action Type                                              │    │  │
│  │   │     • INFORMATIONAL (query, explain)                         │    │  │
│  │   │     • TRANSACTIONAL (transfer, create, delete)               │    │  │
│  │   │     • CONVERSATIONAL (chat, discuss)                         │    │  │
│  │   │     • ANALYTICAL (analyze, compare, report)                  │    │  │
│  │   └──────────────────────────────────────────────────────────────┘    │  │
│  │                                                                         │  │
│  │   ┌─────────────────────────────────────────────────────────────┐    │  │
│  │   │ 4b. ROUTING DECISION TREE                                   │    │  │
│  │   │                                                              │    │  │
│  │   │  Step 1: Check Predefined Flow Repository                   │    │  │
│  │   │  ┌────────────────────────────────────────────┐             │    │  │
│  │   │  │ Predefined Flow Matching:                  │             │    │  │
│  │   │  │                                             │             │    │  │
│  │   │  │ Flow Repository (by Intent Pattern):       │             │    │  │
│  │   │  │                                             │             │    │  │
│  │   │  │ • "check_balance" → Flow_001               │             │    │  │
│  │   │  │   Template: Single tool call               │             │    │  │
│  │   │  │   Steps: [authenticate, query_balance]     │             │    │  │
│  │   │  │   SLA: < 500ms                             │             │    │  │
│  │   │  │                                             │             │    │  │
│  │   │  │ • "transfer_money" → Flow_002              │             │    │  │
│  │   │  │   Template: Multi-step with validation    │             │    │  │
│  │   │  │   Steps: [verify, check_balance,           │             │    │  │
│  │   │  │           execute_transfer, notify]        │             │    │  │
│  │   │  │   SLA: < 2s                                │             │    │  │
│  │   │  │                                             │             │    │  │
│  │   │  │ • "order_food" → Flow_003                  │             │    │  │
│  │   │  │   Template: E-commerce workflow            │             │    │  │
│  │   │  │   Steps: [search_menu, select_items,       │             │    │  │
│  │   │  │           calculate_total, process_order]  │             │    │  │
│  │   │  │   SLA: < 1.5s                              │             │    │  │
│  │   │  │                                             │             │    │  │
│  │   │  │ Matching Strategy:                         │             │    │  │
│  │   │  │ 1. Exact intent match (fastest)            │             │    │  │
│  │   │  │ 2. Pattern regex match                     │             │    │  │
│  │   │  │ 3. Semantic similarity (embeddings)        │             │    │  │
│  │   │  │ 4. No match → proceed to dynamic routing   │             │    │  │
│  │   │  └────────────────────────────────────────────┘             │    │  │
│  │   │                                                              │    │  │
│  │   │  Step 2: Dynamic Routing (if no predefined flow)            │    │  │
│  │   │  ┌────────────────────────────────────────────┐             │    │  │
│  │   │  │                                             │             │    │  │
│  │   │  │  Complexity < 0.3 (SIMPLE)                 │             │    │  │
│  │   │  │       ↓                                     │             │    │  │
│  │   │  │  ┌────────────────────┐                    │             │    │  │
│  │   │  │  │ Direct LLM         │                    │             │    │  │
│  │   │  │  │ Response           │                    │             │    │  │
│  │   │  │  │ (No tools)         │                    │             │    │  │
│  │   │  │  └────────────────────┘                    │             │    │  │
│  │   │  │                                             │             │    │  │
│  │   │  │  0.3 ≤ Complexity < 0.7 (MEDIUM)           │             │    │  │
│  │   │  │       ↓                                     │             │    │  │
│  │   │  │  ┌────────────────────┐                    │             │    │  │
│  │   │  │  │ Single Tool        │                    │             │    │  │
│  │   │  │  │ Execution          │                    │             │    │  │
│  │   │  │  │ (ReAct pattern)    │                    │             │    │  │
│  │   │  │  └────────────────────┘                    │             │    │  │
│  │   │  │                                             │             │    │  │
│  │   │  │  Complexity ≥ 0.7 (COMPLEX)                │             │    │  │
│  │   │  │       ↓                                     │             │    │  │
│  │   │  │  ┌────────────────────┐                    │             │    │  │
│  │   │  │  │ Task Decomposition │                    │             │    │  │
│  │   │  │  │ (DAG Creation)     │                    │             │    │  │
│  │   │  │  └────────────────────┘                    │             │    │  │
│  │   │  │                                             │             │    │  │
│  │   │  └────────────────────────────────────────────┘             │    │  │
│  │   └──────────────────────────────────────────────────────────────┘    │  │
│  │                                                                         │  │
│  │   OUTPUT: RoutingDecision {path, confidence, reasoning}                │  │
│  └──────────┬──────────────────────────────────────────────────────────────┘  │
│             │                                                                 │
│             │                                                                 │
│         ┌───┴────┬──────────────────┬──────────────────────────┐            │
│         ▼        ▼                  ▼                          ▼             │
│    ┌─────┐  ┌─────────┐    ┌──────────────┐    ┌─────────────────────┐    │
│    │PATH │  │ PATH 2  │    │   PATH 3     │    │      PATH 4         │    │
│    │  1  │  │         │    │              │    │                     │    │
│    └─────┘  └─────────┘    └──────────────┘    └─────────────────────┘    │
│                                                                               │
│  ┌─────────────────────────────────────────────────────────────────────┐   │
│  │ PATH 1: PREDEFINED FLOW EXECUTION (Fastest, Most Reliable)          │   │
│  │                                                                       │   │
│  │  ┌────────────────┐                                                  │   │
│  │  │ Flow Template  │ ◄── Pre-built, tested, optimized                │   │
│  │  │ Executor       │     • No LLM planning overhead                   │   │
│  │  └────────┬───────┘     • Deterministic execution                   │   │
│  │           │              • Sub-second latency                        │   │
│  │           ▼                                                           │   │
│  │  ┌────────────────┐                                                  │   │
│  │  │ Parameter      │ ◄── Bind user input to template params          │   │
│  │  │ Binding        │                                                  │   │
│  │  └────────┬───────┘                                                  │   │
│  │           │                                                           │   │
│  │           ▼                                                           │   │
│  │  ┌────────────────┐                                                  │   │
│  │  │ Execute Steps  │ ◄── Run predefined DAG                           │   │
│  │  │ (Programmatic) │                                                  │   │
│  │  └────────┬───────┘                                                  │   │
│  │           │                                                           │   │
│  │           └──────────────────┐                                       │   │
│  └──────────────────────────────┼───────────────────────────────────────┘   │
│                                 │                                            │
│  ┌─────────────────────────────┼───────────────────────────────────────┐   │
│  │ PATH 2: SIMPLE - DIRECT LLM │ (No Task Decomposition)               │   │
│  │                              │                                        │   │
│  │                              ▼                                        │   │
│  │                     ┌────────────────┐                               │   │
│  │                     │ LLM Generate   │ ◄── Single completion        │   │
│  │                     │ Response       │     No tools                  │   │
│  │                     └────────┬───────┘                               │   │
│  │                              │                                        │   │
│  │                              └──────────────────┐                    │   │
│  └────────────────────────────────────────────────┼────────────────────┘   │
│                                                    │                        │
│  ┌────────────────────────────────────────────────┼────────────────────┐   │
│  │ PATH 3: MEDIUM - SINGLE TOOL (ReAct Pattern)   │                    │   │
│  │                                                 │                    │   │
│  │                                                 ▼                    │   │
│  │                                        ┌────────────────┐            │   │
│  │                                        │ Reasoning      │            │   │
│  │                                        │ Engine         │            │   │
│  │                                        └────────┬───────┘            │   │
│  │                                                 │                    │   │
│  │                                                 ▼                    │   │
│  │                                        ┌────────────────┐            │   │
│  │                                        │ Tool Selection │            │   │
│  │                                        │ & Execution    │            │   │
│  │                                        └────────┬───────┘            │   │
│  │                                                 │                    │   │
│  │                                                 ▼                    │   │
│  │                                        ┌────────────────┐            │   │
│  │                                        │ Result         │            │   │
│  │                                        │ Synthesis      │            │   │
│  │                                        └────────┬───────┘            │   │
│  │                                                 │                    │   │
│  │                                                 └──────────────┐     │   │
│  └───────────────────────────────────────────────────────────────┼─────┘   │
│                                                                   │         │
│  ┌───────────────────────────────────────────────────────────────┼─────┐   │
│  │ PATH 4: COMPLEX - TASK DECOMPOSITION (Full DAG)               │     │   │
│  │                                                                ▼     │   │
│  │                                                       ┌────────────┐ │   │
│  │                                                       │Task        │ │   │
│  │                                                       │Decomposer  │ │   │
│  │                                                       └──────┬─────┘ │   │
│  │                                                              │       │   │
│  │                                                              ▼       │   │
│  │                                                       ┌────────────┐ │   │
│  │                                                       │Collaborator│ │   │
│  │                                                       │Selector    │ │   │
│  │                                                       └──────┬─────┘ │   │
│  │                                                              │       │   │
│  │                                                              ▼       │   │
│  │                                                       ┌────────────┐ │   │
│  │                                                       │Parameter   │ │   │
│  │                                                       │Substitution│ │   │
│  │                                                       └──────┬─────┘ │   │
│  │                                                              │       │   │
│  │                                                              ▼       │   │
│  │                                                       ┌────────────┐ │   │
│  │                                                       │Programmatic│ │   │
│  │                                                       │Executor    │ │   │
│  │                                                       └──────┬─────┘ │   │
│  │                                                              │       │   │
│  │                                                              └───┐   │   │
│  └──────────────────────────────────────────────────────────────────┼───┘   │
│                                                                      │       │
│              ┌───────────────────┬──────────────────┬────────────────┘       │
│              ▼                   ▼                  ▼                        │
│         All paths converge here                                             │
│              │                                                               │
│              ▼                                                               │
│  ┌──────────────────────────────┐                                           │
│  │   6. RESPONSE SYNTHESIZER    │                                           │
│  │   • Format final response    │                                           │
│  │   • Add metadata/trace       │                                           │
│  └──────────┬───────────────────┘                                           │
│             │                                                                │
│             ▼                                                                │
│  ┌──────────────────────────────────────────────────────────────────────┐  │
│  │   7. MEMORY UPDATE & FEEDBACK LOOP                                   │  │
│  │                                                                         │  │
│  │   ┌─────────────────────────────────────────────────────────────┐    │  │
│  │   │ What to Store (Selective Storage):                          │    │  │
│  │   │                                                              │    │  │
│  │   │ A. Conversation Episode                                     │    │  │
│  │   │    ├─ User message + Agent response                         │    │  │
│  │   │    ├─ Embedding vector (for semantic search)                │    │  │
│  │   │    ├─ Metadata: intent, domain, tools_used                  │    │  │
│  │   │    └─ Importance score (calculated)                         │    │  │
│  │   │                                                              │    │  │
│  │   │ B. Task Execution Trace (if complex path)                   │    │  │
│  │   │    ├─ TaskPlan DAG structure                                │    │  │
│  │   │    ├─ Each step: input, output, duration                    │    │  │
│  │   │    ├─ Success/failure status                                │    │  │
│  │   │    └─ Link to episode                                       │    │  │
│  │   │                                                              │    │  │
│  │   │ C. Extracted Facts (semantic memory)                        │    │  │
│  │   │    ├─ User preferences discovered                           │    │  │
│  │   │    ├─ Domain knowledge learned                              │    │  │
│  │   │    └─ Relationship updates                                  │    │  │
│  │   │                                                              │    │  │
│  │   │ D. Successful Patterns (procedural memory)                  │    │  │
│  │   │    ├─ Intent → TaskPlan mapping (if worked well)            │    │  │
│  │   │    ├─ Tool selection patterns                               │    │  │
│  │   │    └─ Parameter binding patterns                            │    │  │
│  │   │                                                              │    │  │
│  │   │ E. Performance Metrics                                      │    │  │
│  │   │    ├─ Latency per component                                 │    │  │
│  │   │    ├─ Token usage                                           │    │  │
│  │   │    ├─ Tool execution time                                   │    │  │
│  │   │    └─ User satisfaction (if available)                      │    │  │
│  │   └─────────────────────────────────────────────────────────────┘    │  │
│  │                                                                         │  │
│  │   ┌─────────────────────────────────────────────────────────────┐    │  │
│  │   │ Memory Consolidation Process (Background Job):              │    │  │
│  │   │                                                              │    │  │
│  │   │ Job Schedule: Every 1 hour or after N conversations         │    │  │
│  │   │                                                              │    │  │
│  │   │ 1. Episode Summarization                                    │    │  │
│  │   │    • Long conversations → hierarchical summaries            │    │  │
│  │   │    • Keep original + Level1 + Level2 summaries              │    │  │
│  │   │                                                              │    │  │
│  │   │ 2. Fact Extraction (LLM-powered)                            │    │  │
│  │   │    • Extract user preferences, facts                        │    │  │
│  │   │    • Update knowledge graph                                 │    │  │
│  │   │    • Resolve conflicts (newest wins)                        │    │  │
│  │   │                                                              │    │  │
│  │   │ 3. Pattern Learning                                         │    │  │
│  │   │    • Identify successful task plans                         │    │  │
│  │   │    • Frequency analysis: intent → plan                      │    │  │
│  │   │    • Promote frequent patterns to templates                 │    │  │
│  │   │                                                              │    │  │
│  │   │ 4. Memory Pruning (Forgetting)                              │    │  │
│  │   │    • Remove low-importance episodes older than 90 days      │    │  │
│  │   │    • Keep summaries of deleted episodes                     │    │  │
│  │   │    • Archive instead of delete (for compliance)             │    │  │
│  │   │                                                              │    │  │
│  │   │ 5. Index Optimization                                       │    │  │
│  │   │    • Update vector index                                    │    │  │
│  │   │    • Rebuild search indices                                 │    │  │
│  │   │    • Update relevance scores                                │    │  │
│  │   └─────────────────────────────────────────────────────────────┘    │  │
│  └─────────────────────────────────────────────────────────────────────────┘  │
│             │                                                                │
│             └───────────────────────────────────────────────────────────┐   │
│                                                                          │   │
│                                                                          ▼   │
│                                                                  RESPONSE TO USER
│                                                                               │
└───────────────────────────────────────────────────────────────────────────────┘

        ▲
        │
        └─── FEEDBACK LOOP: Stored data enhances future interactions
```

---

## 2. CONTEXT WINDOW MANAGEMENT - ANTI-OVERLOAD STRATEGIES

```
┌───────────────────────────────────────────────────────────────────────┐
│           CONTEXT WINDOW OVERFLOW PREVENTION SYSTEM                   │
└───────────────────────────────────────────────────────────────────────┘

Problem: As agent accumulates memory, context can exceed LLM limits

Solution: Multi-Layer Caching + Adaptive Summarization

┌─────────────────────────────────────────────────────────────────────┐
│  STRATEGY 1: HIERARCHICAL MEMORY STORAGE                            │
│                                                                       │
│  Level 0: Raw Episodes (Full Detail)                                │
│  ├─ Storage: Vector DB                                              │
│  ├─ Size: Original token count                                      │
│  ├─ Access: Only when absolutely needed                             │
│  └─ Example: 2000 tokens                                            │
│                                                                       │
│  Level 1: Compressed Summaries (5:1 ratio)                          │
│  ├─ Storage: Vector DB + Cache                                      │
│  ├─ Size: ~20% of original                                          │
│  ├─ Access: Default for episodic retrieval                          │
│  └─ Example: 400 tokens                                             │
│                                                                       │
│  Level 2: Micro Summaries (25:1 ratio)                              │
│  ├─ Storage: Fast cache (Redis)                                     │
│  ├─ Size: ~4% of original                                           │
│  ├─ Access: Quick overview/scanning                                 │
│  └─ Example: 80 tokens                                              │
│                                                                       │
│  Level 3: Extracted Facts (Structured)                              │
│  ├─ Storage: PostgreSQL                                             │
│  ├─ Size: Minimal (key-value pairs)                                 │
│  ├─ Access: Always included                                         │
│  └─ Example: 50 tokens                                              │
│                                                                       │
│  Adaptive Selection:                                                 │
│  • High relevance (>0.9) → Use Level 1 (compressed)                │
│  • Medium relevance (0.7-0.9) → Use Level 2 (micro)                │
│  • Low relevance (<0.7) → Use Level 3 (facts only)                 │
└─────────────────────────────────────────────────────────────────────┘

┌─────────────────────────────────────────────────────────────────────┐
│  STRATEGY 2: DYNAMIC TOKEN BUDGET ALLOCATION                        │
│                                                                       │
│  Token Budget Manager monitors and adjusts in real-time:            │
│                                                                       │
│  Initial Budget Distribution:                                        │
│  ┌──────────────────────────────────────────────┐                  │
│  │ Component           │ Tokens  │ % of Total   │                  │
│  ├──────────────────────────────────────────────┤                  │
│  │ System Prompt       │ 500     │ 6%           │ Fixed            │
│  │ User Message        │ 200     │ 2%           │ Variable         │
│  │ Working Memory      │ 2000    │ 24%          │ Elastic          │
│  │ Episodic Retrieval  │ 2000    │ 24%          │ Elastic          │
│  │ Semantic Facts      │ 500     │ 6%           │ Elastic          │
│  │ Tool Schemas        │ 1000    │ 12%          │ Semi-fixed       │
│  │ Response Buffer     │ 2000    │ 24%          │ Reserved         │
│  │ Safety Margin       │ 192     │ 2%           │ Buffer           │
│  └──────────────────────────────────────────────┘                  │
│                                                                       │
│  Adaptive Reallocation (when user message is large):                │
│  IF user_message > 500 tokens:                                      │
│     • Reduce episodic retrieval: 2000 → 1500 (-500)                │
│     • Reduce working memory: 2000 → 1700 (-300)                    │
│     • Allocate to user message: +800                                │
│                                                                       │
│  IF tools_needed:                                                    │
│     • Increase tool schemas budget                                  │
│     • Reduce episodic retrieval proportionally                      │
└─────────────────────────────────────────────────────────────────────┘

┌─────────────────────────────────────────────────────────────────────┐
│  STRATEGY 3: PROGRESSIVE CONTEXT LOADING (Lazy Loading)             │
│                                                                       │
│  Instead of loading everything upfront, load incrementally:         │
│                                                                       │
│  Phase 1: Minimal Context (Initial LLM Call)                        │
│  ├─ Working memory: Last 10 messages only                           │
│  ├─ Facts: Critical user preferences only                           │
│  ├─ Tools: Most relevant 3 tools                                    │
│  └─ Total: ~1500 tokens                                             │
│                                                                       │
│  Phase 2: Expansion (If LLM needs more context)                     │
│  ├─ LLM signals: "Need more information about X"                    │
│  ├─ System retrieves specific memories about X                      │
│  ├─ Add to context window                                           │
│  └─ Re-invoke LLM with expanded context                             │
│                                                                       │
│  Phase 3: Tool Execution Context                                    │
│  ├─ Load only tool-specific context                                 │
│  ├─ Discard irrelevant memories                                     │
│  └─ Focus budget on tool parameters                                 │
│                                                                       │
│  Benefits:                                                           │
│  • Start fast with minimal context                                  │
│  • Only load what's needed                                          │
│  • Adaptive to complexity                                           │
└─────────────────────────────────────────────────────────────────────┘

┌─────────────────────────────────────────────────────────────────────┐
│  STRATEGY 4: MEMORY IMPORTANCE SCORING & PRUNING                    │
│                                                                       │
│  Calculate importance score for each memory:                        │
│                                                                       │
│  Importance = w1×Recency + w2×Frequency + w3×Relevance + w4×Impact │
│                                                                       │
│  Where:                                                              │
│  • Recency (0-1): How recent is this memory?                        │
│    - Last 24h: 1.0                                                  │
│    - Last week: 0.7                                                 │
│    - Last month: 0.4                                                │
│    - Older: exponential decay                                       │
│                                                                       │
│  • Frequency (0-1): How often referenced?                           │
│    - Referenced 10+ times: 1.0                                      │
│    - 5-9 times: 0.7                                                 │
│    - 1-4 times: 0.3                                                 │
│    - Never: 0.0                                                     │
│                                                                       │
│  • Relevance (0-1): Semantic similarity to current query            │
│    - Cosine similarity of embeddings                                │
│                                                                       │
│  • Impact (0-1): Did this lead to successful outcome?               │
│    - User satisfaction signal                                       │
│    - Task completion rate                                           │
│                                                                       │
│  Pruning Strategy:                                                   │
│  1. Sort memories by importance score                               │
│  2. Include top-K until token budget exhausted                      │
│  3. Always include: working memory + critical facts                 │
│  4. Diversify: avoid redundant similar memories                     │
└─────────────────────────────────────────────────────────────────────┘

┌─────────────────────────────────────────────────────────────────────┐
│  STRATEGY 5: CONTEXT COMPRESSION TECHNIQUES                         │
│                                                                       │
│  A. Entity Resolution & Deduplication                               │
│     • Merge repeated mentions of same entity                        │
│     • "John Smith" = "John" = "Mr. Smith" = "he"                    │
│     • Replace with entity ID, expand only when needed               │
│                                                                       │
│  B. Conversation Summarization (LLM-based)                          │
│     Original (300 tokens):                                          │
│     "User asked about weather in Tokyo. Agent provided...           │
│      User then asked about restaurants... Agent suggested..."       │
│                                                                       │
│     Compressed (50 tokens):                                         │
│     "User planned Tokyo trip: checked weather, found restaurants,   │
│      booked hotel for March 15-20."                                 │
│                                                                       │
│  C. Template-Based Compression                                      │
│     For common patterns, use templates:                             │
│     • "User checked balance" instead of full conversation           │
│     • Store just the result: balance=$5,432.10                      │
│                                                                       │
│  D. Factual Extraction                                              │
│     From conversation → Extract facts → Store compactly             │
│     "I prefer vegetarian restaurants" → fact: diet=vegetarian       │
└─────────────────────────────────────────────────────────────────────┘
```

---

## 3. INTENT COMPLEXITY DETERMINATION - DETAILED BREAKDOWN

```
┌───────────────────────────────────────────────────────────────────────┐
│              COMPLEXITY SCORING ALGORITHM                             │
└───────────────────────────────────────────────────────────────────────┘

Input: User message + AgentContext
Output: Complexity score (0.0 - 1.0) + Routing decision

┌─────────────────────────────────────────────────────────────────────┐
│  DIMENSION 1: INTENT INDICATORS (0-1 score)                          │
│                                                                       │
│  Keywords Analysis:                                                  │
│                                                                       │
│  Simple Indicators (reduce complexity):                              │
│  ├─ Question words: "what", "who", "when", "where", "why"          │
│  ├─ Explanation requests: "explain", "describe", "tell me about"    │
│  ├─ Greetings: "hello", "hi", "how are you"                         │
│  └─ Impact: -0.3 per indicator                                      │
│                                                                       │
│  Complex Indicators (increase complexity):                           │
│  ├─ Multi-step words: "and then", "after that", "first...then"     │
│  ├─ Conditional: "if", "when", "in case", "depending on"           │
│  ├─ Multiple entities: > 2 distinct nouns                           │
│  ├─ Action verbs: "transfer", "create", "analyze", "compare"        │
│  └─ Impact: +0.2 per indicator                                      │
│                                                                       │
│  Examples:                                                           │
│  • "What is AI?" → Simple indicators: +1, Complex: 0 → 0.2         │
│  • "Transfer money and notify me" → Complex: +2 → 0.8              │
└─────────────────────────────────────────────────────────────────────┘

┌─────────────────────────────────────────────────────────────────────┐
│  DIMENSION 2: TOOL REQUIREMENT ANALYSIS (0-1 score)                  │
│                                                                       │
│  0.0 - No tools needed (purely conversational)                       │
│    • General knowledge questions                                     │
│    • Explanations, definitions                                       │
│    • Casual conversation                                             │
│                                                                       │
│  0.3 - Single tool, single call                                      │
│    • "Check my balance"                                              │
│    • "What's the weather?"                                           │
│    • "Search for X"                                                  │
│                                                                       │
│  0.6 - Single tool, multiple calls OR multiple tools sequentially    │
│    • "Check balance then transfer"                                   │
│    • "Search and summarize"                                          │
│                                                                       │
│  0.9 - Multiple tools with dependencies + conditional logic          │
│    • "If balance > $1000, transfer $500 and notify me"              │
│    • "Book flight, hotel, and car rental"                            │
│    • "Analyze sales data and create report"                          │
│                                                                       │
│  Detection Method:                                                   │
│  1. Match intent against tool capabilities                           │
│  2. Count potential tool invocations                                 │
│  3. Detect dependencies (if-then, sequential)                        │
└─────────────────────────────────────────────────────────────────────┘

┌─────────────────────────────────────────────────────────────────────┐
│  DIMENSION 3: DOMAIN COMPLEXITY (0-1 score)                          │
│                                                                       │
│  Simple Domains (0.2):                                               │
│  • General knowledge                                                 │
│  • Weather, time, simple facts                                       │
│                                                                       │
│  Medium Domains (0.5):                                               │
│  • E-commerce (product search, ordering)                             │
│  • Information retrieval                                             │
│  • Basic CRUD operations                                             │
│                                                                       │
│  Complex Domains (0.8):                                              │
│  • Banking (requires auth, validation, compliance)                   │
│  • Healthcare (sensitive, regulated)                                 │
│  • Legal (requires accuracy, citations)                              │
│  • Multi-domain (crosses multiple systems)                           │
└─────────────────────────────────────────────────────────────────────┘

┌─────────────────────────────────────────────────────────────────────┐
│  DIMENSION 4: STATE DEPENDENCIES (0-1 score)                         │
│                                                                       │
│  Stateless (0.1):                                                    │
│  • Self-contained request                                            │
│  • No reference to prior context                                     │
│  • Example: "What is 2+2?"                                           │
│                                                                       │
│  Context-Dependent (0.5):                                            │
│  • Refers to conversation history                                    │
│  • Uses pronouns ("it", "that", "them")                             │
│  • Example: "Tell me more about it"                                  │
│                                                                       │
│  Multi-Turn Workflow (0.9):                                          │
│  • Requires multiple back-and-forth                                  │
│  • Form filling, clarifications                                      │
│  • Example: "Book me a flight" (needs dates, destination, etc.)      │
└─────────────────────────────────────────────────────────────────────┘

┌─────────────────────────────────────────────────────────────────────┐
│  FINAL COMPLEXITY CALCULATION                                        │
│                                                                       │
│  Weighted Average:                                                   │
│                                                                       │
│  Complexity = w1×IntentScore +                                       │
│               w2×ToolScore +                                         │
│               w3×DomainScore +                                       │
│               w4×StateScore                                          │
│                                                                       │
│  Default Weights:                                                    │
│  w1 = 0.3 (Intent indicators)                                        │
│  w2 = 0.4 (Tool requirement) ← Highest weight                       │
│  w3 = 0.2 (Domain complexity)                                        │
│  w4 = 0.1 (State dependencies)                                       │
│                                                                       │
│  Thresholds:                                                         │
│  • 0.0 - 0.3: SIMPLE → Direct LLM                                   │
│  • 0.3 - 0.7: MEDIUM → Single tool execution                        │
│  • 0.7 - 1.0: COMPLEX → Task decomposition                          │
└─────────────────────────────────────────────────────────────────────┘
```

---

## 4. PREDEFINED FLOW MATCHING - FAST PATH

```
┌───────────────────────────────────────────────────────────────────────┐
│              PREDEFINED FLOW REPOSITORY STRUCTURE                     │
└───────────────────────────────────────────────────────────────────────┘

Flow Repository Design:

FlowDefinition {
    flowId: String
    intentPattern: IntentPattern
    domain: String
    taskTemplate: TaskTemplate (pre-built DAG)
    parameters: List<ParameterDefinition>
    slaTarget: Duration
    successRate: Float (from historical data)
    usageCount: Integer (popularity)
}

┌─────────────────────────────────────────────────────────────────────┐
│  MATCHING STRATEGIES (in order of speed)                             │
│                                                                       │
│  1. Exact Intent Match (Fastest - Hash lookup)                       │
│     ├─ Intent classifier outputs: "check_balance"                    │
│     ├─ Hash map: intentId → FlowDefinition                          │
│     ├─ Lookup: O(1)                                                  │
│     └─ Latency: < 1ms                                                │
│                                                                       │
│  2. Pattern Regex Match (Fast - Compiled regex)                      │
│     ├─ Patterns stored as compiled regex                            │
│     ├─ Example: "transfer.*(?:money|funds).*to.*"                   │
│     ├─ Match against user message                                    │
│     └─ Latency: < 5ms                                                │
│                                                                       │
│  3. Semantic Similarity (Medium - Vector comparison)                 │
│     ├─ Embed user message: vector[768]                              │
│     ├─ Compare with flow embeddings (pre-computed)                  │
│     ├─ Cosine similarity > 0.85 → Match                             │
│     └─ Latency: ~10-20ms                                             │
│                                                                       │
│  4. LLM Classification (Slowest - Fallback)                          │
│     ├─ Ask LLM: "Which flow matches this intent?"                   │
│     ├─ Provide flow descriptions                                    │
│     └─ Latency: ~200-500ms                                           │
└─────────────────────────────────────────────────────────────────────┘

┌─────────────────────────────────────────────────────────────────────┐
│  EXAMPLE PREDEFINED FLOWS (Banking Domain)                           │
│                                                                       │
│  Flow 1: CHECK_BALANCE                                               │
│  ├─ Intent Patterns:                                                 │
│  │   • "check (my )?balance"                                        │
│  │   • "how much (money )?(do I|have)"                              │
│  │   • "account balance"                                            │
│  ├─ Steps (DAG):                                                     │
│  │   [1] Authenticate user                                          │
│  │   [2] Query account balance (Tool: banking.getBalance)           │
│  │   [3] Format response                                            │
│  ├─ Parameters:                                                      │
│  │   • accountType: Optional (checking|savings), default: checking  │
│  ├─ SLA: < 500ms                                                     │
│  └─ Success Rate: 99.2%                                              │
│                                                                       │
│  Flow 2: TRANSFER_MONEY                                              │
│  ├─ Intent Patterns:                                                 │
│  │   • "transfer.*(?:money|funds|\\$\\d+)"                          │
│  │   • "send.*money"                                                │
│  │   • "pay.*from.*to"                                              │
│  ├─ Steps (DAG):                                                     │
│  │   [1] Authenticate user                                          │
│  │   [2] Validate source account (Tool: banking.getBalance)         │
│  │   [3] Validate destination                                       │
│  │   [4] Check sufficient funds                                     │
│  │   [5] Execute transfer (Tool: banking.transfer)                  │
│  │   [6] Send confirmation (Tool: notification.send)                │
│  ├─ Parameters:                                                      │
│  │   • amount: Required (extracted from message)                    │
│  │   • fromAccount: Optional (default: primary checking)            │
│  │   • toAccount: Required (extracted or ask user)                  │
│  ├─ SLA: < 2s                                                        │
│  └─ Success Rate: 97.8%                                              │
│                                                                       │
│  Flow 3: ORDER_FOOD                                                  │
│  ├─ Intent Patterns:                                                 │
│  │   • "order.*(?:food|lunch|dinner)"                               │
│  │   • "I('m| am) hungry"                                           │
│  │   • "food delivery"                                              │
│  ├─ Steps (DAG):                                                     │
│  │   [1] Get user location                                          │
│  │   [2] Search nearby restaurants (Tool: restaurant.search)        │
│  │   [3] Present options                                            │
│  │   [4] Get user selection                                         │
│  │   [5] Fetch menu (Tool: restaurant.getMenu)                      │
│  │   [6] Build order                                                │
│  │   [7] Calculate total                                            │
│  │   [8] Process payment                                            │
│  │   [9] Submit order (Tool: restaurant.createOrder)                │
│  ├─ Parameters:                                                      │
│  │   • cuisine: Optional (Italian, Chinese, etc.)                   │
│  │   • maxPrice: Optional                                           │
│  ├─ SLA: < 1.5s (excluding user interaction)                        │
│  └─ Success Rate: 94.5%                                              │
└─────────────────────────────────────────────────────────────────────┘

┌─────────────────────────────────────────────────────────────────────┐
│  FLOW EVOLUTION & LEARNING                                           │
│                                                                       │
│  Automatic Flow Creation:                                            │
│                                                                       │
│  IF an intent pattern is seen frequently (>10 times/week)            │
│     AND success rate via dynamic routing > 90%                       │
│     AND execution follows similar DAG structure                      │
│  THEN:                                                               │
│     1. Extract common pattern                                        │
│     2. Create flow template automatically                            │
│     3. Add to repository                                             │
│     4. Monitor performance                                           │
│     5. Promote to production if successful                           │
│                                                                       │
│  Flow Refinement:                                                    │
│  • Track execution metrics per flow                                  │
│  • A/B test variations                                               │
│  • Update based on failures/edge cases                               │
│  • Retire low-usage flows (< 1 use/month)                           │
└─────────────────────────────────────────────────────────────────────┘
```

This is your complete enhanced data flow! Want me to elaborate on any specific part? 🎯

