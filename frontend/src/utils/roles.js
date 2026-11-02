export function canManageEmployees(role) {
  return role === 'ADMIN' || role === 'HR';
}

export function canManageAssets(role) {
  return role === 'ADMIN' || role === 'HR' || role === 'IT';
}

export function canManageAssetMaintenance(role) {
  return role === 'ADMIN' || role === 'IT';
}

export function canManageSystemSync(role) {
  return role === 'ADMIN' || role === 'IT';
}
