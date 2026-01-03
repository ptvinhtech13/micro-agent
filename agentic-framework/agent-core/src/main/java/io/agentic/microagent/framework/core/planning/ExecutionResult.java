package io.agentic.microagent.framework.core.planning;

import java.util.List;

import io.agentic.microagent.framework.shared.model.ErrorInfo;
import io.agentic.microagent.framework.shared.model.StepResult;
import lombok.Builder;
import lombok.Data;

/**
 * Result of executing a plan
 */
@Data
@Builder
public class ExecutionResult {

	private String executionId;

	private Boolean success;

	private List<StepResult> stepResults;

	private Object finalOutput;

	private ErrorInfo error;

}
