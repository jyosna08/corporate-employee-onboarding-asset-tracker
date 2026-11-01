/** Displays environment-managed service and credential information. */
export default function SettingsPage() {
  return (
    <section className="rounded-xl border bg-white p-6 text-sm">
      <h2 className="font-bold">Workspace settings</h2>
      <p className="mt-2 text-[14px] text-slate-500">
        Service URLs, database credentials, and signing keys are managed through environment configuration.
      </p>
    </section>
  );
}
