# Coding Standards

## Assumptions

- Standards apply to all new code and refactors. Service-specific conventions can add detail but cannot weaken security or layering.

## Backend

- Use Java 17, Spring Boot 3, Maven, package-by-feature/layer, and one Spring application per service.
- Each business service uses explicit `entity`, `dto`, `enums`, `repository`, `mapper`, `service`, `controller` (or `api`), and `exception` packages, plus configuration where applicable.
- Entity classes map persistence only. DTOs are the only API request/response types. Enums represent controlled roles and lifecycle states.
- Controllers handle HTTP binding/status only. Business decisions belong in services; persistence queries belong in repositories.
- Use constructor injection, Bean Validation on request DTOs, a service-wide exception handler, and Swagger/OpenAPI annotations/descriptions.
- Use Flyway for schema evolution. Never edit a previously applied migration in a shared environment; append a new migration.
- Use transactions at service boundaries for related writes. Never use cross-database foreign keys or entities across services.
- Use Feign service names rather than hardcoded addresses. Configure timeouts and circuit-breaker fallback for downstream calls.
- Avoid magic numbers, hardcoded secrets/URLs, duplicate code, long methods, logging sensitive data, and exposing entities.

## Frontend

- Use React function components and hooks. Keep navigation, header, layout, pipeline, inventory, dialogs, forms, and feedback states in focused components.
- Use Tailwind CSS utility classes and shared theme tokens; do not add a second component styling system without a documented need.
- Keep API calls in a shared API client. The client base URL is configurable and points to API Gateway.
- Model API data with small view mappers. Show loading, error, empty, validation, success, and unauthorized states.
- Use accessible labels, keyboard controls, semantic buttons, focus states, and responsive layouts.
- Keep sample/preview data isolated from the live API client and clearly labeled in the UI.

## Git and review

- Use feature branches and focused commits. Keep each change within the active task/module.
- Update API, database, UI, and deployment documentation when behavior or configuration changes.
- Review generated code for security, validation, and consistency before merging.
