package io.agentic.microagent.framework.core;

import io.agentic.microagent.framework.core.model.AgentRequest;
import io.agentic.microagent.framework.core.model.AgentResponse;

/**
 * AgentBrain - Main orchestrator for the AI Agent System
 *
 * This is the central component that coordinates:
 * - Context Engine: Builds context from request
 * - Reasoning Engine: Analyzes intent and creates reasoning
 * - Planning Engine: Creates execution plans
 * - Execution Engine: Executes the plan
 * - Memory Manager: Retrieves and stores memories
 */
public interface AgentBrain {

	/**
	 * Process an agent request and return a response
	 *
	 * @param request The incoming agent request
	 * @return The agent response
	 */
	AgentResponse process(AgentRequest request);

}
