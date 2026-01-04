# AI Agent Architecture - Mermaid Diagrams

## 1. Component Architecture Diagram

```mermaid
graph TB
    subgraph "MicroAgent Platform"
        subgraph "Orchestration Layer"
            BRAIN[Agent Brain Service<br/>Orchestrator]
        end

        subgraph "Infrastructure Services"
            REGISTRY[Agent Registry Service<br/>Agent Discovery]
            POLICY[Agent Policy Service<br/>Governance]
        end

        subgraph "Specialist Agents - Demo"
            USER_AGENT[Agent User Service<br/>User Management]
            ORDER_AGENT[Agent Order Service<br/>Order Management]
            PAYMENT_AGENT[Agent Payment Service<br/>Payment Processing]
        end

        subgraph "Shared Infrastructure"
            subgraph "Memory System"
                REDIS[Redis<br/>Working Memory]
                VECTOR[Vector DB<br/>Episodic Memory]
                POSTGRES[PostgreSQL<br/>Semantic Memory & Policies]
            end

            subgraph "Event System"
                KAFKA[Kafka<br/>Event Stream]
            end

            subgraph "Tool System - MCP"
                MCP[MCP Servers<br/>External Integrations]
            end
        end

        subgraph "Communication Layer"
            API_GW[API Gateway<br/>REST/WebSocket]
        end
    end

    USER[User Requests] --> API_GW
    API_GW --> BRAIN

    BRAIN --> REGISTRY
    BRAIN --> POLICY
    BRAIN --> USER_AGENT
    BRAIN --> ORDER_AGENT
    BRAIN --> PAYMENT_AGENT

    USER_AGENT --> REDIS
    ORDER_AGENT --> REDIS
    PAYMENT_AGENT --> REDIS

    USER_AGENT --> POSTGRES
    ORDER_AGENT --> POSTGRES
    PAYMENT_AGENT --> POSTGRES

    BRAIN --> VECTOR
    REGISTRY --> POSTGRES
    POLICY --> POSTGRES

    USER_AGENT --> KAFKA
    ORDER_AGENT --> KAFKA
    PAYMENT_AGENT --> KAFKA

    USER_AGENT --> MCP
    ORDER_AGENT --> MCP
    PAYMENT_AGENT --> MCP
```

## 2. Data Flow Sequence Diagram

```mermaid
sequenceDiagram
    participant User
    participant Brain as Agent Brain
    participant Registry as Agent Registry
    participant Policy as Policy Service
    participant UserAgent as Agent User
    participant OrderAgent as Agent Order
    participant PaymentAgent as Agent Payment
    participant Memory as Memory System
    participant MCP as MCP Servers
    participant Audit as Audit Trail

    User->>Brain: Send Request
    Brain->>Brain: Analyze & Decompose Task

    Brain->>Registry: Discover Required Agents
    Registry-->>Brain: Agent Profiles (User, Order, Payment)

    Brain->>Policy: Load Policies for Agents
    Policy-->>Brain: Policy Rules

    Brain->>Policy: PRE-REQUEST Validation
    Policy->>Policy: Check Compliance

    alt Violation Detected
        Policy->>Audit: Log Violation
        Policy-->>Brain: BLOCK Response
        Brain-->>User: Error Response
    else Request Valid
        Brain->>Memory: Retrieve Context
        Memory-->>Brain: User History & Context

        par Execute Specialist Agent Tasks
            Brain->>UserAgent: Task 1: Verify User
            activate UserAgent
            UserAgent->>Memory: Get User Data
            UserAgent->>MCP: Call Auth Service
            MCP-->>UserAgent: Auth Result
            UserAgent-->>Brain: Task 1 Complete
            deactivate UserAgent

            Brain->>OrderAgent: Task 2: Create Order
            activate OrderAgent
            OrderAgent->>Memory: Get Order Data
            OrderAgent->>MCP: Call Inventory System
            MCP-->>OrderAgent: Inventory Result
            OrderAgent-->>Brain: Task 2 Complete
            deactivate OrderAgent

            Brain->>PaymentAgent: Task 3: Process Payment
            activate PaymentAgent
            PaymentAgent->>MCP: Call Payment Gateway
            MCP-->>PaymentAgent: Payment Result
            PaymentAgent-->>Brain: Task 3 Complete
            deactivate PaymentAgent
        end

        Brain->>Brain: Aggregate Results

        Brain->>Policy: POST-RESPONSE Validation
        Policy->>Policy: Check Response Compliance

        alt Response Violation
            Policy->>Audit: Log Violation
            Policy->>Policy: Apply Remediation
        end

        Policy->>Audit: Log All Policy Checks
        Policy-->>Brain: Validated Response

        Brain->>Memory: Store Conversation
        Brain->>Registry: Record Metrics

        Brain-->>User: Final Response
    end
```

