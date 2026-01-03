package io.agentic.microagent.framework.shared.model;

import java.time.Instant;
import java.util.Map;
import lombok.Builder;
import lombok.Data;

/**
 * Conversation message
 */
@Data
@Builder
public class Message {

	private String messageId;

	private Role role;

	private String content;

	private Instant timestamp;

	private Map<String, Object> metadata;

	public enum Role {

		USER,
		ASSISTANT,
		SYSTEM

	}

}
