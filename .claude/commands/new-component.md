Create a new component following the architecture patterns in `/docs/architecture/high_level_java_design.md`.

Based on the component type I specify, create:

1. **Interface**: Following the interface-first approach with proper Javadoc
2. **Implementation**: With `@Service` or `@Component` annotation
3. **Data Models**: If needed, using `@Data` and `@Builder` from Lombok
4. **Spring Configuration**: If special beans are needed

Make sure to:
- Use proper package structure (e.g., `com.agent.core.*`)
- Include dependency injection via constructor with `@RequiredArgsConstructor`
- Add comprehensive Javadoc
- Follow naming conventions from the architecture docs
- Include TODO comments for implementation placeholders

Ask me which layer and component type before creating.