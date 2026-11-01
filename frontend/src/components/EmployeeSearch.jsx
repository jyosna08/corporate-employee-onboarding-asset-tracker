import { Search } from 'lucide-react';

/** Search input for the employee directory. */
export default function EmployeeSearch({ search, setSearch }) {
  return (
    <div className="border-y border-slate-100 px-4 py-2">
      <label className="flex h-9 max-w-sm items-center gap-2 rounded-lg border border-slate-200 px-3 text-slate-400">
        <Search size={15} />
        <input value={search} onChange={(event) => setSearch(event.target.value)}
          placeholder="Search employees…" className="w-full text-[14px] outline-none" />
      </label>
    </div>
  );
}
