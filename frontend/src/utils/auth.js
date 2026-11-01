/** Decodes the non-sensitive display claims from a JWT. */
function readClaims(token) {
  try {
    const payload = token.split('.')[1];
    const base64 = payload.replace(/-/g, '+').replace(/_/g, '/');
    const bytes = Uint8Array.from(atob(base64), (character) => character.charCodeAt(0));
    return JSON.parse(new TextDecoder().decode(bytes));
  } catch {
    return {};
  }
}

export function readRole(token) {
  return readClaims(token).role || 'HR';
}

export function readUser(token) {
  const claims = readClaims(token);
  const email = claims.email || '';
  return {
    id: claims.sub || '',
    fullName: claims.fullName || email.split('@')[0] || 'HR user',
    email,
    role: claims.role || 'HR',
  };
}
