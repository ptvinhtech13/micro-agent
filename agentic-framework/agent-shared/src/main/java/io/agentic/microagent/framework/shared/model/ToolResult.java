package io.agentic.microagent.framework.shared.model;

import java.util.Map;
import lombok.Builder;
import lombok.Data;

/**
 * Tool execution result
 */
@Data
@Builder
public class ToolResult {

	private Boolean success;

	private Object data;

	private ErrorInfo error;

	private Map<String, Object> metadata;

}
