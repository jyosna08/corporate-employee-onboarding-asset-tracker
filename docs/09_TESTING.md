# Testing Strategy

## Assumptions

- Tests are added with each module rather than deferred until all services are complete.
- Unit tests mock external boundaries; database constraints and Flyway are exercised by integration tests where a PostgreSQL test environment is available.
- Contract tests and Postman/manual checks cover the API Gateway as the public entry point.

## Test layers

- **Domain/service unit tests:** onboarding transitions, status validation, asset eligibility, and return/maintenance invariants.
- **Repository tests:** case-insensitive email/tag uniqueness, filters, ordering, and persistence mappings.
- **Controller tests:** Bean Validation, DTO-only serialization, HTTP statuses, and global error payloads.
- **Feign/client tests:** service-name client behavior, timeout/fallback 503, employee 404, and terminated-employee rejection.
- **Gateway tests:** public login exception, JWT signature/expiry, role matrix, route mapping, CORS, and identity-header sanitization.
- **Integration tests:** Flyway migrations and transactional assignment/return behavior against PostgreSQL.
- **Frontend tests/manual checks:** navigation, responsive behavior, form validation, loading/error/empty states, sign-in, pipeline actions, inventory filters, and API failure handling.
- **End-to-end acceptance:** login through gateway, create employee, advance stages, create asset, assign to active employee, return asset, verify assignment history.

## Test data and safety

- Use generated test credentials and isolated databases. Never place production credentials in fixtures or test logs.
- Verify a failed/timeout employee lookup does not create an assignment or mutate an asset.
- Verify terminated employees and maintenance/checked-out assets cannot be assigned.
- Test malformed IDs, unsupported enums, duplicates, and role violations.

## Test reporting

Each delivery records the command, result, failures or blocked dependencies, and manual scenarios still requiring a running local stack.
