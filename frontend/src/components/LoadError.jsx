import { AlertCircle } from 'lucide-react';

/** Displays a request error without hiding the rest of the workspace. */
export default function LoadError({ message }) {
  return (
    <div className="flex items-center gap-2 rounded-lg border border-rose-200 bg-rose-50 p-3 text-[14px] text-rose-700">
      <AlertCircle size={15} />
      {message}
    </div>
  );
}
