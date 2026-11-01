/** Empty table state for employee search results. */
export default function EmptyEmployeeRow() {
  return (
    <tr>
      <td colSpan="7" className="py-12 text-center text-[14px] text-slate-400">
        No employee profiles match this search.
      </td>
    </tr>
  );
}
