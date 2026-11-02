import { request } from './client.js';

export function listAssets(token, filters) {
  const query = new URLSearchParams({
    page: String(filters.page),
    size: String(filters.size ?? 20),
    sort: filters.sort,
  });
  if (filters.search) query.set('search', filters.search);
  if (filters.status) query.set('status', filters.status);
  return request(`/api/assets?${query}`, { token });
}

export function createAsset(token, form) {
  return request('/api/assets', { token, method: 'POST', body: form });
}

export function updateAsset(token, id, form) {
  return request(`/api/assets/${id}`, { token, method: 'PUT', body: form });
}

export function assignAsset(token, assetId, employeeId) {
  return request(`/api/assets/${assetId}/assignments`, {
    token,
    method: 'POST',
    body: { employeeId },
  });
}

export function returnAsset(token, assetId) {
  return request(`/api/assets/${assetId}/return`, { token, method: 'POST' });
}

export function markAssetMaintenance(token, assetId, reason) {
  return request(`/api/assets/${assetId}/maintenance`, {
    token,
    method: 'POST',
    body: { reason },
  });
}

export function resolveAssetMaintenance(token, assetId) {
  return request(`/api/assets/${assetId}/maintenance/resolve`, {
    token,
    method: 'POST',
  });
}

export function getAssignmentHistory(token, assetId) {
  return request(`/api/assets/${assetId}/assignments?page=0&size=50`, { token });
}