## 3. Memory System Architecture

```mermaid
graph LR
    subgraph "Memory Hierarchy"
        subgraph "Short-Term"
            WM[Working Memory<br/>Redis<br/>Last 50 messages]
        end
        
        subgraph "Long-Term"
            EM[Episodic Memory<br/>Vector DB<br/>Past conversations]
            SM[Semantic Memory<br/>PostgreSQL<br/>Facts & Knowledge]
            PM[Procedural Memory<br/>Code/Templates<br/>How-to knowledge]
        end
    end
    
    REQUEST[User Request] --> WM
    WM --> CONSOLIDATE{Consolidation<br/>Process}
    CONSOLIDATE -->|Important| EM
    CONSOLIDATE -->|Facts| SM
    CONSOLIDATE -->|Patterns| PM
    
    EM --> RETRIEVE[Semantic Search]
    SM --> RETRIEVE
    PM --> RETRIEVE
    RETRIEVE --> CONTEXT[Agent Context]
```

## 4. Tool/MCP Integration Architecture

```mermaid
graph TB
    subgraph "Specialist Agents"
        USER_AGENT[Agent User]
        ORDER_AGENT[Agent Order]
        PAYMENT_AGENT[Agent Payment]
    end

    subgraph "Tool System"
        TR[Tool Registry]

        subgraph "Domain-Specific Tools"
            subgraph "User Tools"
                UT1[Auth Service]
                UT2[Profile Manager]
                UT3[User Validator]
            end

            subgraph "Order Tools"
                OT1[Inventory System]
                OT2[Cart Manager]
                OT3[Order Processor]
            end

            subgraph "Payment Tools"
                PT1[Payment Gateway]
                PT2[Billing Service]
                PT3[Refund Processor]
            end
        end

        subgraph "MCP Servers"
            MCP1[Email MCP]
            MCP2[SMS MCP]
            MCP3[Analytics MCP]
        end

        TR --> UT1
        TR --> UT2
        TR --> UT3
        TR --> OT1
        TR --> OT2
        TR --> OT3
        TR --> PT1
        TR --> PT2
        TR --> PT3
        TR --> MCP1
        TR --> MCP2
        TR --> MCP3
    end

    USER_AGENT -->|Discover & Execute| TR
    ORDER_AGENT -->|Discover & Execute| TR
    PAYMENT_AGENT -->|Discover & Execute| TR
```

## 5. Agent Brain Orchestration Flow

