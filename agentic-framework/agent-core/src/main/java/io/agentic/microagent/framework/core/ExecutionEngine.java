package io.agentic.microagent.framework.core;

import io.agentic.microagent.framework.core.context.AgentContext;
import io.agentic.microagent.framework.core.planning.ExecutionPlan;
import io.agentic.microagent.framework.core.planning.ExecutionResult;

/**
 * Execution Engine - Executes plans
 *
 * Responsibilities:
 * - Execute the steps in the execution plan
 * - Handle tool invocations
 * - Manage execution flow (sequential, parallel)
 * - Handle errors and retries
 */
public interface ExecutionEngine {

	/**
	 * Execute the execution plan
	 *
	 * @param plan    The execution plan
	 * @param context The agent context
	 * @return The execution result
	 */
	ExecutionResult execute(ExecutionPlan plan, AgentContext context);

}
