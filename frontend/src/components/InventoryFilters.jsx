import { ArrowDownUp, Search } from 'lucide-react';
import { ASSET_STATUS } from '../constants/statuses.js';

/** Search, lifecycle filter, and sort controls for the inventory table. */
export default function InventoryFilters({ search, setSearch, status, setStatus, onSort }) {
  return (
    <div className="flex flex-wrap items-center justify-between gap-2 border-y border-slate-100 px-4 py-2">
      <label className="flex h-9 min-w-56 flex-1 items-center gap-2 rounded-lg border border-slate-200 px-3 text-slate-400">
        <Search size={15} />
        <input
          value={search}
          onChange={(event) => setSearch(event.target.value)}
          placeholder="Search tags, devices, specs…"
          className="w-full text-[14px] text-slate-700 outline-none"
        />
      </label>
      <select
        value={status}
        onChange={(event) => setStatus(event.target.value)}
        className="h-9 rounded-lg border border-slate-200 px-3 text-[14px]"
      >
        <option value="">All statuses</option>
        <option value={ASSET_STATUS.available}>Available</option>
        <option value={ASSET_STATUS.checkedOut}>Checked out</option>
        <option value={ASSET_STATUS.maintenance}>Maintenance</option>
      </select>
      <button onClick={onSort} className="flex h-9 items-center gap-2 rounded-lg border border-slate-200 px-3 text-[14px] text-slate-600">
        <ArrowDownUp size={14} /> Sort
      </button>
    </div>
  );
}
