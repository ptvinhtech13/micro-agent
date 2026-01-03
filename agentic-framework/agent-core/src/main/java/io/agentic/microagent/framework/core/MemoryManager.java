package io.agentic.microagent.framework.core;

import io.agentic.microagent.framework.core.brain.AgentDecision;
import io.agentic.microagent.framework.core.brain.Intent;
import io.agentic.microagent.framework.core.context.AgentContext;
import io.agentic.microagent.framework.core.model.AgentRequest;
import io.agentic.microagent.framework.core.model.AgentResponse;
import io.agentic.microagent.framework.shared.model.MemorySnapshot;

/**
 * Reasoning Engine - Analyzes request and makes decisions
 *
 * Responsibilities:
 * - Analyze user intent from request
 * - Reason about the best approach
 * - Make decisions on how to respond
 */
public interface MemoryManager {


	void storeMemory(AgentRequest request, AgentResponse response, AgentContext context);

	MemorySnapshot retrieveMemory(String conversationId, AgentContext context);
}
