package io.agentic.microagent.framework.core.context;

import lombok.Getter;
import lombok.With;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Getter
@With
public class AgenticContextState<K, V> {
	private final Map<K, V> state;

	public AgenticContextState() {
		this.state = new ConcurrentHashMap<>();
	}

	public AgenticContextState(Map<K, V> state) {
		this.state = Optional.ofNullable(state)
				.map(ConcurrentHashMap::new)
				.orElse(new ConcurrentHashMap<>());
	}

}