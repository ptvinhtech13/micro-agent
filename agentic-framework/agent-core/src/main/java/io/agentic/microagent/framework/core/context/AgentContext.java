package io.agentic.microagent.framework.core.context;

import io.agentic.microagent.framework.shared.model.UserProfile;
import java.util.Map;
import lombok.Builder;
import lombok.Data;

/**
 * Agent context - Built from request and memory
 */
@Data
@Builder
public class AgentContext {

	private String conversationId;

	private String userId;

	private UserProfile userProfile;

	private Map<String, Object> environmentContext;

	private Map<String, Object> domainContext;

	private Map<String, Object> technicalContext;

}