```mermaid
flowchart TD
    START[User Request] --> BRAIN[Agent Brain Receives Request]
    BRAIN --> ANALYZE[Analyze Request Intent]

    ANALYZE --> COMPLEX{Complex<br/>Multi-Step Task?}

    COMPLEX -->|Yes| DECOMPOSE[Task Decomposition]
    COMPLEX -->|No| SIMPLE[Single Agent Task]

    DECOMPOSE --> IDENTIFY[Identify Required Agents]
    IDENTIFY --> DISCOVER[Discover from Registry]

    DISCOVER --> AGENTS{Which Agents?}

    AGENTS -->|User Operations| USER_AGENT[Agent User]
    AGENTS -->|Order Operations| ORDER_AGENT[Agent Order]
    AGENTS -->|Payment Operations| PAYMENT_AGENT[Agent Payment]

    SIMPLE --> AGENTS

    USER_AGENT --> EXECUTE_USER[Execute User Tasks]
    ORDER_AGENT --> EXECUTE_ORDER[Execute Order Tasks]
    PAYMENT_AGENT --> EXECUTE_PAY[Execute Payment Tasks]

    EXECUTE_USER --> CHECK{All Tasks<br/>Complete?}
    EXECUTE_ORDER --> CHECK
    EXECUTE_PAY --> CHECK

    CHECK -->|No| NEXT[Next Task in Sequence]
    NEXT --> AGENTS

    CHECK -->|Yes| AGGREGATE[Aggregate Results]

    AGGREGATE --> VALIDATE[Validate Against Policies]

    VALIDATE --> SUCCESS{Success?}

    SUCCESS -->|Yes| SYNTHESIZE[Synthesize Final Response]
    SUCCESS -->|No, Recoverable| RETRY[Retry Failed Tasks]
    SUCCESS -->|No, Fatal| ERROR[Error Response]

    RETRY --> AGENTS

    SYNTHESIZE --> RESPONSE[Final Response to User]
    ERROR --> RESPONSE
```

## 6. Communication Protocol Options

```mermaid
graph LR
    subgraph "Client Applications"
        WEB[Web App]
        MOBILE[Mobile App]
        SERVICE[Microservice]
    end
    
    subgraph "Communication Protocols"
        REST[REST API<br/>Request/Response]
        WS[WebSocket<br/>Streaming]
        KAFKA[Kafka<br/>Events]
        GRPC[gRPC<br/>Service-to-Service]
    end
    
    subgraph "Agent System"
        ROUTER[Request Router]
        AGENT[Agent Brain]
    end
    
    WEB --> REST
    WEB --> WS
    MOBILE --> REST
    MOBILE --> WS
    SERVICE --> KAFKA
    SERVICE --> GRPC
    
    REST --> ROUTER
    WS --> ROUTER
    KAFKA --> ROUTER
    GRPC --> ROUTER
    ROUTER --> AGENT
```

## 7. Context Building Flow

```mermaid
flowchart TD
    REQUEST[Agent Request] --> CB[Context Builder]
    
    CB --> USER[Build User Context]
    CB --> ENV[Build Environment Context]
    CB --> DOMAIN[Build Domain Context]
    CB --> TECH[Build Technical Context]
    
    USER --> UP[User Profile]
    USER --> UH[User History]
    USER --> UPERM[Permissions]
    
    ENV --> TIME[Timestamp]
    ENV --> LOC[Location]
    ENV --> DEVICE[Device Info]
    
    DOMAIN --> ENTITIES[Domain Entities]
    DOMAIN --> CONSTRAINTS[Domain Constraints]
    
    TECH --> TOOLS[Available Tools]
    TECH --> LIMITS[Rate Limits]
    
    UP --> MERGE[Merge All Context]
    UH --> MERGE
    UPERM --> MERGE
    TIME --> MERGE
    LOC --> MERGE
    DEVICE --> MERGE
    ENTITIES --> MERGE
    CONSTRAINTS --> MERGE
    TOOLS --> MERGE
    LIMITS --> MERGE
    
    MERGE --> ENRICHED[Enriched Agent Context]
```

## 8. Multi-Agent Collaboration (Microagent Pattern)

