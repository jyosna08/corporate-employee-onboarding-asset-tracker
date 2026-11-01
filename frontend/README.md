# Frontend guide

This React and Vite application provides the employee onboarding and asset inventory workspace. It sends API requests to the API Gateway; it does not connect directly to a service or database.

## Source layout

```text
frontend/src/
  api/          shared JSON client and one resource module per API area
  components/   reusable UI components, one component per file
  constants/    page titles, status values, sort defaults, and UI timing
  hooks/        authentication, workspace data loading, and user actions
  pages/        overview, employee directory, access, settings, and sign-in screens
  utils/        JWT display helpers, role checks, and formatters
  App.jsx       page selection and shared workspace layout
  main.jsx      React application entry point
  globals.css   Tailwind layers and shared global styles
```

## Request flow

```text
Page/component -> hook -> resource module in api/ -> API Gateway (8080)
  -> Spring Boot service -> that service's PostgreSQL database
```

`api/client.js` applies the API base URL, JSON headers, bearer token, and error handling. Resource files such as `api/employees.js` and `api/assets.js` define endpoint calls. Hooks load data and coordinate user actions; pages and components render loading, error, empty, and form states.

## Run locally

Start the backend stack from the repository root, then from this folder run:

```powershell
npm ci
npm run dev
```

Vite prints a local URL, usually `http://localhost:5173`. Add that origin to `CORS_ALLOWED_ORIGINS` in the root `.env`. The default API base URL is `http://localhost:8080`; set `VITE_API_BASE_URL` at build time to use another gateway address.

Create a production bundle with:

```powershell
npm run build
```

The Docker Compose frontend serves the production bundle on port 3000. Start it with the rest of the stack using `docker compose up --build` from the repository root.
