import { useState } from 'react';
import { ASSET_SORTS } from '../constants/app.js';
import AssignmentDialog from './AssignmentDialog.jsx';
import AssignmentHistoryDialog from './AssignmentHistoryDialog.jsx';
import AssetRow from './AssetRow.jsx';
import EmptyInventoryRow from './EmptyInventoryRow.jsx';
import InventoryFilters from './InventoryFilters.jsx';
import InventoryHeader from './InventoryHeader.jsx';
import InventoryPagination from './InventoryPagination.jsx';
import InventoryTableHeader from './InventoryTableHeader.jsx';

/** Shows searchable, filterable inventory with asset actions and history. */
export default function AssetInventory({
  pageData,
  search,
  setSearch,
  status,
  setStatus,
  sort,
  setSort,
  page,
  setPage,
  employees,
  canEdit,
  canMaintain,
  onAdd,
  onEdit,
  onAssign,
  onReturn,
  onMaintenance,
  onResolveMaintenance,
  onHistory,
}) {
  const [assigning, setAssigning] = useState(null);
  const [historyAsset, setHistoryAsset] = useState(null);
  const [historyRows, setHistoryRows] = useState([]);
  const [historyError, setHistoryError] = useState('');
  const rows = pageData?.content || [];
  const totalPages = pageData?.totalPages || 0;

  const showHistory = async (asset) => {
    setHistoryAsset(asset);
    setHistoryError('');
    try {
      setHistoryRows(await onHistory(asset));
    } catch (error) {
      setHistoryRows([]);
      setHistoryError(error.message || 'Could not load assignment history.');
    }
  };

  const toggleSort = () => {
    setSort(sort === ASSET_SORTS.default
      ? ASSET_SORTS.purchaseDateDescending
      : ASSET_SORTS.default);
  };

  return (
    <section className="overflow-hidden rounded-xl border border-slate-200 bg-white shadow-card">
      <InventoryHeader total={pageData?.totalElements ?? 0} canEdit={canEdit} onAdd={onAdd} />
      <InventoryFilters
        search={search}
        setSearch={setSearch}
        status={status}
        setStatus={setStatus}
        onSort={toggleSort}
      />
      <div className="overflow-x-auto">
        <table className="w-full min-w-[850px] text-left">
          <InventoryTableHeader />
          <tbody className="divide-y divide-slate-100">
            {rows.map((asset) => (
              <AssetRow
                key={asset.id}
                asset={asset}
                employees={employees}
                canEdit={canEdit}
                canMaintain={canMaintain}
                onAssign={() => setAssigning(asset)}
                onReturn={onReturn}
                onEdit={onEdit}
                onMaintenance={onMaintenance}
                onResolveMaintenance={onResolveMaintenance}
                onHistory={showHistory}
              />
            ))}
            {!rows.length && <EmptyInventoryRow />}
          </tbody>
        </table>
      </div>
      <InventoryPagination
        page={page}
        totalPages={totalPages}
        totalAssets={pageData?.totalElements || 0}
        setPage={setPage}
      />
      {assigning && (
        <AssignmentDialog
          asset={assigning}
          employees={employees}
          onClose={() => setAssigning(null)}
          onAssign={onAssign}
        />
      )}
      {historyAsset && (
        <AssignmentHistoryDialog
          asset={historyAsset}
          rows={historyRows}
          employees={employees}
          error={historyError}
          onClose={() => setHistoryAsset(null)}
        />
      )}
    </section>
  );
}
