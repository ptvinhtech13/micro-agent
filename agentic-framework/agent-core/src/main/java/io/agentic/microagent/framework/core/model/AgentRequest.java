package io.agentic.microagent.framework.core.model;

import io.agentic.microagent.framework.shared.model.Attachment;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import lombok.Builder;
import lombok.Data;

/**
 * Agent Request - Input to the AI Agent System
 */
@Data
@Builder
public class AgentRequest {

	private String conversationId; // Conversation thread identifier

	private String userId; // User identifier

	private String sessionId; // Current session

	private String message; // User's input message

	private Map<String, Object> context; // Additional context

	private Instant timestamp; // Request time

	private List<Attachment> attachments; // Files, images, etc.

}
