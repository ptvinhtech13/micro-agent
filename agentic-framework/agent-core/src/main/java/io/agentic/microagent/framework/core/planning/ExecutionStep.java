package io.agentic.microagent.framework.core.planning;

import java.util.Map;
import lombok.Builder;
import lombok.Data;

/**
 * Individual execution step in a plan
 */
@Data
@Builder
public class ExecutionStep {

	private String stepId;

	private String description;

	private StepType type;

	private String toolName; // If type is TOOL_EXECUTION

	private Map<String, Object> parameters;

	private Integer order;

	public enum StepType {

		LLM_CALL,
		TOOL_EXECUTION,
		DATA_TRANSFORM,
		DECISION

	}

}
