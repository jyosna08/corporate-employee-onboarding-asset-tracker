const TABLE_HEADERS = [
  'ASSET TAG', 'DEVICE', 'ASSIGNED TO', 'PURCHASED',
  'HARDWARE SPECS', 'STATUS', 'ACTIONS',
];

/** Column headings for the asset inventory table. */
export default function InventoryTableHeader() {
  return (
    <thead className="bg-slate-50 text-[10px] font-bold tracking-wider text-slate-400">
      <tr>
        {TABLE_HEADERS.map((header) => (
          <th key={header} className="px-4 py-3">{header}</th>
        ))}
      </tr>
    </thead>
  );
}
