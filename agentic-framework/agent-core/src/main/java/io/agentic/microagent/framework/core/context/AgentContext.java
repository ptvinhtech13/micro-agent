package io.agentic.microagent.framework.core.context;

import lombok.Builder;
import lombok.Data;

/**
 * Agent context - Built from request and memory
 */
@Data
@Builder
public class AgentContext {

	private final String conversationId;

	private final String userId;

	private final AgenticContextState<String, String> environmentContext;

	private final AgenticContextState<String, String> domainContext;

	private final AgenticContextState<String, String> technicalContext;

}
