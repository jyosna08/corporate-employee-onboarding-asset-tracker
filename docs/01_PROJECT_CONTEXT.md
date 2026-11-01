# Project Context

## Assumptions

- This is a corporate onboarding and company asset management application for HR and IT teams.
- The existing implementation is an early prototype and will be refactored to match the approved service and UI architecture. There is no production data to migrate.
- The TrainingMug AI Development Framework is the process guide. The project-specific requirements in this repository define the actual services, roles, API, and user journeys.
- React is the only frontend framework. Tailwind CSS is the styling system. The backend uses Java 17, Spring Boot 3, Maven, Spring Cloud, PostgreSQL, and Docker.
- Local development uses Docker Compose. Hosted deployment is a later phase and must inject secrets from a secret manager.

## Problem statement

HR and IT coordinate employee onboarding, identity access, and company equipment across separate workflows. Manual tracking can delay new starters, assign unavailable devices, and make access or asset ownership difficult to audit.

## Objective

Provide one operations dashboard for employee onboarding stages, Slack/GitHub sync health, and asset inventory. Enforce employee and asset lifecycle rules in backend services and keep each business service's data isolated.

## Scope

- React dashboard with reusable layout and feature components, responsive design, Tailwind CSS, login, employee pipeline, access sync feedback, and asset inventory.
- Independently deployable Eureka discovery server, API Gateway, auth-service, employee-service, and asset-service.
- Separate PostgreSQL database for each data-owning service: auth, employee, and asset.
- JWT access control at the gateway and synchronous employee lookup from asset-service using OpenFeign and Eureka.
- Flyway migrations, OpenAPI/Swagger, JUnit tests, Dockerfiles, Compose configuration, environment examples, Postman collection, and run documentation.

## Out of scope for this version

- Automated provisioning in Slack or GitHub. The initial release stores and displays integration sync state; a failed/pending integration is updated through the API.
- Message brokers, shared databases, cross-database foreign keys, direct browser access to service ports, and cloud deployment automation.
- Employee self-service onboarding, payroll, procurement workflows, and asset depreciation accounting.

## User roles

| Role | Main responsibilities |
| --- | --- |
| ADMIN | Manage all employee, access-sync, and asset operations |
| HR | Create/update employee records and advance onboarding stages |
| IT | Read employees for assignment checks; manage assets and integration sync status |

All non-login API access requires a valid JWT. Authorization is enforced at the gateway and repeated for sensitive operations where appropriate.

## Technology and source layout

```text
docs/                 Project design and delivery documents
backend/
  discovery-server/   Eureka registry application
  api-gateway/        Public API, JWT and CORS boundary
  auth-service/       Login, roles and token issuance
  employee-service/  Employee profile and onboarding domain
  asset-service/     Inventory and assignment domain
frontend/             React + Tailwind application
postman/              Importable API collection and environment
docker-compose.yml    Local multi-container environment
```

Every backend application has its own entry point, service name, configuration, and Docker image. The Maven parent is only a version/build convenience; it does not combine runtime services.

## Engineering workflow

1. Review requirements and design documents.
2. Work on one module or service at a time.
3. Explain assumptions before each implementation stage.
4. Add/update unit tests and API documentation with the feature.
5. Build and run the affected service, then verify the end-to-end flow.
6. Update deployment/run instructions when behavior changes.
