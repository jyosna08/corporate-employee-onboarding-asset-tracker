import { Laptop } from 'lucide-react';
import { ASSET_STATUS } from '../constants/statuses.js';
import AssetActions from './AssetActions.jsx';
import StatusBadge from './StatusBadge.jsx';

/** Displays one asset row and its applicable actions. */
export default function AssetRow({ asset, employees, canEdit, onAssign, onReturn, onEdit, onMaintenance, onHistory }) {
  const employee = employees.find((item) => item.id === asset.employeeId);
  const requestMaintenance = () => {
    const reason = window.prompt('Maintenance reason');
    if (reason) onMaintenance(asset, reason);
  };

  return (
    <tr className="text-[13px] hover:bg-slate-50/80">
      <td className="px-4 py-3">
        <span className="rounded border bg-slate-50 px-1.5 py-1 font-semibold">{asset.assetTag}</span>
      </td>
      <td className="px-4 py-3">
        <span className="flex items-center gap-2">
          <Laptop size={14} className="text-slate-400" /> {asset.type}
        </span>
      </td>
      <td className="px-4 py-3 text-slate-600">
        {employee
          ? `${employee.firstName} ${employee.lastName}`
          : asset.employeeId ? 'Assigned' : 'Available'}
      </td>
      <td className="px-4 py-3 text-slate-500">{asset.purchaseDate}</td>
      <td className="max-w-52 truncate px-4 py-3 text-slate-500">{asset.hardwareSpecs}</td>
      <td className="px-4 py-3">
        <StatusBadge value={asset.lifecycleStatus} />
        {asset.maintenanceReason && (
          <p className="mt-1 max-w-40 truncate text-[10px] text-orange-700">
            {asset.maintenanceReason}
          </p>
        )}
      </td>
      <td className="px-4 py-3">
        <AssetActions
          asset={asset}
          canEdit={canEdit}
          onAssign={onAssign}
          onReturn={onReturn}
          onEdit={onEdit}
          onMaintenance={requestMaintenance}
          onHistory={onHistory}
        />
      </td>
    </tr>
  );
}