```mermaid
graph TB
    USER[User Request] --> BRAIN[Agent Brain<br/>Orchestrator]

    BRAIN --> DECOMPOSE{Task<br/>Decomposition}

    DECOMPOSE --> REGISTRY[Agent Registry<br/>Discover Agents]

    REGISTRY --> AGENTS{Available<br/>Specialist Agents}

    AGENTS -->|User Domain| USER_AGENT[Agent User<br/>User Management]
    AGENTS -->|Order Domain| ORDER_AGENT[Agent Order<br/>Order Management]
    AGENTS -->|Payment Domain| PAY_AGENT[Agent Payment<br/>Payment Processing]
    AGENTS -->|General Domain| GEN_AGENT[General Agent<br/>Fallback]

    USER_AGENT --> POL_USER[Load User<br/>Policies]
    ORDER_AGENT --> POL_ORDER[Load Order<br/>Policies]
    PAY_AGENT --> POL_PAY[Load Payment<br/>Policies]

    POL_USER --> EXEC_USER[Execute<br/>User Tasks]
    POL_ORDER --> EXEC_ORDER[Execute<br/>Order Tasks]
    POL_PAY --> EXEC_PAY[Execute<br/>Payment Tasks]

    EXEC_USER --> COORD[Agent Brain<br/>Coordination]
    EXEC_ORDER --> COORD
    EXEC_PAY --> COORD
    GEN_AGENT --> COORD

    COORD --> AGGREGATE[Result<br/>Aggregation]

    AGGREGATE --> POLICY_CHECK[POST-RESPONSE<br/>Policy Validation]

    POLICY_CHECK --> AUDIT[Audit Trail<br/>Logging]

    AUDIT --> METRICS[Record Metrics<br/>to Registry]

    METRICS --> RESPONSE[Final Response]

    RESPONSE --> USER
```

## 9. Observability & Monitoring

```mermaid
graph TD
    subgraph "Agent System"
        AGENT[Agent Brain]
        TOOL[Tool Executor]
        MEMORY[Memory Manager]
    end
    
    subgraph "Observability Layer"
        METRICS[Metrics Collector]
        LOGS[Structured Logging]
        TRACES[Distributed Tracing]
    end
    
    subgraph "Monitoring Stack"
        PROM[Prometheus]
        ELK[ELK Stack]
        JAEGER[Jaeger]
        GRAFANA[Grafana Dashboard]
    end
    
    AGENT --> METRICS
    AGENT --> LOGS
    AGENT --> TRACES
    
    TOOL --> METRICS
    TOOL --> LOGS
    TOOL --> TRACES
    
    MEMORY --> METRICS
    MEMORY --> LOGS
    
    METRICS --> PROM
    LOGS --> ELK
    TRACES --> JAEGER
    
    PROM --> GRAFANA
    ELK --> GRAFANA
    JAEGER --> GRAFANA
```

## 10. Deployment Architecture

```mermaid
graph TB
    subgraph "Kubernetes Cluster"
        subgraph "Microservices"
            subgraph "Orchestration Layer"
                BRAIN1[Agent Brain 1]
                BRAIN2[Agent Brain 2]
            end

            subgraph "Infrastructure Services"
                REG1[Registry Instance 1]
                REG2[Registry Instance 2]
                POL1[Policy Instance 1]
                POL2[Policy Instance 2]
            end

            subgraph "Specialist Agents"
                USER1[Agent User 1]
                USER2[Agent User 2]
                ORDER1[Agent Order 1]
                ORDER2[Agent Order 2]
                PAY1[Agent Payment 1]
                PAY2[Agent Payment 2]
            end
        end

        subgraph "Data Layer"
            REDIS[(Redis<br/>Working Memory)]
            POSTGRES[(PostgreSQL<br/>Policies & Data)]
            VECTOR[(Vector DB<br/>Episodic Memory)]
        end

        subgraph "Message Queue"
            KAFKA[(Kafka<br/>Event Stream)]
        end

        LB[Load Balancer]

        LB --> BRAIN1
        LB --> BRAIN2

        BRAIN1 --> REG1
        BRAIN2 --> REG2

        BRAIN1 --> POL1
        BRAIN2 --> POL2

        BRAIN1 --> USER1
        BRAIN1 --> ORDER1
        BRAIN1 --> PAY1
        BRAIN2 --> USER2
        BRAIN2 --> ORDER2
        BRAIN2 --> PAY2

        REG1 --> POSTGRES
        REG2 --> POSTGRES
        POL1 --> POSTGRES
        POL2 --> POSTGRES

        USER1 --> POSTGRES
        USER2 --> POSTGRES
        ORDER1 --> POSTGRES
        ORDER2 --> POSTGRES
        PAY1 --> POSTGRES
        PAY2 --> POSTGRES

        BRAIN1 --> REDIS
        BRAIN2 --> REDIS
        USER1 --> REDIS
        USER2 --> REDIS
        ORDER1 --> REDIS
        ORDER2 --> REDIS

        BRAIN1 --> VECTOR
        BRAIN2 --> VECTOR

        BRAIN1 --> KAFKA
        BRAIN2 --> KAFKA
        USER1 --> KAFKA
        USER2 --> KAFKA
        ORDER1 --> KAFKA
        ORDER2 --> KAFKA
        PAY1 --> KAFKA
        PAY2 --> KAFKA
    end

    EXTERNAL[External Services] --> LB
    MCP[MCP Servers] --> USER1
    MCP --> USER2
    MCP --> ORDER1
    MCP --> ORDER2
    MCP --> PAY1
    MCP --> PAY2
```

