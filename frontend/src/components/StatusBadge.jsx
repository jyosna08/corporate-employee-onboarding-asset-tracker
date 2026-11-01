import { formatStatus } from '../utils/formatters.js';

const STATUS_STYLES = {
  PENDING_REVIEW: 'bg-amber-50 text-amber-700 ring-amber-100',
  IN_PROGRESS: 'bg-blue-50 text-blue-700 ring-blue-100',
  VERIFIED: 'bg-emerald-50 text-emerald-700 ring-emerald-100',
  ACTIVE: 'bg-emerald-50 text-emerald-700 ring-emerald-100',
  TERMINATED: 'bg-rose-50 text-rose-700 ring-rose-100',
  PENDING: 'bg-amber-50 text-amber-700 ring-amber-100',
  SYNCED: 'bg-emerald-50 text-emerald-700 ring-emerald-100',
  FAILED: 'bg-rose-50 text-rose-700 ring-rose-100',
  AVAILABLE: 'bg-emerald-50 text-emerald-700 ring-emerald-100',
  CHECKED_OUT: 'bg-blue-50 text-blue-700 ring-blue-100',
  MAINTENANCE_ALERT: 'bg-orange-50 text-orange-700 ring-orange-100',
};

/** Displays a consistent badge for business status values. */
export default function StatusBadge({ value, children }) {
  const label = children || value || 'Unknown';
  const style = STATUS_STYLES[value] || 'bg-slate-100 text-slate-600 ring-slate-200';

  return (
    <span className={`inline-flex items-center gap-1 rounded-full px-2 py-1 text-[11px] font-semibold ring-1 ring-inset ${style}`}>
      <i className="h-1 w-1 rounded-full bg-current" />
      {typeof label === 'string' ? formatStatus(label) : label}
    </span>
  );
}
