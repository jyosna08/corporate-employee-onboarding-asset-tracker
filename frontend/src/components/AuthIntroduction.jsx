import { ShieldCheck } from 'lucide-react';

/** Introductory message displayed beside authentication forms. */
export default function AuthIntroduction() {
  return (
    <div className="relative max-w-xl">
      <span className="rounded-full border border-white/15 bg-white/5 px-3 py-1 text-[13px] text-indigo-200">
        Employee onboarding &amp; asset operations
      </span>
      <h1 className="mt-6 font-display text-4xl font-bold leading-tight">
        A clearer start for every new teammate.
      </h1>
      <p className="mt-4 max-w-md text-sm leading-6 text-slate-300">
        Manage onboarding stages, account setup, and equipment from one secure workspace.
      </p>
      <div className="mt-8 flex items-center gap-3 text-[14px] text-slate-300">
        <ShieldCheck size={16} className="text-emerald-400" />
        HR accounts are protected with role based access.
      </div>
    </div>
  );
}
