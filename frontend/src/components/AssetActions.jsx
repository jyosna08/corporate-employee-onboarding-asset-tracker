import {
  CheckCircle2,
  History,
  Pencil,
  RotateCcw,
  ShieldAlert,
  UserRoundPlus,
} from 'lucide-react';
import { ASSET_STATUS } from '../constants/statuses.js';

/** Lifecycle actions available for an inventory row. */
export default function AssetActions({
  asset,
  canEdit,
  canMaintain,
  onAssign,
  onReturn,
  onEdit,
  onMaintenance,
  onResolveMaintenance,
  onHistory,
}) {
  const isAvailable = asset.lifecycleStatus === ASSET_STATUS.available;
  const isCheckedOut = asset.lifecycleStatus === ASSET_STATUS.checkedOut;
  const isMaintenance = asset.lifecycleStatus === ASSET_STATUS.maintenance;

  return (
    <div className="flex items-center gap-1">
      {isAvailable && canEdit && (
        <button title="Assign asset" onClick={onAssign} className="rounded-md p-1.5 text-indigo-600 hover:bg-indigo-50">
          <UserRoundPlus size={14} />
        </button>
      )}
      {isCheckedOut && canEdit && (
        <button title="Return asset" onClick={() => onReturn(asset)} className="rounded-md p-1.5 text-blue-600 hover:bg-blue-50">
          <RotateCcw size={14} />
        </button>
      )}
      {!isCheckedOut && !isMaintenance && canMaintain && (
        <button title="Mark maintenance" onClick={onMaintenance} className="rounded-md p-1.5 text-orange-600 hover:bg-orange-50">
          <ShieldAlert size={14} />
        </button>
      )}
      {isMaintenance && canMaintain && (
        <button
          title="Resolve maintenance"
          onClick={() => onResolveMaintenance(asset)}
          className="rounded-md p-1.5 text-emerald-600 hover:bg-emerald-50"
        >
          <CheckCircle2 size={14} />
        </button>
      )}
      <button
        title="Edit asset"
        disabled={!canEdit || isCheckedOut}
        onClick={() => onEdit(asset)}
        className="rounded-md p-1.5 text-slate-500 hover:bg-slate-100 disabled:opacity-30"
      >
        <Pencil size={14} />
      </button>
      <button title="Assignment history" onClick={() => onHistory(asset)} className="rounded-md p-1.5 text-slate-500 hover:bg-slate-100">
        <History size={14} />
      </button>
    </div>
  );
}
