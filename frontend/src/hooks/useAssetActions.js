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
    try {
      await assetApi.assignAsset(token, asset.id, employeeId);
      refreshAssets();
      await reloadEmployees();
      notify('Asset assigned');
    } catch (error) {
      notify(error.message);
    }
  }, [token, reloadEmployees, refreshAssets, notify]);

  const returnAsset = useCallback(async (asset) => {
    try {
      await assetApi.returnAsset(token, asset.id);
      refreshAssets();
      await reloadAssets();
    } catch (error) {
      notify(error.message);
    }
  }, [token, reloadAssets, refreshAssets, notify]);

  const markMaintenance = useCallback(async (asset, reason) => {
    try {
      await assetApi.markAssetMaintenance(token, asset.id, reason);
      refreshAssets();
      await reloadAssets();
    } catch (error) {
      notify(error.message);
    }
  }, [token, reloadAssets, refreshAssets, notify]);

  const loadHistory = useCallback(async (asset) => {
    const result = await assetApi.getAssignmentHistory(token, asset.id);
    return result.content || [];
  }, [token]);

  return { saveAsset, assignAsset, returnAsset, markMaintenance, loadHistory };
}