## 11. Agent Registry Architecture

```mermaid
graph TB
    subgraph "Agent Registry System"
        subgraph "API Layer"
            REGAPI[Registry REST API]
        end

        subgraph "Core Services"
            REG[Agent Registry Service]
            ROUTER[Agent Router]
            HEALTH[Health Monitor]
            METRICS[Metrics Collector]
        end

        subgraph "Agent Catalog"
            PROFILES[(Agent Profiles)]

            BANK[Banking Agent<br/>Profile]
            HEALTH_A[Healthcare Agent<br/>Profile]
            GEN[General Agent<br/>Profile]

            PROFILES --> BANK
            PROFILES --> HEALTH_A
            PROFILES --> GEN
        end

        subgraph "Operations"
            REGISTER[Register Agent]
            SELECT[Select Agent]
            DISCOVER[Discover Agents]
            LIFECYCLE[Lifecycle Mgmt]
        end
    end

    subgraph "Agent Instances"
        A1[Banking Agent]
        A2[Healthcare Agent]
        A3[General Agent]
    end

    USER[User Request] --> REGAPI
    REGAPI --> REG

    REG --> REGISTER
    REG --> SELECT
    REG --> DISCOVER
    REG --> LIFECYCLE

    REGISTER --> PROFILES
    SELECT --> PROFILES
    DISCOVER --> PROFILES
    LIFECYCLE --> PROFILES

    SELECT --> ROUTER
    ROUTER --> A1
    ROUTER --> A2
    ROUTER --> A3

    A1 --> METRICS
    A2 --> METRICS
    A3 --> METRICS

    A1 --> HEALTH
    A2 --> HEALTH
    A3 --> HEALTH

    HEALTH --> PROFILES
    METRICS --> PROFILES
```

## 12. Policy Governance Architecture

```mermaid
graph TB
    subgraph "Policy Governance System"
        subgraph "API Layer"
            POLAPI[Policy Management API]
        end

        subgraph "Core Services"
            POLMGMT[Policy Management Service]
            ENFORCE[Policy Enforcement Engine]
            TAGMGMT[Tag Management Service]
            AUDIT[Audit Trail Service]
        end

        subgraph "Policy Repository"
            POLICIES[(Policy Database<br/>PostgreSQL)]

            PII[PII Protection<br/>Policy]
            BRAND[Brand Guidelines<br/>Policy]
            ETHICS[Ethics<br/>Policy]

            POLICIES --> PII
            POLICIES --> BRAND
            POLICIES --> ETHICS
        end

        subgraph "Enforcement Phases"
            PRE[PRE-REQUEST<br/>Validation]
            DURING[DURING-GENERATION<br/>Prompt Injection]
            POST[POST-RESPONSE<br/>Validation]
        end

        subgraph "Actions"
            BLOCK[BLOCK]
            REDACT[REDACT]
            WARN[WARN]
        end
    end

    subgraph "Storage Strategy"
        SANITIZE[OWASP HTML<br/>Sanitizer]
        TEXTCOL[TEXT Columns<br/>for Markdown]
        JSONBCOL[JSONB Columns<br/>for Rules]
        FTS[Full-Text Search<br/>GIN Index]
    end

    REQUEST[Agent Request] --> ENFORCE

    ENFORCE --> PRE
    ENFORCE --> DURING
    ENFORCE --> POST

    PRE --> POLICIES
    DURING --> POLICIES
    POST --> POLICIES

    PRE --> BLOCK
    PRE --> WARN
    POST --> BLOCK
    POST --> REDACT
    POST --> WARN

    POLMGMT --> SANITIZE
    SANITIZE --> TEXTCOL
    SANITIZE --> JSONBCOL
    TEXTCOL --> POLICIES
    JSONBCOL --> POLICIES

    POLICIES --> FTS

    PRE --> AUDIT
    POST --> AUDIT

    POLAPI --> POLMGMT
    POLAPI --> TAGMGMT
```

