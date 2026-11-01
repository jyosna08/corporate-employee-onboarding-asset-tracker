import { Plus } from 'lucide-react';

/** Employee directory title and conditional create action. */
export default function EmployeeDirectoryHeader({ canEdit, onAdd }) {
  return (
    <div className="flex flex-wrap items-center justify-between gap-3 p-4">
      <div>
        <h2 className="font-display text-sm font-bold">Employee directory</h2>
        <p className="mt-1 text-[13px] text-slate-400">
          Profiles, onboarding status, employment, and access sync.
        </p>
      </div>
      {canEdit && (
        <button onClick={onAdd} className="flex items-center gap-1 rounded-lg bg-brand px-3 py-2 text-[13px] font-semibold text-white">
          <Plus size={14} /> Add employee
        </button>
      )}
    </div>
  );
}
