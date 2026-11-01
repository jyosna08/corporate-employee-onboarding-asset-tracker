import { request } from './client.js';

export function listEmployees(token) {
  return request('/api/employees', { token });
}

export function createEmployee(token, form) {
  return request('/api/employees', { token, method: 'POST', body: form });
}

export function updateEmployee(token, id, form) {
  return request(`/api/employees/${id}`, { token, method: 'PUT', body: form });
}

export function advanceOnboarding(token, id) {
  return request(`/api/employees/${id}/onboarding/advance`, { token, method: 'POST' });
}

export function updateSystemSync(token, employee, sync, status, failureMessage) {
  return request(`/api/employees/${employee.id}/system-sync/${sync.system}`, {
    token,
    method: 'PATCH',
    body: { status, failureMessage },
  });
}

export function updateEmploymentStatus(token, id, status) {
  return request(`/api/employees/${id}/employment-status`, {
    token,
    method: 'PATCH',
    body: { status },
  });
}
