import { KeyRound, ShieldCheck, Users } from 'lucide-react';

const ROLES = [
  ['ADMIN', 'Full platform access', 'Manage employees, onboarding, sync states, and inventory.'],
  ['HR', 'People operations', 'Create and update employee profiles; progress onboarding stages.'],
  ['IT', 'Technology operations', 'Manage assets, assignment lifecycle, and system sync status.'],
];

/** Explains the current user's role and platform access rules. */
export default function AccessPage({ role }) {
  return (
    <div className="space-y-5">
      <section className="rounded-xl border border-slate-200 bg-white p-5 shadow-card">
        <div className="flex items-center gap-3">
          <span className="grid h-10 w-10 place-items-center rounded-xl bg-indigo-50 text-indigo-600">
            <KeyRound size={18} />
          </span>
          <div>
            <h2 className="font-display font-bold">Access controls</h2>
            <p className="text-[14px] text-slate-400">
              Your role controls which operations are available.
            </p>
          </div>
        </div>
        <div className="mt-4 rounded-lg bg-slate-50 p-3 text-[14px]">
          Current role: <b className="text-indigo-700">{role}</b> · JWT protected via API Gateway
        </div>
      </section>
      <section className="grid gap-3 md:grid-cols-3">
        {ROLES.map(([name, title, detail]) => (
          <article key={name} className="rounded-xl border border-slate-200 bg-white p-4">
            <div className="flex items-center justify-between">
              <span className="grid h-8 w-8 place-items-center rounded-lg bg-emerald-50 text-emerald-600">
                <ShieldCheck size={16} />
              </span>
              <span className="rounded-full bg-slate-100 px-2 py-1 text-[11px] font-bold">{name}</span>
            </div>
            <h3 className="mt-3 text-[14px] font-bold">{title}</h3>
            <p className="mt-1 text-[13px] leading-relaxed text-slate-500">{detail}</p>
          </article>
        ))}
      </section>
      <section className="rounded-xl border border-slate-200 bg-white p-4">
        <div className="flex items-center gap-2">
          <Users size={16} className="text-indigo-600" />
          <h3 className="text-[14px] font-bold">Authentication policy</h3>
        </div>
        <ul className="mt-3 list-inside list-disc space-y-1.5 text-[13px] text-slate-500">
          <li>Login is the only public API operation.</li>
          <li>Every other request requires a signed, unexpired bearer token.</li>
          <li>Access rules are checked by API Gateway before the request reaches a service.</li>
          <li>New accounts are provisioned by an administrator, not through public sign-up.</li>
        </ul>
      </section>
    </div>
  );
}
