package io.agentic.microagent.app;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Main Spring Boot Application class for MicroAgent.
 * This is the entry point of the application.
 */
@SpringBootApplication(scanBasePackages = { "io.agentic.microagent.api", "io.agentic.microagent.core",
		"io.agentic.microagent.dataaccess", "io.agentic.microagent.shared", "io.agentic.microagent.framework" })
public class MicroagentApplication {

	public static void main(String[] args) {
		SpringApplication.run(MicroagentApplication.class, args);
	}

}
