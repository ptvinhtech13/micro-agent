package io.agentic.microagent.framework.core.model;

import io.agentic.microagent.framework.core.brain.AgentDecision;
import io.agentic.microagent.framework.core.brain.ReasoningTrace;
import io.agentic.microagent.framework.shared.model.MemoryUpdate;
import io.agentic.microagent.framework.shared.model.ResponseMetadata;
import io.agentic.microagent.framework.shared.model.ToolExecution;
import java.util.List;
import lombok.Builder;
import lombok.Data;

/**
 * Agent Response - Output from the AI Agent System
 */
@Data
@Builder
public class AgentResponse {

	private String conversationId;

	private String responseId;

	private String content; // Final text response

	private Float confidence; // Response confidence (0-1)

	private ReasoningTrace reasoning; // Why this response?

	private List<ToolExecution> toolsExecuted;

	private List<MemoryUpdate> memoryUpdates;

	private AgentDecision decision; // What agent decided

	private ResponseMetadata metadata;

}
