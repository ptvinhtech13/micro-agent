package io.agentic.microagent.framework.shared.model;

import lombok.Builder;
import lombok.Data;

/**
 * Token usage tracking
 */
@Data
@Builder
public class TokenUsage {

	private Integer promptTokens;

	private Integer completionTokens;

	private Integer totalTokens;

}
