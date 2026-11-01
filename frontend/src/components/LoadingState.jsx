/** Centered loading indicator for initial workspace data. */
export default function LoadingState({ label = 'Loading workspace…' }) {
  return (
    <div className="grid min-h-56 place-items-center text-[14px] text-slate-400">
      <div className="flex items-center gap-2">
        <span className="h-4 w-4 animate-spin rounded-full border-2 border-indigo-200 border-t-indigo-600" />
        {label}
      </div>
    </div>
  );
}
