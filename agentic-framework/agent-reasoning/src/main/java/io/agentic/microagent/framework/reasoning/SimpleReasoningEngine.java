package io.agentic.microagent.framework.reasoning;

import io.agentic.microagent.framework.core.ReasoningEngine;
import io.agentic.microagent.framework.core.brain.AgentDecision;
import io.agentic.microagent.framework.core.brain.Intent;
import io.agentic.microagent.framework.core.context.AgentContext;
import io.agentic.microagent.framework.core.model.AgentRequest;
import io.agentic.microagent.framework.shared.model.MemorySnapshot;
import java.util.ArrayList;
import java.util.UUID;
import lombok.extern.slf4j.Slf4j;

/**
 * Simple implementation of ReasoningEngine
 *
 * This is a basic implementation with simple heuristics.
 * In production, this should use:
 * - NLP for intent detection
 * - LLM for reasoning
 * - Complex decision logic
 */
@Slf4j
public class SimpleReasoningEngine implements ReasoningEngine {

	@Override
	public Intent analyze(AgentRequest request, AgentContext context, MemorySnapshot memory) {
		log.debug("Analyzing intent for message: {}", request.getMessage());

		// Simple keyword-based intent detection
		String message = request.getMessage()
				.toLowerCase();
		Intent.IntentType type = determineIntentType(message);

		return Intent.builder()
				.intentId(UUID.randomUUID()
						.toString())
				.type(type)
				.domain("general")
				.confidence(0.8f)
				.entities(new ArrayList<>())
				.build();
	}

	@Override
	public AgentDecision decide(Intent intent, AgentContext context, MemorySnapshot memory) {
		log.debug("Making decision for intent type: {}", intent.getType());

		// Simple decision logic - always direct response for now
		AgentDecision.DecisionType decisionType = AgentDecision.DecisionType.DIRECT_RESPONSE;

		return AgentDecision.builder()
				.decisionId(UUID.randomUUID()
						.toString())
				.type(decisionType)
				.reasoning("Based on intent analysis, determined to provide direct response")
				.actions(new ArrayList<>())
				.confidence(0.8f)
				.build();
	}

	private Intent.IntentType determineIntentType(String message) {
		// Simple keyword matching
		if (message.contains("what") || message.contains("how") || message.contains("why") || message.contains("?")) {
			return Intent.IntentType.INFORMATIONAL;
		} else if (message.contains("create") || message.contains("update") || message.contains("delete")) {
			return Intent.IntentType.TRANSACTIONAL;
		} else if (message.contains("analyze") || message.contains("compare") || message.contains("report")) {
			return Intent.IntentType.ANALYTICAL;
		} else {
			return Intent.IntentType.CONVERSATIONAL;
		}
	}

}
