package io.agentic.microagent.framework.core;

import io.agentic.microagent.framework.core.context.AgentContext;
import io.agentic.microagent.framework.core.model.AgentRequest;

/**
 * Context Engine - Builds agent context from request
 * <p>
 * Responsibilities:
 * - Extract user profile
 * - Build environment context
 * - Gather domain-specific context
 * - Prepare technical context
 */
public interface ContextEngine {

	/**
	 * Build agent context from request
	 *
	 * @param request The agent request
	 * @return The built agent context
	 */
	AgentContext buildContext(AgentRequest request);

}
