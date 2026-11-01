import {
  ChevronDown,
  CircleHelp,
  Command,
  LayoutDashboard,
  LogOut,
  Settings,
  Shield,
  Users,
  Laptop,
} from 'lucide-react';

const NAVIGATION = [
  ['overview', 'Overview', LayoutDashboard],
  ['employees', 'Employees', Users],
  ['assets', 'Assets', Laptop],
  ['access', 'Access & roles', Shield],
];

/** Main navigation, current user summary, and sign-out action. */
export default function Sidebar({ page, setPage, role, user, onLogout }) {
  const fullName = user?.fullName || user?.email || 'HR user';
  const initials = fullName.split(/[ .@_-]+/)
    .filter(Boolean)
    .slice(0, 2)
    .map((part) => part[0]?.toUpperCase())
    .join('') || 'HR';

  return (
    <aside className="fixed inset-y-0 left-0 z-30 flex w-[248px] flex-col border-r border-slate-200 bg-white px-4 py-5 max-lg:w-[72px] max-lg:px-2">
      <div className="flex h-10 items-center gap-2 px-2">
        <span className="grid h-8 w-8 place-items-center rounded-lg bg-brand text-white shadow">
          <Command size={18} />
        </span>
        <div className="max-lg:hidden">
          <b className="font-display text-[17px] tracking-tight">peopleos</b>
          <small className="block text-[10px] font-bold tracking-[.16em] text-slate-400">
            PEOPLE OPERATIONS
          </small>
        </div>
        <ChevronDown className="ml-auto -rotate-90 text-slate-400 max-lg:hidden" size={15} />
      </div>

      <div className="mt-7 flex items-center gap-2 rounded-xl border border-slate-100 bg-slate-50 p-2">
        <span className="grid h-8 w-8 place-items-center rounded-lg bg-indigo-100 font-bold text-indigo-700">
          C
        </span>
        <div className="min-w-0 flex-1 max-lg:hidden">
          <b className="block truncate text-[14px]">Company workspace</b>
          <span className="text-[13px] text-slate-400">Enterprise workspace</span>
        </div>
        <ChevronDown size={14} className="text-slate-400 max-lg:hidden" />
      </div>

      <p className="mb-2 mt-8 px-2 text-[11px] font-bold tracking-[.14em] text-slate-400 max-lg:hidden">
        WORKSPACE
      </p>
      <nav className="grid gap-1">
        {NAVIGATION.map(([id, label, Icon]) => (
          <button
            key={id}
            onClick={() => setPage(id)}
            className={`flex items-center gap-3 rounded-lg px-3 py-2.5 text-left text-[14px] transition ${page === id
              ? 'bg-indigo-50 font-semibold text-indigo-700'
              : 'text-slate-500 hover:bg-slate-50'}`}
          >
            <Icon size={17} />
            <span className="max-lg:hidden">{label}</span>
          </button>
        ))}
      </nav>

      <button
        onClick={() => setPage('settings')}
        className={`mt-7 flex items-center gap-3 rounded-lg px-3 py-2.5 text-[14px] ${page === 'settings'
          ? 'bg-indigo-50 text-indigo-700'
          : 'text-slate-500 hover:bg-slate-50'}`}
      >
        <Settings size={17} />
        <span className="max-lg:hidden">Settings</span>
      </button>

      <div className="mt-auto">
        <div className="mb-3 flex items-center gap-2 rounded-lg border border-slate-100 p-2 max-lg:hidden">
          <CircleHelp size={17} className="text-indigo-500" />
          <div>
            <b className="block text-[13px]">Need a hand?</b>
            <span className="text-[11px] text-slate-400">People operations help</span>
          </div>
        </div>
        <div className="border-t border-slate-100 pt-3">
          <div className="flex items-center gap-2">
            <span className="grid h-8 w-8 shrink-0 place-items-center rounded-full bg-violet-100 text-[13px] font-bold text-violet-700">
              {initials}
            </span>
            <div className="min-w-0 flex-1 max-lg:hidden">
              <b className="block truncate text-[14px]">{fullName}</b>
              <span className="text-[11px] text-slate-400">{role} access</span>
            </div>
          </div>
          <button onClick={onLogout} title="Sign out" className="mt-2 flex w-full items-center gap-2 rounded-md px-2 py-1.5 text-left text-[13px] font-semibold text-slate-500 hover:bg-rose-50 hover:text-rose-600">
            <LogOut size={14} />
            <span className="max-lg:hidden">Log out</span>
          </button>
        </div>
      </div>
    </aside>
  );
}
