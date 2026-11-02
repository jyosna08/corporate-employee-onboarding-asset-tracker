import { ArrowRight } from 'lucide-react';
import { ONBOARDING_STATUS, SYNC_STATUS } from '../constants/statuses.js';
import SyncStatusControl from './SyncStatusControl.jsx';
import StatusBadge from './StatusBadge.jsx';

/** Employee summary card displayed in an onboarding lane. */
export default function EmployeeCard({ employee, onAdvance, onSync, canEdit, canSync }) {
  const failedSync = employee.systemSyncs?.find((sync) => sync.status === SYNC_STATUS.failed);
  const canAdvance = employee.onboardingStatus !== ONBOARDING_STATUS.verified && canEdit;

  return (
    <article className="rounded-lg border border-slate-200 bg-white p-3 shadow-card">
      <div className="flex items-center gap-2">
        <span className="grid h-8 w-8 place-items-center rounded-full bg-indigo-50 text-[13px] font-bold text-indigo-700">
          {employee.firstName?.[0]}{employee.lastName?.[0]}
        </span>
        <div className="min-w-0 flex-1">
          <b className="block truncate text-[13px] text-slate-800">
            {employee.firstName} {employee.lastName}
          </b>
          <span className="block truncate text-[11px] text-slate-400">{employee.jobTitle}</span>
        </div>
      </div>
      <div className="mt-2 flex items-center justify-between">
        <StatusBadge value={employee.onboardingStatus} />
        <span className="text-[10px] text-slate-400">{employee.employeeNumber}</span>
      </div>
      {canAdvance && (
        <button onClick={() => onAdvance(employee)} className="mt-2 flex w-full items-center justify-center gap-1 rounded-md bg-slate-50 py-1.5 text-[11px] font-semibold text-slate-600 hover:bg-indigo-50 hover:text-indigo-700">
          {employee.onboardingStatus === ONBOARDING_STATUS.pendingReview
            ? 'Move to access sync'
            : 'Mark fully onboarded'}
          <ArrowRight size={12} />
        </button>
      )}
      <div className="mt-2 flex flex-wrap gap-1">
        {(employee.systemSyncs || []).map((sync) => (
          <SyncStatusControl
            key={sync.system}
            employee={employee}
            sync={sync}
            canSync={canSync}
            onSync={onSync}
          />
        ))}
      </div>
      {failedSync && (
        <p className="mt-2 rounded-md bg-rose-50 px-2 py-1.5 text-[10px] leading-relaxed text-rose-700">
          {failedSync.failureMessage}
        </p>
      )}
    </article>
  );
}
