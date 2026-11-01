import { Pencil } from 'lucide-react';
import StatusBadge from './StatusBadge.jsx';

/** Employee profile row with its permitted onboarding and access actions. */
export default function EmployeeRow({ employee, canEdit, canSync, onAdvance, onSync, onEmployment, onEdit }) {
  return (
    <tr className="text-[13px] hover:bg-slate-50/80">
      <td className="px-4 py-3">
        <b className="block text-slate-700">{employee.firstName} {employee.lastName}</b>
        <span className="text-[11px] text-slate-400">
          {employee.employeeNumber} · {employee.email}
        </span>
      </td>
      <td className="px-4 py-3">
        <span className="block text-slate-700">{employee.jobTitle}</span>
        <span className="text-slate-400">{employee.department}</span>
      </td>
      <td className="px-4 py-3 text-slate-500">{employee.startDate}</td>
      <td className="px-4 py-3">
        <StatusBadge value={employee.onboardingStatus} />
        {employee.onboardingStatus !== 'VERIFIED' && canEdit && (
          <button onClick={() => onAdvance(employee)} className="ml-2 text-[11px] font-semibold text-indigo-600">
            Advance
          </button>
        )}
      </td>
      <td className="px-4 py-3">
        <button disabled={!canEdit} onClick={() => onEmployment(employee)} title="Toggle employment status">
          <StatusBadge value={employee.employmentStatus} />
        </button>
      </td>
      <td className="px-4 py-3">
        <div className="flex gap-1">
          {employee.systemSyncs?.map((sync) => (
            <button disabled={!canSync} key={sync.system}
              title={sync.failureMessage || 'Click to update sync state'}
              onClick={() => onSync(employee, sync)}>
              <StatusBadge value={sync.status}>{sync.system}: {sync.status}</StatusBadge>
            </button>
          ))}
        </div>
      </td>
      <td className="px-4 py-3">
        {canEdit && (
          <button onClick={onEdit} className="rounded-md p-1.5 text-slate-500 hover:bg-slate-100" title="Edit employee">
            <Pencil size={13} />
          </button>
        )}
      </td>
    </tr>
  );
}
