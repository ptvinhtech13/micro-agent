package io.agentic.microagent.framework.agent.tools;

import lombok.Builder;
import lombok.Data;

/**
 * Output schema for tool result
 */
@Data
@Builder
public class OutputSchema {

	private String description;

	private ParameterDefinition.DataType type;

	private String format;

}
