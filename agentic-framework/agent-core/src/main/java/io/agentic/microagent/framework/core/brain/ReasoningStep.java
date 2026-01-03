package io.agentic.microagent.framework.core.brain;

import lombok.Builder;
import lombok.Data;

/**
 * Individual reasoning step
 */
@Data
@Builder
public class ReasoningStep {

	private String stepId;

	private String description;

	private String input;

	private String output;

	private Float confidence;

}
