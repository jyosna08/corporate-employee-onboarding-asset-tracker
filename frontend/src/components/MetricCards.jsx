import { Activity, Laptop, RefreshCw, Users } from 'lucide-react';

const CARDS = [
  { label: 'Employees tracked', key: 'tracked', iconStyle: 'bg-indigo-50 text-indigo-600', icon: Users },
  { label: 'In onboarding', key: 'onboarding', iconStyle: 'bg-amber-50 text-amber-600', icon: Activity },
  { label: 'Assets in circulation', key: 'assets', iconStyle: 'bg-blue-50 text-blue-600', icon: Laptop },
  { label: 'Access sync health', key: 'sync', iconStyle: 'bg-green-50 text-green-600', icon: RefreshCw },
];

/** Summary cards for employee, asset, and integration metrics. */
export default function MetricCards({ metrics }) {
  const people = metrics?.employeesByOnboardingStatus;
  const devices = metrics?.assetsByLifecycleStatus;
  const sync = metrics?.systemSync;
  const onboarding = (people?.PENDING_REVIEW || 0) + (people?.IN_PROGRESS || 0);
  const totalAssets = Object.values(devices || {}).reduce((total, count) => total + count, 0);
  const assignedAssets = devices?.CHECKED_OUT || 0;
  const totalSyncs = Object.values(sync || {}).reduce((total, count) => total + count, 0);
  const healthySyncPercent = totalSyncs
    ? Math.round(((sync.SYNCED || 0) / totalSyncs) * 100)
    : 0;
  const values = [
    metrics?.newHiresThisMonth ?? (people?.PENDING_REVIEW || 0)
      + (people?.IN_PROGRESS || 0) + (people?.VERIFIED || 0),
    onboarding,
    `${assignedAssets} / ${totalAssets}`,
    `${healthySyncPercent}%`,
  ];

  return (
    <section className="grid grid-cols-4 gap-3 max-xl:grid-cols-2 max-sm:gap-2">
      {CARDS.map(({ label, key, iconStyle, icon: Icon }, index) => (
        <article key={key} className="rounded-xl border border-slate-200 bg-white p-4 shadow-card">
          <div className="flex items-center justify-between text-[13px] text-slate-500">
            <span>{label}</span>
            <span className={`grid h-7 w-7 place-items-center rounded-lg ${iconStyle}`}>
              <Icon size={16} />
            </span>
          </div>
          <div className="mt-2 font-display text-2xl font-bold tracking-tight text-ink">
            {values[index]}
            <span className="ml-1 text-[13px] font-medium text-slate-400">
              {index < 2 ? 'people' : index === 3 ? 'healthy' : ''}
            </span>
          </div>
          <div className="mt-1 text-[11px] text-slate-400">
            {index === 1
              ? `${people?.PENDING_REVIEW || 0} need documentation review`
              : index === 2
                ? `${devices?.AVAILABLE || 0} available to assign`
                : index === 3 ? 'Slack & GitHub status' : 'Onboarding overview'}
          </div>
        </article>
      ))}
    </section>
  );
}
