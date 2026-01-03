package io.agentic.microagent.framework.core.planning;

import java.util.List;

import lombok.Builder;
import lombok.Data;

/**
 * Execution plan created by planning engine
 */
@Data
@Builder
public class ExecutionPlan {

	private String planId;

	private List<ExecutionStep> steps;

	private PlanStrategy strategy;

	private Float estimatedComplexity;

	public enum PlanStrategy {

		DIRECT, // Direct LLM response
		SINGLE_TOOL, // Use one tool
		SEQUENTIAL, // Multiple steps in sequence
		PARALLEL // Multiple steps in parallel

	}

}
