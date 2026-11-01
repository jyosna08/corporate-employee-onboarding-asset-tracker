import { useCallback, useState } from 'react';
import * as authApi from '../api/auth.js';
import { STORAGE_KEYS } from '../constants/app.js';
import { readRole, readUser } from '../utils/auth.js';

function readStoredUser(token) {
  try {
    return JSON.parse(localStorage.getItem(STORAGE_KEYS.user) || 'null') || readUser(token);
  } catch {
    return readUser(token);
  }
}

/** Owns authentication state and browser storage for the current session. */
export function useAuth() {
  const [token, setToken] = useState(
    () => localStorage.getItem(STORAGE_KEYS.token) || '',
  );
  const [user, setUser] = useState(() => {
    const storedToken = localStorage.getItem(STORAGE_KEYS.token) || '';
    return readStoredUser(storedToken);
  });
  const [role, setRole] = useState(
    () => readRole(localStorage.getItem(STORAGE_KEYS.token) || ''),
  );

  const login = useCallback(async (email, password) => {
    const result = await authApi.login(email, password);
    localStorage.setItem(STORAGE_KEYS.token, result.accessToken);
    localStorage.setItem(STORAGE_KEYS.user, JSON.stringify(result.user));
    setUser(result.user);
    setRole(result.user.role);
    setToken(result.accessToken);
    return result.user;
  }, []);

  const register = useCallback((form) => authApi.register(form), []);

  const clearSession = useCallback(() => {
    localStorage.removeItem(STORAGE_KEYS.token);
    localStorage.removeItem(STORAGE_KEYS.user);
    setToken('');
    setUser(null);
    setRole('HR');
  }, []);

  return { token, user, role, login, register, clearSession };
}
