package io.agentic.microagent.framework.core;

import io.agentic.microagent.framework.core.context.AgentContext;
import io.agentic.microagent.framework.core.brain.AgentDecision;
import io.agentic.microagent.framework.core.model.AgentRequest;
import io.agentic.microagent.framework.core.brain.Intent;
import io.agentic.microagent.framework.shared.model.MemorySnapshot;

/**
 * Reasoning Engine - Analyzes request and makes decisions
 *
 * Responsibilities:
 * - Analyze user intent from request
 * - Reason about the best approach
 * - Make decisions on how to respond
 */
public interface ReasoningEngine {

	/**
	 * Analyze the request to detect user intent
	 *
	 * @param request The agent request
	 * @param context The agent context
	 * @param memory  The memory snapshot
	 * @return The detected intent
	 */
	Intent analyze(AgentRequest request, AgentContext context, MemorySnapshot memory);

	/**
	 * Make a decision on how to respond
	 *
	 * @param intent  The detected intent
	 * @param context The agent context
	 * @param memory  The memory snapshot
	 * @return The agent decision
	 */
	AgentDecision decide(Intent intent, AgentContext context, MemorySnapshot memory);

}
