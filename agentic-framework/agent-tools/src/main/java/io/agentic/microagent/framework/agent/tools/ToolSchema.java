package io.agentic.microagent.framework.agent.tools;

import java.util.Map;
import java.util.Set;
import lombok.Builder;
import lombok.Data;

/**
 * Tool schema - Defines input and output structure
 */
@Data
@Builder
public class ToolSchema {

	private String name;

	private Map<String, ParameterDefinition> inputParameters;

	private OutputSchema outputSchema;

	private Set<String> requiredPermissions;

}
