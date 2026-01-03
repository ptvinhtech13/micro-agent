package io.agentic.microagent.framework.shared.model;

import lombok.Builder;
import lombok.Data;

/**
 * Error information
 */
@Data
@Builder
public class ErrorInfo {

	private String errorCode;

	private String message;

	private String stackTrace;

	private String severity;

}
