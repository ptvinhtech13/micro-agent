package io.agentic.microagent.framework.shared.model;

import java.util.List;
import java.util.Map;

import lombok.Builder;
import lombok.Data;

/**
 * Snapshot of relevant memories
 */
@Data
@Builder
public class MemorySnapshot {

	private List<Message> workingMemory; // Current conversation

	private List<Episode> episodicMemory; // Relevant past episodes

	private Map<String, Object> semanticMemory; // Facts and knowledge

	private Map<String, Object> proceduralMemory; // Workflows and templates

}
