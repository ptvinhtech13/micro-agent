package io.agentic.microagent.framework.core.brain;

import java.util.List;
import lombok.Builder;
import lombok.Data;

/**
 * Agent decision - What the agent decided to do
 */
@Data
@Builder
public class AgentDecision {

	private String decisionId;

	private DecisionType type;

	private String reasoning;

	private List<String> actions;

	private Float confidence;

	public enum DecisionType {

		DIRECT_RESPONSE,
		TOOL_EXECUTION,
		MULTI_STEP_PLAN,
		CLARIFICATION_NEEDED

	}

}
