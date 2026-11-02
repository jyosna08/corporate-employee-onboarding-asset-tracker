import { Plus, ShieldCheck } from 'lucide-react';
import { ONBOARDING_STATUS } from '../constants/statuses.js';
import EmployeeCard from './EmployeeCard.jsx';

const LANES = [
  {
    status: ONBOARDING_STATUS.pendingReview,
    title: 'Documentation queue',
    hint: 'REVIEW & APPROVAL',
    tone: 'border-amber-200 bg-amber-50/70',
    icon: 'text-amber-600',
  },
  {
    status: ONBOARDING_STATUS.inProgress,
    title: 'System access sync',
    hint: 'PROVISIONING',
    tone: 'border-blue-200 bg-blue-50/60',
    icon: 'text-blue-600',
  },
  {
    status: ONBOARDING_STATUS.verified,
    title: 'Fully onboarded',
    hint: 'READY TO GO',
    tone: 'border-emerald-200 bg-emerald-50/60',
    icon: 'text-emerald-600',
  },
];

/** Organizes employees into their current onboarding stage. */
export default function PipelineBoard({
  employees = [],
  onAdvance = () => {},
  onSync = () => {},
  onAdd = () => {},
  canEdit = true,
  canSync = true,
}) {
  return (
    <section>
      <div className="mb-3 flex items-end justify-between">
        <div>
          <div className="flex items-center gap-2">
            <h2 className="font-display text-sm font-bold text-slate-800">Onboarding pipeline</h2>
            <span className="flex items-center gap-1 rounded-full bg-emerald-50 px-1.5 py-1 text-[10px] font-bold text-emerald-700">
              <i className="h-1 w-1 rounded-full bg-emerald-500" /> LIVE
            </span>
          </div>
          <p className="mt-1 text-[13px] text-slate-400">Every new hire, moving forward.</p>
        </div>
        {canEdit && (
          <button onClick={onAdd} className="inline-flex items-center gap-1 rounded-lg bg-brand px-3 py-2 text-[13px] font-semibold text-white hover:bg-indigo-700">
            <Plus size={14} /> Add employee
          </button>
        )}
      </div>
      <div className="grid grid-cols-3 gap-3 max-lg:grid-cols-1">
        {LANES.map((lane) => {
          const laneEmployees = employees.filter(
            (employee) => employee.onboardingStatus === lane.status);
          return (
            <section key={lane.status} className="overflow-hidden rounded-xl border border-slate-200 bg-white">
              <div className={`flex items-center gap-2 border-b px-3 py-2 ${lane.tone}`}>
                <ShieldCheck size={14} className={lane.icon} />
                <span className="flex-1 text-[10px] font-bold tracking-[.12em] text-slate-500">
                  {lane.hint}
                </span>
              </div>
              <div className="grid gap-2 p-2">
                {laneEmployees.map((employee) => (
                  <EmployeeCard
                    key={employee.id}
                    employee={employee}
                    onAdvance={onAdvance}
                    onSync={onSync}
                    canEdit={canEdit}
                    canSync={canSync}
                  />
                ))}
                {!laneEmployees.length && (
                  <p className="rounded-lg border border-dashed border-slate-200 py-6 text-center text-[13px] text-slate-400">
                    No employees in this stage.
                  </p>
                )}
              </div>
            </section>
          );
        })}
      </div>
    </section>
  );
}
