package io.agentic.microagent.framework.agent.tools;

import java.util.Map;

import io.agentic.microagent.framework.shared.model.ToolResult;

/**
 * Tool interface - Represents an executable tool
 *
 * Tools can be:
 * - Internal (built-in functionality)
 * - External (MCP servers, APIs)
 * - Composite (orchestration of multiple tools)
 */
public interface Tool {

	/**
	 * Get tool metadata
	 *
	 * @return Tool metadata
	 */
	ToolMetadata getMetadata();

	/**
	 * Get tool schema (input/output definition)
	 *
	 * @return Tool schema
	 */
	ToolSchema getSchema();

	/**
	 * Execute the tool with given parameters
	 *
	 * @param parameters Input parameters
	 * @return Tool execution result
	 */
	ToolResult execute(Map<String, Object> parameters);

}
