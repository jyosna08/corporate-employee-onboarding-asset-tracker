import { useState } from 'react';
import { Laptop, X } from 'lucide-react';

const ASSET_TYPES = [
  'LAPTOP', 'MONITOR', 'DOCK', 'KEYBOARD', 'MOUSE', 'HEADSET', 'PHONE', 'OTHER',
];

/** Creates an inventory asset or edits its profile. */
export default function AssetFormDialog({ onClose, onSave, initial = null }) {
  const [form, setForm] = useState(initial || {
    assetTag: '',
    type: 'LAPTOP',
    hardwareSpecs: '',
    purchaseDate: '',
  });
  const [error, setError] = useState('');
  const [busy, setBusy] = useState(false);

  const updateField = (field, value) => {
    setForm((current) => ({ ...current, [field]: value }));
  };

  const submit = async (event) => {
    event.preventDefault();
    setBusy(true);
    setError('');
    try {
      await onSave(form, initial?.id);
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
      <form onSubmit={submit} className="w-full max-w-md rounded-2xl bg-white p-6 shadow-2xl">
        <div className="flex justify-between">
          <div>
            <span className="grid h-9 w-9 place-items-center rounded-xl bg-blue-50 text-blue-600">
              <Laptop size={18} />
            </span>
            <h2 className="mt-3 font-display text-lg font-bold">
              {initial ? 'Edit asset' : 'Add an asset'}
            </h2>
            <p className="text-[14px] text-slate-400">
              {initial ? 'Update equipment details.' : 'Register new equipment in inventory.'}
            </p>
          </div>
          <button type="button" onClick={onClose} aria-label="Close">
            <X size={18} />
          </button>
        </div>

        <label className="mt-4 block text-[13px] font-semibold">
          Asset tag
          <input
            required
            value={form.assetTag}
            onChange={(event) => updateField('assetTag', event.target.value)}
            placeholder="AST-900"
            className="field"
          />
        </label>
        <label className="mt-3 block text-[13px] font-semibold">
          Type
          <select
            value={form.type}
            onChange={(event) => updateField('type', event.target.value)}
            className="field"
          >
            {ASSET_TYPES.map((type) => <option key={type}>{type}</option>)}
          </select>
        </label>
        <label className="mt-3 block text-[13px] font-semibold">
          Hardware specs
          <input
            required
            value={form.hardwareSpecs}
            onChange={(event) => updateField('hardwareSpecs', event.target.value)}
            placeholder="16 GB RAM, 512 GB SSD"
            className="field"
          />
        </label>
        <label className="mt-3 block text-[13px] font-semibold">
          Purchase date
          <input
            required
            type="date"
            value={form.purchaseDate}
            onChange={(event) => updateField('purchaseDate', event.target.value)}
            className="field"
          />
        </label>
        {error && <p role="alert" className="mt-3 rounded-lg bg-rose-50 p-2 text-[14px] text-rose-700">{error}</p>}
        <div className="mt-5 flex justify-end gap-2">
          <button type="button" onClick={onClose} className="rounded-lg border border-slate-200 px-4 py-2 text-[14px]">
            Cancel
          </button>
          <button disabled={busy} className="rounded-lg bg-brand px-4 py-2 text-[14px] font-semibold text-white">
            {busy ? 'Saving…' : initial ? 'Save changes' : 'Add asset'}
          </button>
        </div>
      </form>
    </div>
  );
}
