package io.agentic.microagent.framework.shared.model;

import java.time.Duration;
import lombok.Builder;
import lombok.Data;

/**
 * Response metadata
 */
@Data
@Builder
public class ResponseMetadata {

	private TokenUsage tokens;

	private Duration latency;

	private String modelUsed;

	private String executionPath;

}
