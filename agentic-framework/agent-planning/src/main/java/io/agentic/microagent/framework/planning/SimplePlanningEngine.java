package io.agentic.microagent.framework.planning;

import java.util.HashMap;
import java.util.UUID;

import org.springframework.stereotype.Component;

import io.agentic.microagent.framework.shared.brain.context.AgentContext;
import io.agentic.microagent.framework.shared.brain.reasoning.AgentDecision;
import io.agentic.microagent.framework.shared.brain.planning.ExecutionPlan;
import io.agentic.microagent.framework.shared.brain.planning.ExecutionStep;
import io.agentic.microagent.framework.shared.brain.reasoning.Intent;
import lombok.extern.slf4j.Slf4j;

/**
 * Simple implementation of PlanningEngine
 *
 * This is a basic implementation that creates simple plans.
 * In production, this should use:
 * - LLM for plan generation
 * - Task decomposition algorithms
 * - Dependency analysis
 */
@Slf4j
public class SimplePlanningEngine implements PlanningEngine {

	@Override
	public ExecutionPlan plan(Intent intent, AgentDecision decision, AgentContext context) {
		log.debug("Creating execution plan for decision type: {}", decision.getType());

		ExecutionPlan.PlanStrategy strategy = determineStrategy(decision);

		// Create a simple single-step plan
		ExecutionStep step = ExecutionStep.builder()
			.stepId(UUID.randomUUID().toString())
			.description("Generate response based on intent")
			.type(ExecutionStep.StepType.LLM_CALL)
			.parameters(new HashMap<>())
			.order(1)
			.build();

		return ExecutionPlan.builder()
			.planId(UUID.randomUUID().toString())
			.steps(java.util.List.of(step))
			.strategy(strategy)
			.estimatedComplexity(0.3f)
			.build();
	}

	private ExecutionPlan.PlanStrategy determineStrategy(AgentDecision decision) {
		return switch (decision.getType()) {
			case DIRECT_RESPONSE -> ExecutionPlan.PlanStrategy.DIRECT;
			case TOOL_EXECUTION -> ExecutionPlan.PlanStrategy.SINGLE_TOOL;
			case MULTI_STEP_PLAN -> ExecutionPlan.PlanStrategy.SEQUENTIAL;
			default -> ExecutionPlan.PlanStrategy.DIRECT;
		};
	}

}
