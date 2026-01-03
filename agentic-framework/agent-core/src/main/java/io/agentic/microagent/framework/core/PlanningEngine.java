package io.agentic.microagent.framework.core;

import io.agentic.microagent.framework.core.context.AgentContext;
import io.agentic.microagent.framework.core.brain.AgentDecision;
import io.agentic.microagent.framework.core.planning.ExecutionPlan;
import io.agentic.microagent.framework.core.brain.Intent;

/**
 * Planning Engine - Creates execution plans
 * <p>
 * Responsibilities:
 * - Create execution plans based on intent and decision
 * - Determine execution strategy (direct, single-tool, sequential, parallel)
 * - Break down complex tasks into steps
 */
public interface PlanningEngine {

	/**
	 * Create an execution plan based on intent and decision
	 *
	 * @param intent   The detected intent
	 * @param decision The agent decision
	 * @param context  The agent context
	 * @return The execution plan
	 */
	ExecutionPlan plan(Intent intent, AgentDecision decision, AgentContext context);

}
