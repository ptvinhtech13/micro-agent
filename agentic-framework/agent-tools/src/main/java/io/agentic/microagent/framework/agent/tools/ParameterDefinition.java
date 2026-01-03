package io.agentic.microagent.framework.agent.tools;

import lombok.Builder;
import lombok.Data;

/**
 * Parameter definition for tool input
 */
@Data
@Builder
public class ParameterDefinition {

	private String name;

	private DataType type;

	private Boolean required;

	private String description;

	private Object defaultValue;

	public enum DataType {

		STRING,
		INTEGER,
		FLOAT,
		BOOLEAN,
		OBJECT,
		ARRAY

	}

}
