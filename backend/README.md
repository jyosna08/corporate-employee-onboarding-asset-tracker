# Backend guide

The backend contains five independently runnable Spring Boot applications. The Maven parent in this folder centralizes dependency versions and lets you build the applications together; it does not combine them into one runtime process.

## Folder layout

```text
backend/
  pom.xml                 Maven reactor and shared dependency versions
  discovery-server/       Eureka service registry (port 8761)
  api-gateway/            Public API, JWT authorization, dashboard (port 8080)
  auth-service/            Login, registration, token issuance (port 8081)
  employee-service/        Employee profiles and onboarding (port 8082)
  asset-service/           Inventory and assignment history (port 8083)
```

Each service has its own `pom.xml`, `Dockerfile`, Spring Boot entry point, `src/main/java`, `src/main/resources`, and Flyway migrations where it owns a database. Each service's `application.yml` contains its runtime settings. Database migrations are in `src/main/resources/db/migration`; update them only when a schema change is intended.

The Spring source packages are organized by responsibility:

- `controller/` receives HTTP requests and returns responses.
- `dto/` defines request and response records, one public type per file.
- `entity/` maps the service's own database tables.
- `enums/` defines persisted or API status values.
- `repository/` contains Spring Data queries.
- `mapper/` converts between entities and response DTOs.
- `service/` applies business rules and coordinates repositories.
- `exception/` contains custom exceptions and the service's global API error handler.
- `config/` contains Spring configuration.
- `constants/` contains shared values that should not be repeated as literals.
- `client/` in `asset-service` contains its employee-service Feign client and fallback.

`discovery-server` has a small `com.trainingmug.discovery` package for its Eureka application entry point. The gateway uses `com.trainingmug.gateway` packages: `security/` for JWT filtering and role policy, `controller/` for dashboard endpoints, `config/` for CORS, and `constants/` for route and header values.

## Request flow

```text
React UI
  -> API Gateway (8080): JWT validation, role checks, and routing
  -> Auth, employee, or asset service
  -> The owning service's PostgreSQL database
```

The frontend calls the gateway only. Eureka resolves service names for gateway routes and OpenFeign calls. The asset service validates an employee through employee-service before assigning equipment. Auth, employee, and asset services each own a separate PostgreSQL database and Flyway migration history.

An employee-to-asset association is a UUID reference. `asset-service` stores `employee_id` locally; it does not create a cross-database foreign key. See [the architecture guide](../docs/03_ARCHITECTURE.md) for diagrams and the full service map.

## Build and run

From this folder, compile all services, run the existing tests, and package each application:

```powershell
mvn -B verify
```

To start the full stack, configure the root `.env` from `.env.example`, then run `docker compose up --build` from the repository root. The root [README](../README.md) lists the local URLs and bootstrap-admin setup.
