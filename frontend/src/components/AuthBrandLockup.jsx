import { Command } from 'lucide-react';

/** Brand mark used in the wide authentication panel. */
export default function AuthBrandLockup() {
  return (
    <div className="relative flex items-center gap-3">
      <span className="grid h-10 w-10 place-items-center rounded-xl bg-indigo-500">
        <Command size={21} />
      </span>
      <div>
        <b className="font-display text-lg">PeopleOS</b>
        <span className="block text-[11px] tracking-[.18em] text-slate-400">PEOPLE OPERATIONS</span>
      </div>
    </div>
  );
}
