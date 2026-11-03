# Test Report

Date: 2026-11-03
Command: cd backend, then mvn -B verify

## Automated tests (all pass, 23 tests)

| Module           | Tests                             | Result |
| ---------------- | --------------------------------- | ------ |
| discovery-server | 0                                 | n/a    |
| auth-service     | 3 (AuthServiceTest)               | Pass   |
| employee-service | 6 (entity 2, service 4)           | Pass   |
| asset-service    | 13 (entity 3, service 10)         | Pass   |
| api-gateway      | 1 (role matrix for ADMIN, HR, IT) | Pass   |

## Issues found and fixed

- Mockito default agent cannot attach on JDK 25, so tests use mock-maker-subclass.
- asset-service tests: strict stubbing errors fixed with lenient strictness.
- api-gateway: spring-boot-starter-test was missing in the pom, added.
- api-gateway: one role test contradicted the policy (HR has no asset maintenance), corrected.

## Manual testing (Docker Compose and UI), all pass

Login and register, employee create and advance, sync with custom failure message,
asset create, assign, return and history, maintenance and resolve, full employee names,
HR role-based UI, employee-service down gives a clear error and recovers after restart.

## Known issue

The first request after a cold start can return 504. A refresh works.
