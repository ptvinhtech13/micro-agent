package io.agentic.microagent.framework.agent.tools;

import java.util.Set;

import lombok.Builder;
import lombok.Data;

/**
 * Tool metadata
 */
@Data
@Builder
public class ToolMetadata {

	private String toolId;

	private String name;

	private String description;

	private String version;

	private ToolCategory category;

	private Set<String> capabilities;

	public enum ToolCategory {

		DATABASE, API, FILE_SYSTEM, NOTIFICATION, CALCULATION, MCP_SERVER, CUSTOM

	}

}
