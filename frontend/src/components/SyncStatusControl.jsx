import { useState } from 'react';
import { SYNC_STATUS } from '../constants/statuses.js';

const SYNC_STATUS_OPTIONS = [
  SYNC_STATUS.pending,
  SYNC_STATUS.synced,
  SYNC_STATUS.failed,
];

/** Lets authorized IT/Admin users set a sync result and explain failures. */
export default function SyncStatusControl({ employee, sync, canSync, onSync }) {
  const [draftStatus, setDraftStatus] = useState(sync.status);
  const [failureMessage, setFailureMessage] = useState(sync.failureMessage || '');
  const [editingFailure, setEditingFailure] = useState(false);
  const [saving, setSaving] = useState(false);

  const changeStatus = async (event) => {
    const nextStatus = event.target.value;
    setDraftStatus(nextStatus);
    if (nextStatus === SYNC_STATUS.failed) {
      setFailureMessage(sync.failureMessage || '');
      setEditingFailure(true);
      return;
    }

    setEditingFailure(false);
    setSaving(true);
    const updated = await onSync(employee, sync, nextStatus, null);
    if (!updated) setDraftStatus(sync.status);
    setSaving(false);
  };

  const saveFailure = async (event) => {
    event.preventDefault();
    const message = failureMessage.trim();
    if (!message) return;

    setSaving(true);
    const updated = await onSync(employee, sync, SYNC_STATUS.failed, message);
    setSaving(false);
    if (updated) setEditingFailure(false);
  };

  const cancelFailure = () => {
    setDraftStatus(sync.status);
    setFailureMessage(sync.failureMessage || '');
    setEditingFailure(false);
  };

  return (
    <div className="min-w-0">
      <label className="sr-only" htmlFor={`${employee.id}-${sync.system}-status`}>
        {sync.system} sync status
      </label>
      <select
        id={`${employee.id}-${sync.system}-status`}
        value={draftStatus}
        disabled={!canSync || saving}
        onChange={changeStatus}
        className="max-w-full rounded-md border border-slate-200 bg-white px-1.5 py-1 text-[10px] text-slate-600 disabled:cursor-default disabled:bg-slate-50"
      >
        {SYNC_STATUS_OPTIONS.map((status) => (
          <option key={status} value={status}>{sync.system}: {status}</option>
        ))}
      </select>
      {editingFailure && canSync && (
        <form onSubmit={saveFailure} className="mt-2 flex flex-wrap gap-1">
          <label className="sr-only" htmlFor={`${employee.id}-${sync.system}-failure`}>
            Failure message for {sync.system}
          </label>
          <input
            id={`${employee.id}-${sync.system}-failure`}
            required
            maxLength={500}
            value={failureMessage}
            onChange={(event) => setFailureMessage(event.target.value)}
            placeholder="Explain why sync failed"
            className="min-w-0 flex-1 rounded border border-rose-200 px-2 py-1 text-[11px]"
          />
          <button
            type="submit"
            disabled={saving || !failureMessage.trim()}
            className="rounded bg-rose-600 px-2 py-1 text-[10px] font-semibold text-white disabled:opacity-50"
          >
            Save
          </button>
          <button
            type="button"
            onClick={cancelFailure}
            disabled={saving}
            className="rounded border px-2 py-1 text-[10px]"
          >
            Cancel
          </button>
        </form>
      )}
    </div>
  );
}
