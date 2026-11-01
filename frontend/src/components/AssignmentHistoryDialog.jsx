import { formatDate } from '../utils/formatters.js';

/** Displays historical assignments for a selected asset. */
export default function AssignmentHistoryDialog({ asset, rows, onClose }) {
  return (
    <div className="fixed inset-0 z-50 grid place-items-center bg-slate-950/40 p-4">
      <section className="w-full max-w-lg rounded-2xl bg-white p-5 shadow-xl">
        <div className="flex justify-between">
          <div>
            <h3 className="font-display font-bold">Assignment history</h3>
            <p className="text-[14px] text-slate-400">{asset.assetTag} · {asset.type}</p>
          </div>
          <button onClick={onClose} className="text-slate-400" aria-label="Close history">
            ✕
          </button>
        </div>
        <div className="mt-4 max-h-72 overflow-auto">
          {rows.length
            ? rows.map((row) => (
              <div key={row.id} className="flex justify-between border-t py-3 text-[14px]">
                <span className="font-medium">Employee {row.employeeId.slice(0, 8)}</span>
                <span className="text-slate-500">
                  {formatDate(row.assignedAt)} → {row.returnedAt ? formatDate(row.returnedAt) : 'Current'}
                </span>
              </div>
            ))
            : <p className="py-8 text-center text-[14px] text-slate-400">No assignment history yet.</p>}
        </div>
      </section>
    </div>
  );
}
