package io.agentic.microagent.framework.shared.model;

import lombok.Builder;
import lombok.Data;

/**
 * Memory update record
 */
@Data
@Builder
public class MemoryUpdate {

	private String updateId;

	private MemoryType type;

	private String content;

	private Object value;

	public enum MemoryType {

		WORKING, EPISODIC, SEMANTIC, PROCEDURAL

	}

}
