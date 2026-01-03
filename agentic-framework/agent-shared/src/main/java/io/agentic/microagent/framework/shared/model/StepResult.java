package io.agentic.microagent.framework.shared.model;

import lombok.Builder;
import lombok.Data;

/**
 * Result of executing a single step
 */
@Data
@Builder
public class StepResult {

	private String stepId;

	private Boolean success;

	private Object output;

	private ErrorInfo error;

}
