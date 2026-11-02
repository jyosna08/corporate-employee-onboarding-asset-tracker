# Deployment and Local Operations

## Assumptions

- Docker Compose is the supported local/demo environment.
- Production deployment is not included in the current implementation. Production secrets must come from a secret manager, not `.env.example`.
- Business services and databases are private network members. API Gateway is the only public backend entry point; the frontend is served separately.

## Local services

| Component | Container port | Host port | Persistent data |
| --- | ---: | ---: | --- |
| React UI | 8080 | 3000 | none |
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

Compose waits for PostgreSQL and each application healthcheck before starting its dependents. The Java services and gateway expose `http://localhost:<service-port>/actuator/health` internally; their healthchecks use `curl`. The frontend waits for a healthy gateway. Runtime application containers use non-root users; PostgreSQL uses the official image's unprivileged database user.

## Startup readiness and first-request timeout

The previous Compose dependency chain used `service_started` for gateway dependencies. That only waited for a container process to start, so the gateway could route a request before a backend finished starting and registered with Eureka. The configured defaults also allowed up to 30 seconds between Eureka registry fetches and 35 seconds in the Spring Cloud LoadBalancer cache, while the gateway response timeout was only 5 seconds. Together, these settings could surface as a first-request `504` during startup.

The current Compose chain waits on each service's Actuator health before starting the gateway, and waits for a healthy gateway before starting the frontend. The gateway now fetches Eureka registry data every 5 seconds, uses a 5-second load-balancer cache TTL, allows 5 seconds to connect, and waits up to 30 seconds for a response. These values are configurable through `.env`.

## Production Compose overlay

Use the production overlay with production secrets injected by the deployment environment (or a protected env file):

```sh
docker compose -f docker-compose.yml -f docker-compose.prod.yml --env-file .env.production up --build -d
```

All services receive `restart: unless-stopped`. No database port is published to the host, and the overlay also removes the public Eureka port. Set `POSTGRES_USER`, `POSTGRES_PASSWORD`, `JWT_SECRET`, `AUTH_BOOTSTRAP_EMAIL`, `AUTH_BOOTSTRAP_PASSWORD`, `CORS_ALLOWED_ORIGINS`, and the frontend/API URL and timeout values in the deployment environment. Do not use the local placeholders from `.env.example` in production.

## Configuration and operations

- Each service reads its database URL/credentials, Eureka URL, and port from environment variables.
- Gateway JWT secret must match the auth-service signing secret. CORS origins, token lifetime, Eureka registry refresh, load-balancer cache, and Feign/gateway timeouts are configurable.
- PostgreSQL data is stored in one named volume per database.
- Flyway applies each service's migrations at startup. Back up volumes before upgrading a persistent deployment.
- Health endpoints are provided by Spring Boot Actuator. Deployment readiness should wait for service health and Eureka registration before accepting traffic.

## Production readiness items

- Replace sample bootstrap credentials and JWT secret; configure secret rotation and secure token delivery.
- Set production CORS origins, TLS termination, database backups, resource limits, log collection, health probes, and network policies.
- Build/publish immutable images and deploy independent services with rolling health checks.
- Configure a supported CI/CD pipeline and record its build/test/deploy results before release.
