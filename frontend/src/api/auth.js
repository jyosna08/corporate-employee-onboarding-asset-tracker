import { request } from './client.js';

/** Authenticates a user and returns the access token response. */
export function login(email, password) {
  return request('/api/auth/login', {
    method: 'POST',
    body: { email, password },
  });
}

/** Creates a public HR account. */
export function register(form) {
  return request('/api/auth/register', { method: 'POST', body: form });
}
