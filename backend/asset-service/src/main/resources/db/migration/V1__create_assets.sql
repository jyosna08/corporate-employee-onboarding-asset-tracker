CREATE TABLE assets (
  id UUID PRIMARY KEY DEFAULT gen_random_uuid(), asset_tag VARCHAR(40) NOT NULL UNIQUE,
  type VARCHAR(80) NOT NULL, hardware_specs VARCHAR(1000) NOT NULL, purchase_date DATE NOT NULL,
  lifecycle_status VARCHAR(24) NOT NULL DEFAULT 'AVAILABLE' CHECK (lifecycle_status IN ('AVAILABLE','CHECKED_OUT','MAINTENANCE_ALERT')),
  maintenance_reason VARCHAR(500),
  employee_id UUID, created_at TIMESTAMPTZ NOT NULL DEFAULT now(), updated_at TIMESTAMPTZ NOT NULL DEFAULT now(), version BIGINT NOT NULL DEFAULT 0,
  CHECK ((lifecycle_status='AVAILABLE' AND employee_id IS NULL AND maintenance_reason IS NULL) OR (lifecycle_status='CHECKED_OUT' AND employee_id IS NOT NULL AND maintenance_reason IS NULL) OR (lifecycle_status='MAINTENANCE_ALERT' AND employee_id IS NULL AND maintenance_reason IS NOT NULL))
);
CREATE INDEX ix_assets_lifecycle_type ON assets(lifecycle_status,type);
CREATE INDEX ix_assets_employee ON assets(employee_id);
CREATE TABLE asset_assignments (
  id UUID PRIMARY KEY DEFAULT gen_random_uuid(), asset_id UUID NOT NULL REFERENCES assets(id), employee_id UUID NOT NULL,
  assigned_at TIMESTAMPTZ NOT NULL DEFAULT now(), returned_at TIMESTAMPTZ,
  CHECK (returned_at IS NULL OR returned_at >= assigned_at)
);
CREATE UNIQUE INDEX ux_asset_assignment_open ON asset_assignments(asset_id) WHERE returned_at IS NULL;
CREATE INDEX ix_assignments_employee ON asset_assignments(employee_id,assigned_at DESC);
