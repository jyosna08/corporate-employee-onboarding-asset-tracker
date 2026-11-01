import { useState } from 'react';
import { UserPlus, X } from 'lucide-react';

const EMPLOYEE_FIELDS = [
  ['employeeNumber', 'Employee number', 'TM-2042'],
  ['firstName', 'First name', 'Alex'],
  ['lastName', 'Last name', 'Morgan'],
  ['email', 'Work email', 'alex@company.com'],
  ['jobTitle', 'Job title', 'Product Designer'],
  ['department', 'Department', 'Design'],
  ['managerName', 'Manager', 'Maya Chen'],
  ['startDate', 'Start date', ''],
];

const EMPLOYEE_FIELD_LIMITS = {
  employeeNumber: 32,
  firstName: 100,
  lastName: 100,
  email: 320,
  jobTitle: 120,
  department: 120,
  managerName: 200,
};

const EMPTY_EMPLOYEE = {
  employeeNumber: '',
  firstName: '',
  lastName: '',
  email: '',
  jobTitle: '',
  department: '',
  managerName: '',
  startDate: '',
};

/** Creates or updates an employee profile. */
export default function EmployeeFormDialog({ onClose, onSave, initial = null }) {
  const [form, setForm] = useState(initial || EMPTY_EMPLOYEE);
  const [busy, setBusy] = useState(false);
  const [error, setError] = useState('');

  const updateField = (field, value) => {
    setForm((current) => ({ ...current, [field]: value }));
  };

  const submit = async (event) => {
    event.preventDefault();
    setBusy(true);
    setError('');
    try {
      await onSave(form);
      onClose();
    } catch (submissionError) {
      setError(submissionError.message);
    } finally {
      setBusy(false);
    }
  };

  return (
    <div
      className="fixed inset-0 z-50 grid place-items-center bg-slate-950/40 p-4"
      onMouseDown={(event) => event.target === event.currentTarget && onClose()}
    >
      <form onSubmit={submit} className="w-full max-w-lg rounded-2xl bg-white p-6 shadow-2xl">
        <div className="flex items-start justify-between">
          <div>
            <span className="grid h-9 w-9 place-items-center rounded-xl bg-indigo-50 text-indigo-600">
              <UserPlus size={18} />
            </span>
            <h2 className="mt-3 font-display text-lg font-bold">
              {initial ? 'Edit employee' : 'Add an employee'}
            </h2>
            <p className="text-[14px] text-slate-400">
              {initial ? 'Update the employee profile.' : 'Create a profile and initialize access sync.'}
            </p>
          </div>
          <button type="button" onClick={onClose} aria-label="Close" className="text-slate-400">
            <X size={18} />
          </button>
        </div>

        <div className="mt-4 grid grid-cols-2 gap-3 max-sm:grid-cols-1">
          {EMPLOYEE_FIELDS.map(([field, label, placeholder]) => (
            <label key={field} className="text-[13px] font-semibold text-slate-600">
              {label}
              <input
                required={field !== 'managerName'}
                maxLength={EMPLOYEE_FIELD_LIMITS[field]}
                disabled={initial && field === 'employeeNumber'}
                type={field === 'email' ? 'email' : field === 'startDate' ? 'date' : 'text'}
                value={form[field] || ''}
                placeholder={placeholder}
                onChange={(event) => updateField(field, event.target.value)}
                className="mt-1.5 h-9 w-full rounded-lg border border-slate-200 px-3 text-[14px] outline-none focus:border-indigo-400 focus:ring-2 focus:ring-indigo-100 disabled:bg-slate-50"
              />
            </label>
          ))}
        </div>
        {error && <p className="mt-3 rounded-lg bg-rose-50 p-2 text-[14px] text-rose-700">{error}</p>}
        <div className="mt-5 flex justify-end gap-2">
          <button type="button" onClick={onClose} className="rounded-lg border border-slate-200 px-4 py-2 text-[14px]">
            Cancel
          </button>
          <button disabled={busy} className="rounded-lg bg-brand px-4 py-2 text-[14px] font-semibold text-white disabled:opacity-60">
            {busy ? 'Saving…' : initial ? 'Save changes' : 'Create profile'}
          </button>
        </div>
      </form>
    </div>
  );
}
