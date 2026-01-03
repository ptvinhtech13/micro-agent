package io.agentic.microagent.framework.core.brain;

import java.util.List;
import lombok.Builder;
import lombok.Data;

/**
 * Reasoning trace - Why this response?
 */
@Data
@Builder
public class ReasoningTrace {

	private List<ReasoningStep> steps;

	private String justification;

}
