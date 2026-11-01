/** Empty table state for inventory search and filtering. */
export default function EmptyInventoryRow() {
  return (
    <tr>
      <td colSpan="7" className="py-12 text-center text-[14px] text-slate-400">
        No assets match these filters.
      </td>
    </tr>
  );
}
