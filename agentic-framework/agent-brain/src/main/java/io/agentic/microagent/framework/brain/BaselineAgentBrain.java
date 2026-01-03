package io.agentic.microagent.framework.brain;

import io.agentic.microagent.framework.core.AgentBrain;
import io.agentic.microagent.framework.core.ContextEngine;
import io.agentic.microagent.framework.core.ExecutionEngine;
import io.agentic.microagent.framework.core.MemoryManager;
import io.agentic.microagent.framework.core.PlanningEngine;
import io.agentic.microagent.framework.core.ReasoningEngine;
import io.agentic.microagent.framework.core.brain.AgentDecision;
import io.agentic.microagent.framework.core.brain.Intent;
import io.agentic.microagent.framework.core.brain.ReasoningTrace;
import io.agentic.microagent.framework.core.context.AgentContext;
import io.agentic.microagent.framework.core.model.AgentRequest;
import io.agentic.microagent.framework.core.model.AgentResponse;
import io.agentic.microagent.framework.core.planning.ExecutionPlan;
import io.agentic.microagent.framework.core.planning.ExecutionResult;
import io.agentic.microagent.framework.shared.model.MemorySnapshot;
import io.agentic.microagent.framework.shared.model.ResponseMetadata;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Default implementation of AgentBrain
 * <p>
 * This is the main orchestrator that coordinates all engines:
 * 1. ContextEngine - builds context
 * 2. MemoryManager - retrieves memories
 * 3. ReasoningEngine - analyzes and decides
 * 4. PlanningEngine - creates execution plan
 * 5. ExecutionEngine - executes the plan
 * 6. MemoryManager - stores results
 */
@Slf4j
@RequiredArgsConstructor
public class BaselineAgentBrain implements AgentBrain {

	private final ContextEngine contextEngine;

	private final ReasoningEngine reasoningEngine;

	private final PlanningEngine planningEngine;

	private final ExecutionEngine executionEngine;

	private final MemoryManager memoryManager;

	@Override
	public AgentResponse process(AgentRequest request) {
		Instant startTime = Instant.now();
		log.info("Processing request for conversation: {}", request.getConversationId());

		try {
			// Step 1: Build context
			log.debug("Building context...");
			AgentContext context = contextEngine.buildContext(request);

			// Step 2: Retrieve memories
			log.debug("Retrieving memories...");
			MemorySnapshot memory = memoryManager.retrieveMemory(request.getConversationId(), context);

			// Step 3: Analyze intent and decide
			log.debug("Analyzing intent...");
			Intent intent = reasoningEngine.analyze(request, context, memory);

			log.debug("Making decision...");
			AgentDecision decision = reasoningEngine.decide(intent, context, memory);

			// Step 4: Create execution plan
			log.debug("Creating execution plan...");
			ExecutionPlan plan = planningEngine.plan(intent, decision, context);

			// Step 5: Execute the plan
			log.debug("Executing plan...");
			ExecutionResult result = executionEngine.execute(plan, context);

			// Step 6: Build response
			log.debug("Building response...");
			AgentResponse response = buildResponse(request, decision, result, startTime);

			// Step 7: Store in memory
			log.debug("Storing in memory...");
			memoryManager.storeMemory(request, response, context);

			log.info("Request processed successfully for conversation: {}", request.getConversationId());
			return response;

		} catch (Exception e) {
			log.error("Error processing request: {}", e.getMessage(), e);
			return buildErrorResponse(request, e, startTime);
		}
	}

	/**
	 * Build agent response from execution result
	 */
	private AgentResponse buildResponse(AgentRequest request, AgentDecision decision, ExecutionResult result,
			Instant startTime) {

		Duration latency = Duration.between(startTime, Instant.now());

		return AgentResponse.builder()
				.conversationId(request.getConversationId())
				.responseId(UUID.randomUUID()
						.toString())
				.content(result.getFinalOutput() != null
						? result.getFinalOutput()
								.toString()
						: "")
				.confidence(decision.getConfidence())
				.reasoning(buildReasoningTrace(decision))
				.toolsExecuted(new ArrayList<>()) // TODO: Extract from execution result
				.memoryUpdates(new ArrayList<>())
				.decision(decision)
				.metadata(ResponseMetadata.builder()
						.latency(latency)
						.modelUsed("claude-3-5-sonnet-20241022")
						.executionPath(decision.getType()
								.name())
						.build())
				.build();
	}

	/**
	 * Build reasoning trace from decision
	 */
	private ReasoningTrace buildReasoningTrace(AgentDecision decision) {
		return ReasoningTrace.builder()
				.steps(new ArrayList<>())
				.justification(decision.getReasoning())
				.build();
	}

	/**
	 * Build error response
	 */
	private AgentResponse buildErrorResponse(AgentRequest request, Exception error, Instant startTime) {
		Duration latency = Duration.between(startTime, Instant.now());

		return AgentResponse.builder()
				.conversationId(request.getConversationId())
				.responseId(UUID.randomUUID()
						.toString())
				.content("I apologize, but I encountered an error while processing your request: " + error.getMessage())
				.confidence(0.0f)
				.reasoning(ReasoningTrace.builder()
						.justification("Error occurred during processing")
						.steps(new ArrayList<>())
						.build())
				.toolsExecuted(new ArrayList<>())
				.memoryUpdates(new ArrayList<>())
				.metadata(ResponseMetadata.builder()
						.latency(latency)
						.build())
				.build();
	}

}
