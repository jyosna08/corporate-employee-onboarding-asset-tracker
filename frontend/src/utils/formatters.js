export function formatDate(value) {
  return value ? new Date(value).toLocaleDateString() : '';
}

export function formatStatus(value) {
  return String(value || 'Unknown').replaceAll('_', ' ');
}
