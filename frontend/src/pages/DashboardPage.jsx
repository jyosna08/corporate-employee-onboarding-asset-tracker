import { AlertTriangle, ArrowRight, Laptop } from 'lucide-react';
import MetricCards from '../components/MetricCards.jsx';
import PipelineBoard from '../components/PipelineBoard.jsx';
import StatusBadge from '../components/StatusBadge.jsx';
import { SYNC_STATUS } from '../constants/statuses.js';

const ASSET_COLUMNS = ['TAG', 'DEVICE', 'ASSIGNED TO', 'PURCHASED', 'STATUS'];

/** Dashboard overview of employee onboarding, access sync, and assets. */
export default function DashboardPage({
  employees,
  assets,
  metrics,
  onAdvance,
  onSync,
  onAdd,
  canEdit,
  canSync,
  onGoAssets,
}) {
  const failures = employees.flatMap((employee) => (
    (employee.systemSyncs || [])
      .filter((sync) => sync.status === SYNC_STATUS.failed)
      .map((sync) => ({ employee, sync }))
  ));
  const assetCount = metrics?.assetsByLifecycleStatus
    ? Object.values(metrics.assetsByLifecycleStatus).reduce((total, count) => total + count, 0)
    : assets.length;

  return (
    <div className="space-y-6">
      <MetricCards metrics={metrics} />
      <PipelineBoard
        employees={employees}
        onAdvance={onAdvance}
        onSync={onSync}
        onAdd={onAdd}
        canEdit={canEdit}
        canSync={canSync}
      />
      <section className="overflow-hidden rounded-xl border border-slate-200 bg-white shadow-card">
        <header className="flex items-center justify-between p-4">
          <div>
            <div className="flex items-center gap-2">
              <h2 className="font-display text-sm font-bold">Asset inventory</h2>
              <span className="rounded-full border px-2 py-0.5 text-[11px] text-slate-500">
                {assetCount} assets
              </span>
            </div>
            <p className="mt-1 text-[13px] text-slate-400">
              Equipment, ownership, and lifecycle at a glance.
            </p>
          </div>
          <button onClick={onGoAssets} className="flex items-center gap-1 text-[13px] font-semibold text-indigo-600">
            View inventory <ArrowRight size={13} />
          </button>
        </header>
        <div className="overflow-x-auto">
          <table className="w-full min-w-[680px] text-left">
            <thead className="bg-slate-50 text-[10px] font-bold tracking-wider text-slate-400">
              <tr>
                {ASSET_COLUMNS.map((column) => (
                  <th className="px-4 py-3" key={column}>{column}</th>
                ))}
              </tr>
            </thead>
            <tbody className="divide-y divide-slate-100">
              {assets.slice(0, 5).map((asset) => {
                const employee = employees.find((item) => item.id === asset.employeeId);
                return (
                  <tr key={asset.id} className="text-[13px]">
                    <td className="px-4 py-3 font-semibold">{asset.assetTag}</td>
                    <td className="px-4 py-3">
                      <span className="flex items-center gap-2">
                        <Laptop size={13} /> {asset.type}
                      </span>
                    </td>
                    <td className="px-4 py-3 text-slate-500">
                      {employee
                        ? `${employee.firstName} ${employee.lastName}`
                        : asset.employeeId ? 'Assigned' : 'Available'}
                    </td>
                    <td className="px-4 py-3 text-slate-500">{asset.purchaseDate}</td>
                    <td className="px-4 py-3">
                      <StatusBadge value={asset.lifecycleStatus} />
                    </td>
                  </tr>
                );
              })}
              {!assets.length && (
                <tr>
                  <td colSpan="5" className="py-8 text-center text-[14px] text-slate-400">
                    No assets available yet.
                  </td>
                </tr>
              )}
            </tbody>
          </table>
        </div>
      </section>
      {failures.map(({ employee, sync }) => (
        <div key={`${employee.id}-${sync.system}`} className="flex items-center gap-3 rounded-xl border border-rose-200 bg-rose-50 p-3 text-[14px]">
          <AlertTriangle size={16} className="text-rose-600" />
          <div className="flex-1">
            <b>{employee.firstName} {employee.lastName} · {sync.system} sync needs attention</b>
            <p className="mt-1 text-[13px] text-rose-700">{sync.failureMessage}</p>
          </div>
        </div>
      ))}
    </div>
  );
}
