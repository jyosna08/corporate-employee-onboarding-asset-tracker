import { useCallback } from 'react';
import * as assetApi from '../api/assets.js';

/** Provides asset operations and reloads the related workspace data. */
export function useAssetActions(
  token, reloadAssets, reloadEmployees, refreshAssets, notify,
) {
  const saveAsset = useCallback(async (form, id) => {
    if (id) {
      await assetApi.updateAsset(token, id, form);
    } else {
      await assetApi.createAsset(token, form);
    }
    refreshAssets();
    await reloadAssets();
    notify(id ? 'Asset updated' : 'Asset added');
  }, [token, reloadAssets, refreshAssets, notify]);

  const assignAsset = useCallback(async (asset, employeeId) => {
    await assetApi.assignAsset(token, asset.id, employeeId);
    refreshAssets();
    await reloadEmployees();
    notify('Asset assigned');
  }, [token, reloadEmployees, refreshAssets, notify]);

  const returnAsset = useCallback(async (asset) => {
    try {
      await assetApi.returnAsset(token, asset.id);
      refreshAssets();
      await reloadAssets();
    } catch (error) {
      notify(error.message || 'Could not return the asset.', 'error');
    }
  }, [token, reloadAssets, refreshAssets, notify]);

  const markMaintenance = useCallback(async (asset, reason) => {
    try {
      await assetApi.markAssetMaintenance(token, asset.id, reason);
      refreshAssets();
      await reloadAssets();
    } catch (error) {
      notify(error.message || 'Could not update the asset maintenance state.', 'error');
    }
  }, [token, reloadAssets, refreshAssets, notify]);

  const resolveMaintenance = useCallback(async (asset) => {
    try {
      await assetApi.resolveAssetMaintenance(token, asset.id);
      refreshAssets();
      await reloadAssets();
      notify('Maintenance resolved');
    } catch (error) {
      notify(error.message || 'Could not resolve asset maintenance.', 'error');
    }
  }, [token, reloadAssets, refreshAssets, notify]);

  const loadHistory = useCallback(async (asset) => {
    const result = await assetApi.getAssignmentHistory(token, asset.id);
    return result.content || [];
  }, [token]);

  return {
    saveAsset,
    assignAsset,
    returnAsset,
    markMaintenance,
    resolveMaintenance,
    loadHistory,
  };
}
