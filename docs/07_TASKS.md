# Task Breakdown

## Assumptions

- Tasks follow the ADF sequence and the project-specific implementation order.
- Each service task is reviewed and completed before starting the next service.
- Estimates are relative planning estimates; they do not include external account setup or cloud provisioning.

| ID | Priority | Task | Dependencies | Deliverables | Definition of done |
| --- | --- | --- | --- | --- | --- |
| ADF-01 | P0 | Align context, requirements, architecture, data, API and UI documents | User requirements | `docs/01`-`docs/06` | Assumptions are explicit and user-approved |
| BE-01 | P1 | Implement discovery-server | ADF-01 | Eureka app, config, Dockerfile | Services can register and resolve |
| BE-02 | P1 | Implement auth-service | BE-01, ADF-01 | Entity, enum, DTO, repository, service, controller, validation, errors, Swagger, migration, tests | Login/JWT/roles work with its own DB |
| BE-03 | P1 | Implement employee-service | BE-01, ADF-01 | Employee and sync domain layers, migration, API, tests | CRUD, transitions, sync-state rules pass |
| BE-04 | P1 | Implement asset-service | BE-01, BE-03 | Asset/assignment layers, Feign client, migration, API, tests | Lifecycle and employee eligibility rules pass |
| BE-05 | P1 | Implement API Gateway | BE-01, BE-02, BE-03, BE-04 | Eureka routes, JWT, role checks, CORS, timeout config | Public API boundary and authorization work |
| FE-01 | P1 | Implement React/Tailwind foundations and shared shell | ADF-01 | App entry, Tailwind config, layout/header/nav components | Responsive shared shell renders |
| FE-02 | P1 | Implement employee onboarding views | FE-01, BE-03, BE-05 | Pipeline, forms, sync health, API client | Employee actions use gateway DTOs |
| FE-03 | P1 | Implement asset inventory and assignment UX | FE-01, BE-04, BE-05 | Search/filter/table/forms/history | Inventory/assignment uses gateway only |
| REL-01 | P1 | Add Compose, environment template, Postman collection and README | BE-01 through BE-05, FE-01 through FE-03 | Run configuration and API examples | Clean local startup is documented |
| REL-02 | P2 | Final verification and delivery docs | All implementation tasks | Test report, deployment notes | Build/test and manual acceptance checks pass |
