CREATE TABLE employees (
  id UUID PRIMARY KEY DEFAULT gen_random_uuid(), employee_number VARCHAR(32) NOT NULL UNIQUE,
  first_name VARCHAR(100) NOT NULL, last_name VARCHAR(100) NOT NULL, email VARCHAR(320) NOT NULL,
  job_title VARCHAR(120) NOT NULL, department VARCHAR(120) NOT NULL, manager_name VARCHAR(200),
  start_date DATE NOT NULL, onboarding_status VARCHAR(24) NOT NULL DEFAULT 'PENDING_REVIEW' CHECK (onboarding_status IN ('PENDING_REVIEW','IN_PROGRESS','VERIFIED')),
  employment_status VARCHAR(16) NOT NULL DEFAULT 'ACTIVE' CHECK (employment_status IN ('ACTIVE','TERMINATED')),
  created_at TIMESTAMPTZ NOT NULL DEFAULT now(), updated_at TIMESTAMPTZ NOT NULL DEFAULT now(), version BIGINT NOT NULL DEFAULT 0
);
CREATE UNIQUE INDEX ux_employees_email_lower ON employees(lower(email));
CREATE INDEX ix_employees_pipeline ON employees(onboarding_status,start_date);
CREATE TABLE employee_system_syncs (
  id UUID PRIMARY KEY DEFAULT gen_random_uuid(), employee_id UUID NOT NULL REFERENCES employees(id) ON DELETE CASCADE,
  system_name VARCHAR(16) NOT NULL CHECK (system_name IN ('SLACK','GITHUB')),
  sync_status VARCHAR(16) NOT NULL CHECK (sync_status IN ('PENDING','SYNCED','FAILED')),
  failure_message VARCHAR(1000), last_attempt_at TIMESTAMPTZ, updated_at TIMESTAMPTZ NOT NULL DEFAULT now(),
  UNIQUE(employee_id,system_name)
);
