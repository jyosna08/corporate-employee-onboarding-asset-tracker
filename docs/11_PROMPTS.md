# AI-Assisted Development Prompts

## Assumptions

- Prompts guide implementation; they do not supersede approved requirements or user instructions.
- Work proceeds one service/module at a time after its API/data contracts are accepted.

## Service implementation prompt template

> Read `docs/01_PROJECT_CONTEXT.md`, `docs/02_REQUIREMENTS.md`, `docs/03_ARCHITECTURE.md`, `docs/04_DATABASE.md`, `docs/05_API_CONTRACT.md`, `docs/08_CODING_STANDARDS.md`, and `docs/09_TESTING.md`. Implement only the assigned service/module. Before coding, state assumptions and identify contract gaps. Use Entity, DTO, Enum, Repository, Service, Controller, Bean Validation, Global Exception Handler, OpenAPI, Flyway, and JUnit layers as applicable. Use constructor injection, DTO-only API responses, environment-based configuration, and no unrelated edits. Implement and document behavior, then report how to run it, API examples, test/build results, and limitations.

## Frontend implementation prompt template

> Read project context, requirements, architecture, API contract, UI flow, and coding standards. Implement only the assigned React feature using reusable components and Tailwind CSS. Call API Gateway only. Include loading, empty, error, success, validation, responsive, and accessible states. Keep preview data visibly separate from persisted API data. Update UI-flow/API documentation if interactions change.

## Review prompt template

> Compare the current module against approved requirements, architecture, database and API contracts, coding standards, and tests. Report concrete defects with file and line locations. Check DTO/entity separation, role enforcement, transactions, status transitions, cross-service boundaries, error mapping, configuration/secrets, and user-visible loading/error states. Do not modify files unless the task explicitly asks for fixes.
