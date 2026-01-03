package io.agentic.microagent.framework.agent.tools;

import java.util.List;
import java.util.Optional;

/**
 * Tool Registry - Central registry for all tools
 *
 * Manages:
 * - Internal tools (built-in functionality)
 * - External tools (MCP servers)
 * - Tool discovery and selection
 * - Tool metadata and capabilities
 *
 * Responsibilities:
 * - Register and unregister tools
 * - Search tools by capability
 * - Get tool schemas
 * - Manage tool lifecycle
 */
public interface ToolRegistry {

	/**
	 * Register a tool
	 *
	 * @param tool The tool to register
	 */
	void register(Tool tool);

	/**
	 * Unregister a tool
	 *
	 * @param toolId The tool ID
	 */
	void unregister(String toolId);

	/**
	 * Get a tool by ID
	 *
	 * @param toolId The tool ID
	 * @return The tool if found
	 */
	Optional<Tool> getTool(String toolId);

	/**
	 * Get all registered tools
	 *
	 * @return List of all tools
	 */
	List<Tool> getAllTools();

	/**
	 * Find tools by capability
	 *
	 * @param capability The required capability
	 * @return List of tools with that capability
	 */
	List<Tool> findByCapability(String capability);

}
