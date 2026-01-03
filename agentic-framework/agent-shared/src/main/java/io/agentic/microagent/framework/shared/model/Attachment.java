package io.agentic.microagent.framework.shared.model;

import lombok.Builder;
import lombok.Data;

/**
 * Attachment for agent requests (files, images, etc.)
 */
@Data
@Builder
public class Attachment {

	private String attachmentId;

	private String fileName;

	private String contentType;

	private byte[] content;

	private Long size;

}
