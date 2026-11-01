/** Displays the current workspace section and API connection state. */
export default function WorkspaceHeading({ connected, title }) {
  return (
    <div className="flex flex-wrap items-end justify-between gap-3">
      <div>
        <p className="text-[11px] font-bold tracking-[.15em] text-indigo-500">
          PEOPLE OPERATIONS
        </p>
        <h1 className="mt-1 font-display text-2xl font-bold tracking-tight">{title}</h1>
        <p className="mt-1 text-[14px] text-slate-400">
          Employee and asset information is loaded from your services.
        </p>
      </div>
      <div className="flex items-center gap-2 rounded-full border border-slate-200 bg-white px-3 py-1.5 text-[11px] text-slate-500">
        <span className={`h-1.5 w-1.5 rounded-full ${connected ? 'bg-emerald-500' : 'bg-amber-400'}`} />
        {connected ? 'Connected to API Gateway' : 'Connecting to API Gateway'}
      </div>
    </div>
  );
}
