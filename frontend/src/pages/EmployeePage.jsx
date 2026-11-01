import { useMemo, useState } from 'react';
import EmployeeDirectoryHeader from '../components/EmployeeDirectoryHeader.jsx';
import EmployeeFormDialog from '../components/EmployeeFormDialog.jsx';
import EmployeeRow from '../components/EmployeeRow.jsx';
import EmployeeSearch from '../components/EmployeeSearch.jsx';
import EmployeeTableHeader from '../components/EmployeeTableHeader.jsx';
import EmptyEmployeeRow from '../components/EmptyEmployeeRow.jsx';

/** Employee directory with profile, lifecycle, and sync actions. */
export default function EmployeePage({
  employees,
  onCreate,
  onUpdate,
  onAdvance,
  onSync,
  onEmployment,
  canEdit,
  canSync,
  role,
}) {
  const [search, setSearch] = useState('');
  const [dialog, setDialog] = useState(null);
  const rows = useMemo(() => {
    const query = search.toLowerCase();
    return employees.filter((employee) => (
      `${employee.firstName} ${employee.lastName} ${employee.email} `
      + `${employee.employeeNumber} ${employee.department}`
    ).toLowerCase().includes(query));
  }, [employees, search]);
  const saveEmployee = (data) => (dialog
    ? onUpdate(dialog.id, data)
    : onCreate(data));

  return (
    <section className="rounded-xl border border-slate-200 bg-white shadow-card">
      <EmployeeDirectoryHeader canEdit={canEdit} onAdd={() => setDialog('new')} />
      <EmployeeSearch search={search} setSearch={setSearch} />
      <div className="overflow-x-auto">
        <table className="w-full min-w-[850px] text-left">
          <EmployeeTableHeader />
          <tbody className="divide-y divide-slate-100">
            {rows.map((employee) => (
              <EmployeeRow
                key={employee.id}
                employee={employee}
                canEdit={canEdit}
                canSync={canSync}
                onAdvance={onAdvance}
                onSync={onSync}
                onEmployment={onEmployment}
                onEdit={() => setDialog(employee)}
              />
            ))}
            {!rows.length && <EmptyEmployeeRow />}
          </tbody>
        </table>
      </div>
      <div className="border-t border-slate-100 px-4 py-3 text-[13px] text-slate-400">
        Showing {rows.length} of {employees.length} employees · signed in as {role}
      </div>
      {dialog && (
        <EmployeeFormDialog
          initial={dialog === 'new' ? null : dialog}
          onClose={() => setDialog(null)}
          onSave={saveEmployee}
        />
      )}
    </section>
  );
}