## 13. Agent Brain Orchestration Architecture

```mermaid
graph TB
    subgraph "Agent Brain Orchestration System"
        subgraph "Orchestration Engine"
            BRAIN[Agent Brain Service]
            PLANNER[Task Planner]
            COORDINATOR[Agent Coordinator]
            MONITOR[Execution Monitor]
            AGGREGATOR[Result Aggregator]
        end

        subgraph "Supporting Services"
            REGISTRY[Agent Registry]
            POLICY[Policy Service]
        end

        subgraph "Specialist Agents"
            USER_AGENT[Agent User<br/>User Management]
            ORDER_AGENT[Agent Order<br/>Order Management]
            PAYMENT_AGENT[Agent Payment<br/>Payment Processing]
        end
    end

    REQUEST[User Request] --> BRAIN

    BRAIN --> PLANNER
    PLANNER --> COORDINATOR

    COORDINATOR --> REGISTRY
    REGISTRY --> COORDINATOR

    COORDINATOR --> POLICY
    POLICY --> COORDINATOR

    COORDINATOR --> USER_AGENT
    COORDINATOR --> ORDER_AGENT
    COORDINATOR --> PAYMENT_AGENT

    USER_AGENT --> AGGREGATOR
    ORDER_AGENT --> AGGREGATOR
    PAYMENT_AGENT --> AGGREGATOR

    AGGREGATOR --> MONITOR
    MONITOR --> RESPONSE[Response to User]
```

## 14. Brain + Registry + Policy + Specialist Agents Integration Flow

```mermaid
sequenceDiagram
    participant User
    participant Brain as Agent Brain
    participant Registry as Agent Registry
    participant Policy as Policy Service
    participant UserAgent as Agent User
    participant OrderAgent as Agent Order
    participant PaymentAgent as Agent Payment
    participant Audit as Audit Trail
    participant Metrics as Metrics Service

    User->>Brain: 1. Send Request
    Brain->>Brain: 2. Task Decomposition<br/>(break into sub-tasks)

    Brain->>Registry: 3. Discover Agents<br/>(User, Order, Payment)
    Registry-->>Brain: Agent Profiles

    Brain->>Policy: 4. Load Policies<br/>for all agents
    Policy-->>Brain: Agent Policies

    Brain->>Policy: 5. PRE-REQUEST Validation
    Policy->>Policy: Check Compliance

    alt Violation Detected
        Policy->>Audit: Log Violation
        Policy-->>Brain: BLOCK Response
        Brain-->>User: Error Response
    else Request Valid
        Brain->>UserAgent: 6. Execute Task 1<br/>(Verify User)
        UserAgent-->>Brain: Task 1 Result

        Brain->>OrderAgent: 7. Execute Task 2<br/>(Create Order)
        OrderAgent-->>Brain: Task 2 Result

        Brain->>PaymentAgent: 8. Execute Task 3<br/>(Process Payment)
        PaymentAgent-->>Brain: Task 3 Result

        Brain->>Brain: 9. Aggregate Results

        Brain->>Policy: 10. POST-RESPONSE Validation
        Policy->>Policy: Check Response

        alt Response Violation
            Policy->>Policy: Apply Remediation
            Policy->>Audit: Log Violation
        end

        Policy->>Audit: 11. Log All Checks
        Policy-->>Brain: Validated Response

        Brain->>Metrics: 12. Record Metrics
        Brain-->>User: 13. Final Response
    end
```

