# Corporate Employee Onboarding & Asset Tracker

A React operations dashboard backed by five independently deployable Spring Boot applications: Eureka discovery, API Gateway, auth, employee, and asset services. The employee and asset services own separate PostgreSQL databases.

## Run the full stack with Docker Compose

Requirements: Docker Desktop with Compose support.

1. Copy `.env.example` to `.env` and replace the local placeholder passwords and JWT key. Keep `.env` private.
2. Start all applications from the project root:

   ```powershell
   Copy-Item .env.example .env
   docker compose --parallel 1 --env-file .env up --build
   ```

3. Open the UI at [http://localhost:3000](http://localhost:3000). The API Gateway is at `http://localhost:8080`; Eureka is at `http://localhost:8761`.
4. Connect using `AUTH_BOOTSTRAP_EMAIL` and `AUTH_BOOTSTRAP_PASSWORD` from `.env`. The auth service creates this first ADMIN account on startup when those variables are set.

HR users can choose **Register as HR** from the app's first screen, create an account, then sign in. Public registration always assigns the HR role; Admin and IT accounts are provisioned separately.

Only the frontend, API Gateway, and Eureka dashboard publish host ports. The business services and PostgreSQL instances stay on the Compose network.

## Local frontend development

Run the backend stack with Compose, then from `frontend/` run `npm install` and `npm run dev`. The Vite origin (`http://localhost:5173`) must be present in `CORS_ALLOWED_ORIGINS` in `.env`.

## Service map

| Service | Port | Database |
| --- | ---: | --- |
| discovery-server | 8761 | none |
| api-gateway | 8080 | none |
| auth-service | 8081 | `auth_db` |
| employee-service | 8082 | `employee_db` |
| asset-service | 8083 | `asset_db` |
| frontend | 3000 (container) | none |

Each service has its own `pom.xml`, `src/main` tree, Dockerfile, and Spring application name. The Maven parent centralizes versions and supports building the services together; it does not combine their runtime processes.

## Build backend applications

From `backend/`, build all independent service jars with:

```powershell
mvn clean package -DskipTests
```

The root Maven module coordinates Java 17 and Spring Boot/Spring Cloud dependency versions. Each service is packaged separately.

## Example API calls

PowerShell examples:

```powershell
$login = Invoke-RestMethod -Method Post -Uri http://localhost:8080/api/auth/login -ContentType 'application/json' -Body '{"email":"admin@example.com","password":"replace-this-with-the-.env-password"}'
$headers = @{ Authorization = "Bearer $($login.accessToken)" }
Invoke-RestMethod -Method Get -Uri http://localhost:8080/api/employees -Headers $headers
Invoke-RestMethod -Method Post -Uri http://localhost:8080/api/employees -Headers $headers -ContentType 'application/json' -Body '{"employeeNumber":"TM-2001","firstName":"Alex","lastName":"Morgan","email":"alex.morgan@example.com","jobTitle":"Product Designer","department":"Design","startDate":"2026-11-16"}'
Invoke-RestMethod -Method Get -Uri http://localhost:8080/api/assets?status=AVAILABLE -Headers $headers
Invoke-RestMethod -Method Post -Uri http://localhost:8080/api/assets -Headers $headers -ContentType 'application/json' -Body '{"assetTag":"AST-900","type":"LAPTOP","hardwareSpecs":"16 GB RAM, 512 GB SSD","purchaseDate":"2026-04-28"}'
$employee = Invoke-RestMethod -Method Post -Uri http://localhost:8080/api/employees -Headers $headers -ContentType 'application/json' -Body '{"employeeNumber":"TM-2002","firstName":"Sam","lastName":"Lee","email":"sam.lee@example.com","jobTitle":"Engineer","department":"Engineering","startDate":"2026-11-16"}'
Invoke-RestMethod -Method Post -Uri "http://localhost:8080/api/employees/$($employee.id)/onboarding/advance" -Headers $headers
$asset = Invoke-RestMethod -Method Post -Uri http://localhost:8080/api/assets -Headers $headers -ContentType 'application/json' -Body '{"assetTag":"AST-901","type":"LAPTOP","hardwareSpecs":"16 GB RAM, 512 GB SSD","purchaseDate":"2026-04-28"}'
Invoke-RestMethod -Method Post -Uri "http://localhost:8080/api/assets/$($asset.id)/assignments" -Headers $headers -ContentType 'application/json' -Body (@{ employeeId = $employee.id } | ConvertTo-Json)
```

See [docs/05_API_CONTRACT.md](docs/05_API_CONTRACT.md) for request/response details and [docs/04_DATABASE.md](docs/04_DATABASE.md) for schema boundaries.

## Framework and implementation notes

The project follows the TrainingMug ADF design-first workflow. Database/API and UI/architecture documentation lives in `docs/`. DTOs separate request/response data from persistence. Flyway migrations create each service's schema. Asset assignment calls the employee service via OpenFeign and Eureka; there is no message broker or shared database.
