import { Plus } from 'lucide-react';

/** Asset table heading and conditional registration action. */
export default function InventoryHeader({ total, canEdit, onAdd }) {
  return (
    <div className="flex items-center justify-between p-4">
      <div>
        <div className="flex items-center gap-2">
          <h2 className="font-display text-sm font-bold">Asset inventory</h2>
          <span className="rounded-full border px-2 py-0.5 text-[11px] text-slate-500">
            {total} assets
          </span>
        </div>
        <p className="mt-1 text-[13px] text-slate-400">
          Equipment, ownership, and lifecycle at a glance.
        </p>
      </div>
      {canEdit && (
        <button onClick={onAdd} className="flex items-center gap-1 rounded-lg bg-brand px-3 py-2 text-[13px] font-semibold text-white">
          <Plus size={14} /> Add asset
        </button>
      )}
    </div>
  );
}
