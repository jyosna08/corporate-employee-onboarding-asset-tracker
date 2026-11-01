# Deployment and Local Operations

## Assumptions

- Docker Compose is the supported local/demo environment.
- Production deployment is not included in the current implementation. Production secrets must come from a secret manager, not `.env.example`.
- Business services and databases are private network members. API Gateway is the only public backend entry point; the frontend is served separately.

## Local services

| Component | Container port | Host port | Persistent data |
| --- | ---: | ---: | --- |
| React UI | 80 | 3000 | none |
| API Gateway | 8080 | 8080 | none |
| Eureka | 8761 | 8761 | none |
| Auth service / PostgreSQL | 8081 / 5432 | internal only | `auth_data` |
| Employee service / PostgreSQL | 8082 / 5432 | internal only | `employee_data` |
| Asset service / PostgreSQL | 8083 / 5432 | internal only | `asset_data` |

## Start and stop

1. Copy `.env.example` to `.env`; use local-only values and protect the file.
2. Start from the project root with `docker compose --parallel 1 --env-file .env up --build`.
3. Open the frontend at `http://localhost:3000`; authenticate with the bootstrap administrator variables.
4. Stop with Ctrl+C, or run `docker compose --env-file .env down`. Add `-v` only when intentionally deleting local database data.

## Configuration and operations

- Each service reads its database URL/credentials, Eureka URL, and port from environment variables.
- Gateway JWT secret must match the auth-service signing secret. CORS origins, token lifetime, and Feign/gateway timeouts are configurable.
- PostgreSQL data is stored in one named volume per database.
- Flyway applies each service's migrations at startup. Back up volumes before upgrading a persistent deployment.
- Health endpoints are provided by Spring Boot Actuator. Deployment readiness should wait for service health and Eureka registration before accepting traffic.

## Production readiness items

- Replace sample bootstrap credentials and JWT secret; configure secret rotation and secure token delivery.
- Set production CORS origins, TLS termination, database backups, resource limits, log collection, health probes, and network policies.
- Build/publish immutable images and deploy independent services with rolling health checks.
- Configure a supported CI/CD pipeline and record its build/test/deploy results before release.
