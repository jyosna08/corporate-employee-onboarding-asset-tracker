import { Bell } from 'lucide-react';

/** Sticky top bar with page title, connection state, and user avatar. */
export default function WorkspaceHeader({ title, connected, user }) {
  const name = user?.fullName || user?.email || 'HR';
  const initials = name.split(/[ .@_-]+/)
    .filter(Boolean)
    .slice(0, 2)
    .map((part) => part[0]?.toUpperCase())
    .join('') || 'HR';

  return (
    <header className="sticky top-0 z-20 flex h-14 items-center justify-between border-b border-slate-200 bg-white/95 px-7 backdrop-blur max-sm:px-4">
      <div className="truncate text-[14px] text-slate-400">
        Workspace <span className="mx-2 text-slate-300">/</span>
        <b className="text-slate-700">{title}</b>
      </div>
      <div className="flex items-center gap-3">
        <span className="hidden items-center gap-2 text-[13px] text-slate-500 sm:flex">
          <i className={`h-1.5 w-1.5 rounded-full ${connected ? 'bg-emerald-500' : 'bg-amber-400'}`} />
          {connected ? 'Live API' : 'Connecting'}
        </span>
        <button aria-label="Notifications" className="relative rounded-md p-1.5 text-slate-500 hover:bg-slate-100">
          <Bell size={17} />
          <i className="absolute right-1 top-1 h-1.5 w-1.5 rounded-full bg-rose-400" />
        </button>
        <span title={name} className="grid h-7 w-7 place-items-center rounded-full bg-violet-100 text-[11px] font-bold text-violet-700">
          {initials}
        </span>
      </div>
    </header>
  );
}
