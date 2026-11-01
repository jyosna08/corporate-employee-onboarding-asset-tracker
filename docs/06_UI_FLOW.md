# UI Flow

## Assumptions

- The screenshot is a visual reference for an HR/IT operations dashboard, not a complete specification for every interaction.
- The screen uses the three onboarding stages: documentation review, access provisioning, and verified onboarding.
- The browser client connects only to the API Gateway. Preview data is shown until a user connects to a running gateway.
- HR creates employee profiles and advances onboarding. IT reviews inventory and handles equipment assignment workflows.
- React uses reusable feature/layout components and Tailwind CSS; the provided image is the visual reference for spacing, lane colors, and inventory hierarchy.

## Navigation

1. **Overview** - summary counts, sync health, onboarding lanes, and asset inventory preview.
2. **Employee pipeline** - review, provisioning, and verified hire cards with search and stage actions.
3. **Asset inventory** - searchable inventory with status filters and lifecycle labels.
4. **Access & roles** - reserved navigation for role/access management.
5. **Settings** - reserved workspace configuration navigation.

## Authentication

The app opens on a registration/sign-in screen. HR registers with a full name, work email, and password of 8-128 characters, then signs in. After login, the signed-in name and role appear in the sidebar with a logout action below the profile. Registration cannot select an elevated role; Admin/IT accounts are provisioned separately.

## Component hierarchy

```text
App
├── AppShell
│   ├── SidebarNavigation
│   ├── WorkspaceHeader
│   └── ToastRegion
├── OverviewPage
│   ├── MetricCards
│   ├── OnboardingPipeline
│   │   ├── PipelineLane (review / sync / verified)
│   │   └── EmployeeCard
│   └── AssetInventoryPreview
├── EmployeePipelinePage
│   ├── PipelineToolbar
│   ├── PipelineLane
│   └── EmployeeFormDialog
└── AssetInventoryPage
    ├── AssetFilters
    ├── AssetTable
    ├── Pagination
    ├── AssetFormDialog
    └── AssignmentHistoryPanel
```

Shared primitives (buttons, badges, avatars, inputs, dialogs, empty/loading/error states) use Tailwind utility classes and shared theme values. API calls live in a shared typed-by-convention client module; page components do not construct service URLs.

## Primary dashboard layout

- Left rail: workspace identity, primary navigation, help entry, signed-in user.
- Header: breadcrumb, API connection state, sign-in/sign-out, notifications.
- Summary cards: monthly hires, onboarding workload, devices in circulation, access sync health.
- Pipeline lanes: `PENDING_REVIEW` (amber), `IN_PROGRESS` (blue), `VERIFIED` (green).
- Sync attention message: highlights integration failures and points to the employee profile.
- Inventory table: tag, device, owner, purchase date, specs, lifecycle status; includes search, filter, sorting, and pagination affordances.

## User journeys

### HR creates and progresses a new hire

1. Choose **Add employee**.
2. Enter name, work email, title, department, and start date.
3. Create the profile; the service initializes pending Slack/GitHub states and places the employee in documentation review.
4. Advance the employee from review to provisioning, then to verified after checks are complete.

### IT checks inventory

1. Open **Asset inventory**.
2. Search by asset tag, device, hardware specs, or owner.
3. Filter by available, checked out, or maintenance status.
4. Review owner and status before assignment; assignment is guarded by asset and employee state checks in the backend.

### Connect the live services

1. Select **Connect API** in the header.
2. Submit a provisioned user email and password.
3. On successful login, the UI stores the access token in browser local storage and loads employees and assets from the gateway.
4. Sign out removes the token and restores preview data.

## States and responsive behavior

- Preview state is labeled and uses sample records when no token is available.
- Connected state loads API data; failed requests show a toast and return to preview mode.
- Empty inventory search state offers a clear-filter action.
- Form constraints and server validation errors are shown before considering a create successful.
- At tablet widths, metrics use two columns and the inventory table scrolls horizontally; on small screens the sidebar collapses to icons and onboarding lanes stack vertically.
