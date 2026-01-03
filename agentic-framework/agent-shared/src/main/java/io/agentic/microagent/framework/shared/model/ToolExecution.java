package io.agentic.microagent.framework.shared.model;

import java.time.Duration;
import java.time.Instant;
import java.util.Map;
import lombok.Builder;
import lombok.Data;

/**
 * Tool execution record
 */
@Data
@Builder
public class ToolExecution {

	private String executionId;

	private String toolName;

	private Map<String, Object> parameters;

	private ToolResult result;

	private Duration duration;

	private Instant startTime;

	private Instant endTime;

}
