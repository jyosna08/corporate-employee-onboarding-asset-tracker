import { request } from './client.js';

export function getDashboardOverview(token) {
  return request('/api/dashboard/overview', { token });
}
