package io.agentic.microagent.framework.agent.memory;

import io.agentic.microagent.framework.core.context.AgentContext;
import io.agentic.microagent.framework.core.model.AgentRequest;
import io.agentic.microagent.framework.core.model.AgentResponse;
import io.agentic.microagent.framework.shared.model.MemorySnapshot;

/**
 * Memory Manager - Manages multi-tier memory system
 *
 * Memory Tiers:
 * 1. Working Memory (Redis) - Current conversation, fast access
 * 2. Episodic Memory (Vector DB) - Past interactions, semantic search
 * 3. Semantic Memory (PostgreSQL) - Facts and knowledge
 * 4. Procedural Memory - Workflows and templates
 *
 * Responsibilities:
 * - Retrieve relevant memories for current context
 * - Store new memories from interactions
 * - Consolidate and optimize memory
 * - Manage memory budget and token limits
 */
public interface MemoryManager {

	/**
	 * Retrieve relevant memory snapshot for the given context
	 *
	 * @param conversationId The conversation ID
	 * @param context        The agent context
	 * @return Memory snapshot containing relevant memories
	 */
	MemorySnapshot retrieve(String conversationId, AgentContext context);

	/**
	 * Store the interaction in memory
	 *
	 * @param request  The agent request
	 * @param response The agent response
	 * @param context  The agent context
	 */
	void store(AgentRequest request, AgentResponse response, AgentContext context);

	/**
	 * Consolidate memories (background job)
	 * - Summarize old conversations
	 * - Extract facts
	 * - Clean up working memory
	 *
	 * @param conversationId The conversation ID
	 */
	void consolidate(String conversationId);

}
