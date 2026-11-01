# Requirements

## Assumptions

- The UI is an operations tool for authorized HR and IT staff, not a public employee portal.
- Employee onboarding stage and employment status are independent fields.
- Slack and GitHub states are tracked independently per employee. External account provisioning is not performed in the first release.
- An employee can own multiple assets. An asset can have only one active employee assignment.
- Asset assignment history is retained after return. Cross-service references are service-owned IDs only.

## Functional requirements

### Authentication and access

- FR-AUTH-01: User can log in with email and password and receive a signed JWT.
- FR-AUTH-02: HR can register with a name, email, and a password of 8-128 characters, then sign in; public registration always creates an `HR` role. `ADMIN` and `IT` accounts are provisioned separately.
- FR-AUTH-03: System recognizes `ADMIN`, `HR`, and `IT` roles and shows the signed-in user's database-backed name and role with a logout control.
- FR-AUTH-04: API Gateway permits registration and login publicly, validates JWT on business API paths, applies CORS, and checks role permissions.

### Employees and onboarding

- FR-EMP-01: HR/Admin can create, read, update, search, and terminate/reactivate employee profiles.
- FR-EMP-02: New profiles start with onboarding `PENDING_REVIEW`, employment `ACTIVE`, and Slack/GitHub sync `PENDING`.
- FR-EMP-03: Onboarding can advance only `PENDING_REVIEW -> IN_PROGRESS -> VERIFIED`.
- FR-EMP-04: Authorized IT/Admin users can update Slack/GitHub state to `PENDING`, `SYNCED`, or `FAILED`; `FAILED` requires a useful failure message.
- FR-EMP-05: UI groups employees into documentation review, system access sync, and fully onboarded lanes.

### Assets and assignment

- FR-AST-01: HR, IT, and Admin can create, read, update, search, filter, sort, and page through asset inventory. HR, IT, and Admin can assign and return assets; maintenance operations remain IT/Admin-only.
- FR-AST-02: Asset lifecycle is `AVAILABLE`, `CHECKED_OUT`, or `MAINTENANCE_ALERT`.
- FR-AST-03: Available assets have no employee ID. Checked-out assets have one employee ID. Maintenance assets cannot be assigned.
- FR-AST-04: Assignment checks that the employee exists and is `ACTIVE`; it records assignment history and marks the asset checked out atomically in asset-service.
- FR-AST-05: Return clears the current employee, sets `AVAILABLE`, and closes the open assignment with `returned_at`.
- FR-AST-06: Asset-service uses OpenFeign with Eureka service discovery, bounded timeouts, and circuit-breaker fallback. Employee-service outage returns 503 without changing asset data.
- FR-AST-07: An asset cannot be assigned to a terminated employee or assigned while already checked out.

### Dashboard and user experience

- FR-UI-01: React app includes overview, employee pipeline, asset inventory, access/role views, loading/error/empty states, and responsive layout.
- FR-UI-02: Shared navigation and header are separate reusable components; employee and asset views are feature components.
- FR-UI-03: Tailwind CSS is used for tokens, layout, responsive behavior, and component states.
- FR-UI-04: Browser calls API Gateway only and never calls a business service directly.
- FR-UI-05: Preview/demo records are visibly identified and never reported as persisted/live data.

## Non-functional requirements

- NFR-01: Java 17 and Spring Boot 3 compatible service applications.
- NFR-02: No shared business database and no cross-database foreign keys.
- NFR-03: Passwords stored as adaptive hashes; secrets and service URLs provided through environment/configuration.
- NFR-04: Use DTOs at API boundaries; never serialize persistence entities.
- NFR-05: Use constructor injection, layered business logic, meaningful logs, centralized error responses, and Bean Validation.
- NFR-06: API requests have bounded network timeouts; downstream failure is explicit and does not create partial assignment state.
- NFR-07: Each service can be built, containerized, and started independently; Compose provides the integrated local environment.
- NFR-08: Persistent data is migrated by Flyway and covered by service-level unit/integration tests.

## Acceptance criteria

- Valid login returns JWT; invalid credentials return 401; role violations return 403.
- Employee create initializes correct statuses and exactly two sync records; invalid onboarding transitions return 409.
- Asset cannot be assigned unless available and target employee is active.
- Employee-service timeout produces 503 and asset/assignment tables remain unchanged.
- Return closes the active history row and makes the asset available.
- UI shows the three lanes and inventory statuses using data from the API Gateway.
- Compose starts independent applications and databases with no business-service ports published to the host.
