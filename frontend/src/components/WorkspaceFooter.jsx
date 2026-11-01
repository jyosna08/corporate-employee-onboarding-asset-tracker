/** Shows application branding and current connection state. */
export default function WorkspaceFooter({ connected }) {
  return (
    <footer className="flex justify-between py-2 text-[11px] text-slate-400">
      <span>© 2026 PeopleOS · {connected ? 'Live workspace' : 'Connecting'}</span>
      <span>Employee and asset operations</span>
    </footer>
  );
}
