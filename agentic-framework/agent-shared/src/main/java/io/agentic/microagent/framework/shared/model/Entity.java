package io.agentic.microagent.framework.shared.model;

import lombok.Builder;
import lombok.Data;

/**
 * Extracted entity from request
 */
@Data
@Builder
public class Entity {

	private String entityId;

	private String type; // e.g., PERSON, ORGANIZATION, LOCATION, etc.

	private String value;

	private Float confidence;

}
