import { useState } from 'react';

/** Assigns an available asset to an active employee. */
export default function AssignmentDialog({ asset, employees, onClose, onAssign }) {
  const [employeeId, setEmployeeId] = useState('');
  const [error, setError] = useState('');
  const [busy, setBusy] = useState(false);
  const activeEmployees = employees.filter((employee) => employee.employmentStatus === 'ACTIVE');

  const submit = async (event) => {
    event.preventDefault();
    setBusy(true);
    setError('');
    try {
      await onAssign(asset, employeeId);
      onClose();
    } catch (submissionError) {
      setError(submissionError.message);
    } finally {
      setBusy(false);
    }
  };

  return (
    <div className="fixed inset-0 z-50 grid place-items-center bg-slate-950/40 p-4">
      <form onSubmit={submit} className="w-full max-w-sm rounded-2xl bg-white p-5 shadow-xl">
        <h3 className="font-display font-bold">Assign {asset.assetTag}</h3>
        <p className="mt-1 text-[14px] text-slate-400">
          Only active employees can receive an asset.
        </p>
        <select
          required
          value={employeeId}
          onChange={(event) => setEmployeeId(event.target.value)}
          className="field mt-4"
        >
          <option value="">Select an active employee</option>
          {activeEmployees.map((employee) => (
            <option key={employee.id} value={employee.id}>
              {employee.firstName} {employee.lastName} · {employee.employeeNumber}
            </option>
          ))}
        </select>
        {error && <p role="alert" className="mt-2 text-[14px] text-rose-600">{error}</p>}
        <div className="mt-4 flex justify-end gap-2">
          <button type="button" onClick={onClose} className="rounded-lg border px-3 py-2 text-[14px]">
            Cancel
          </button>
          <button disabled={busy} className="rounded-lg bg-brand px-3 py-2 text-[14px] font-semibold text-white">
            {busy ? 'Assigning…' : 'Confirm assignment'}
          </button>
        </div>
      </form>
    </div>
  );
}
