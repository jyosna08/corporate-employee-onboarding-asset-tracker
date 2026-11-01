const HEADERS = [
  'EMPLOYEE', 'ROLE / DEPARTMENT', 'START DATE', 'ONBOARDING',
  'EMPLOYMENT', 'SYSTEM ACCESS', 'ACTIONS',
];

/** Column headings for the employee directory. */
export default function EmployeeTableHeader() {
  return (
    <thead className="bg-slate-50 text-[10px] font-bold tracking-wider text-slate-400">
      <tr>
        {HEADERS.map((header) => <th className="px-4 py-3" key={header}>{header}</th>)}
      </tr>
    </thead>
  );
}
