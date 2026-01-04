package io.agentic.microagent.framework.core.synthesizer;

import io.agentic.microagent.framework.core.brain.AgentDecision;
import io.agentic.microagent.framework.core.model.AgentRequest;
import io.agentic.microagent.framework.core.model.AgentResponse;
import io.agentic.microagent.framework.core.planning.ExecutionResult;
import java.time.Instant;

public interface AgentSynthesizer {
	AgentResponse synthesize(AgentRequest request, AgentDecision decision, ExecutionResult result, Instant startTime);
}