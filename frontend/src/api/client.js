const API_BASE = import.meta.env.VITE_API_BASE_URL || 'http://localhost:8080';

/** Sends a JSON request to the API gateway and normalizes API errors. */
export async function request(path, { token = '', method = 'GET', body, signal } = {}) {
  let response;
  try {
    response = await fetch(`${API_BASE}${path}`, {
      method,
      signal,
      headers: {
        Accept: 'application/json',
        ...(body ? { 'Content-Type': 'application/json' } : {}),
        ...(token ? { Authorization: `Bearer ${token}` } : {}),
      },
      ...(body ? { body: JSON.stringify(body) } : {}),
    });
  } catch (error) {
    if (error.name === 'AbortError') {
      throw error;
    }
    throw new Error(
      `Cannot reach the API Gateway at ${API_BASE}. Start Docker Desktop, then run `
      + '"docker compose up --build" from the project folder. If the gateway is already '
      + 'running, check that this frontend address is allowed in CORS.',
    );
  }

  const payload = response.status === 204
    ? null
    : await response.json().catch(() => null);
  if (!response.ok) {
    throw new Error(payload?.message || payload?.detail || `Request failed (${response.status})`);
  }
  return payload;
}
