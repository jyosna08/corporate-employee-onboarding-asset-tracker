import { ChevronLeft, ChevronRight } from 'lucide-react';

/** Page controls and result count for the inventory table. */
export default function InventoryPagination({ page, totalPages, totalAssets, setPage }) {
  return (
    <footer className="flex items-center justify-between border-t border-slate-100 px-4 py-3 text-[13px] text-slate-400">
      <span>Page {page + 1} of {Math.max(totalPages, 1)} · {totalAssets} assets</span>
      <div className="flex gap-1">
        <button disabled={page <= 0} onClick={() => setPage((current) => current - 1)} className="rounded border px-2 py-1 disabled:opacity-40">
          <ChevronLeft size={14} />
        </button>
        <button disabled={page + 1 >= totalPages} onClick={() => setPage((current) => current + 1)} className="rounded border px-2 py-1 disabled:opacity-40">
          <ChevronRight size={14} />
        </button>
      </div>
    </footer>
  );
}
