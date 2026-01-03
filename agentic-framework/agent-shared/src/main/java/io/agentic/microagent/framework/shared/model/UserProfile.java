package io.agentic.microagent.framework.shared.model;

import java.util.Map;
import java.util.Set;
import lombok.Builder;
import lombok.Data;

/**
 * User profile information
 */
@Data
@Builder
public class UserProfile {

	private String userId;

	private String userName;

	private Set<String> permissions;

	private Map<String, Object> preferences;

	private Map<String, Object> metadata;

}
