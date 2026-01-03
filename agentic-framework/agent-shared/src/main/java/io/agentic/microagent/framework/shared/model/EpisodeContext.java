package io.agentic.microagent.framework.shared.model;

import java.util.List;

import lombok.Builder;
import lombok.Data;

/**
 * Context of an episode
 */
@Data
@Builder
public class EpisodeContext {

	private String userIntent;

	private List<String> toolsUsed;

	private String outcome;

}
