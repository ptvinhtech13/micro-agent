package io.agentic.microagent.framework.core.brain;

import java.util.List;

import io.agentic.microagent.framework.shared.model.Entity;
import lombok.Builder;
import lombok.Data;

/**
 * User intent detected from request
 */
@Data
@Builder
public class Intent {

	private String intentId;

	private IntentType type;

	private String domain;

	private Float confidence;

	private List<Entity> entities;

	public enum IntentType {

		INFORMATIONAL, // Query, explain
		TRANSACTIONAL, // Transfer, create, delete
		CONVERSATIONAL, // Chat, discuss
		ANALYTICAL // Analyze, compare, report

	}

}
