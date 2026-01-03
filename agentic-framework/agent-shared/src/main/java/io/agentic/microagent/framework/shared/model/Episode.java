package io.agentic.microagent.framework.shared.model;

import java.time.Instant;

import lombok.Builder;
import lombok.Data;

/**
 * Episodic memory entry
 */
@Data
@Builder
public class Episode {

	private String episodeId;

	private float[] embedding; // Vector representation

	private String content;

	private EpisodeContext context;

	private Float importance; // 0-1 scale

	private Instant timestamp;

}
