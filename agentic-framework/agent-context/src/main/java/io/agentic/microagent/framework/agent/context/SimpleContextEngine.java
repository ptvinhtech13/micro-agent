package io.agentic.microagent.framework.agent.context;

import io.agentic.microagent.framework.core.ContextEngine;
import io.agentic.microagent.framework.core.context.AgentContext;
import io.agentic.microagent.framework.core.model.AgentRequest;
import io.agentic.microagent.framework.shared.model.UserProfile;
import java.util.HashMap;
import lombok.extern.slf4j.Slf4j;

/**
 * Simple implementation of ContextEngine
 * <p>
 * This is a basic implementation that builds minimal context.
 * In production, this should be enhanced with:
 * - User profile lookup
 * - Environment detection
 * - Domain context gathering
 * - Permission checking
 */
@Slf4j
public class SimpleContextEngine implements ContextEngine {

	@Override
	public AgentContext buildContext(AgentRequest request) {
		log.debug("Building context for user: {} in conversation: {}", request.getUserId(), request
				.getConversationId());

		// Build basic user profile
		UserProfile userProfile = UserProfile.builder()
				.userId(request.getUserId())
				.userName(request.getUserId())
				.permissions(java.util.Set.of("read", "write"))
				.preferences(new HashMap<>())
				.metadata(new HashMap<>())
				.build();

		// Build minimal context
		return AgentContext.builder()
				.conversationId(request.getConversationId())
				.userId(request.getUserId())
				.userProfile(userProfile)
				.environmentContext(request.getContext() != null ? request.getContext() : new HashMap<>())
				.domainContext(new HashMap<>())
				.technicalContext(new HashMap<>())
				.build();
	}

}
