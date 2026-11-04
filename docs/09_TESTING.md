# Testing Guide

## Automated checks

Run backend verification with JDK 17 from the `backend/` directory:

```sh
mvn -B verify
```

The Maven reactor runs isolated JUnit 5 tests across the backend modules. The latest recorded result is in [TEST_REPORT.md](TEST_REPORT.md): 23 tests passed across auth-service (3), employee-service (6), asset-service (13), and api-gateway (1). discovery-server currently has no tests.

Build the frontend from `frontend/`:

```sh
npm install --include=optional --no-audit --no-fund
npm run build
```

The build writes generated assets to `frontend/dist/`. The CI workflow runs backend verification on Java 17 and the frontend production build on Node 22. It also builds the Compose images after those jobs pass.

## What the tests cover

- **Auth service:** successful HR registration and password hashing, duplicate email conflict, and wrong-password rejection.
- **Employee service:** creation and sync-state initialization, duplicate employee number/email conflicts, invalid advance after `VERIFIED`, and transitions from `PENDING_REVIEW` through `IN_PROGRESS` to `VERIFIED`.
- **Asset service:** creation and duplicate tag conflict; valid assignment; rejecting unavailable and maintenance assets; employee lookup unavailable/not found; return and assignment closure; maintenance marking/resolution; conflict when resolving a non-maintenance asset.
- **API Gateway:** role policy for ADMIN, HR, and IT, including read access, employee and asset writes, sync updates, and maintenance routes.
- **Frontend:** production bundle compilation. Browser behavior is checked manually; there is no frontend unit-test command configured.

These service tests use JUnit 5 and Mockito without a database or Testcontainers. The automated role-policy test exercises the policy directly; it does not replace gateway HTTP/security integration checks.

## Postman checks

Import both files from the root `postman/` directory into Postman:

- `Corporate-Employee-Onboarding-Asset-Tracker.postman_collection.json`
- `Corporate-Employee-Onboarding-Asset-Tracker.local.postman_environment.json`

Select **Corporate Tracker Local**, set the HR and IT login credentials, and confirm `apiUrl` points to the gateway (default `http://localhost:8080`). Run the HR and IT login requests to save their tokens. HR registration is available for a new HR account; run it once and use unique account values. An existing IT account must be provisioned separately. Requests that create employees or assets write to the local database, so change the employee number, employee email, and asset tag before repeating them. The Auth Service Health request calls the service directly and needs that service exposed at `authServiceUrl`; it is not a gateway route.

The collection includes response status and body assertions for auth, employee, asset, dashboard, and maintenance resolution requests. Run requests in order when using the generated employee and asset IDs.

## Manual acceptance

With the local stack available, verify the main user flows through the UI or Postman:

1. Sign in as HR and confirm the dashboard loads.
2. Create an employee, edit the profile, and advance onboarding through the allowed stages.
3. Update an employee system sync as IT; verify a failure message is retained when status is `FAILED`.
4. Create an asset, assign it to an active employee, inspect assignment history, and return it.
5. Mark an available asset for maintenance and resolve it back to `AVAILABLE`.
6. Confirm HR cannot perform IT-only sync or maintenance actions, and IT cannot create employee records.
7. Stop employee-service temporarily and confirm assignment reports the dependency failure without creating an assignment; restart it and retry.

## Test data and reporting

Use local-only credentials and unique test records. Never put production secrets in Postman exports or test logs. Preserve database volumes unless the test explicitly requires a clean database.

Record the command, runtime versions, per-module counts, pass/fail result, environmental blockers, and manual checks in [TEST_REPORT.md](TEST_REPORT.md). Update that report when test results change.
