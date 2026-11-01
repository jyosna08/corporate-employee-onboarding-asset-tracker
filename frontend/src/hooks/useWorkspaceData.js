import { useCallback, useEffect, useState } from 'react';
import * as assetApi from '../api/assets.js';
import * as dashboardApi from '../api/dashboard.js';
import * as employeeApi from '../api/employees.js';
import { EMPTY_ASSET_PAGE } from '../constants/app.js';
import { ASSET_SORTS } from '../constants/app.js';

/** Loads dashboard resources and manages asset inventory filters and paging. */
export function useWorkspaceData(token, onUnauthorized) {
  const [employees, setEmployees] = useState([]);
  const [assets, setAssets] = useState([]);
  const [metrics, setMetrics] = useState(null);
  const [connected, setConnected] = useState(false);
  const [loading, setLoading] = useState(false);
  const [loadError, setLoadError] = useState('');
  const [assetSearch, setAssetSearch] = useState('');
  const [assetStatus, setAssetStatus] = useState('');
  const [assetSort, setAssetSort] = useState(ASSET_SORTS.default);
  const [assetPage, setAssetPage] = useState(0);
  const [assetPageData, setAssetPageData] = useState(EMPTY_ASSET_PAGE);
  const [refresh, setRefresh] = useState(0);

  const reloadEmployees = useCallback(async () => {
    if (!token) return;
    const [people, summary] = await Promise.all([
      employeeApi.listEmployees(token),
      dashboardApi.getDashboardOverview(token),
    ]);
    setEmployees(people);
    setMetrics(summary);
  }, [token]);

  const reloadAssets = useCallback(async () => {
    if (!token) return;
    const result = await assetApi.listAssets(token, {
      page: assetPage,
      size: 20,
      sort: assetSort,
      search: assetSearch,
      status: assetStatus,
    });
    setAssetPageData(result);
    setAssets(result.content || []);
  }, [token, assetPage, assetSearch, assetStatus, assetSort, refresh]);

  const refreshAssets = useCallback(() => {
    setRefresh((value) => value + 1);
  }, []);

  useEffect(() => {
    if (!token) {
      setConnected(false);
      setLoading(false);
      return undefined;
    }

    let active = true;
    setLoading(true);
    setLoadError('');
    Promise.all([
      employeeApi.listEmployees(token),
      dashboardApi.getDashboardOverview(token),
      assetApi.listAssets(token, { page: assetPage, size: 20, sort: assetSort }),
    ])
      .then(([people, summary, assetResult]) => {
        if (!active) return;
        setEmployees(people);
        setMetrics(summary);
        setAssets(assetResult.content || []);
        setAssetPageData(assetResult);
        setConnected(true);
      })
      .catch((error) => {
        if (!active) return;
        setLoadError(error.message);
        const message = error.message.toLowerCase();
        if (message.includes('401') || message.includes('token')) {
          onUnauthorized();
        }
      })
      .finally(() => {
        if (active) setLoading(false);
      });

    return () => {
      active = false;
    };
  }, [token, onUnauthorized]);

  useEffect(() => {
    if (token && connected) {
      reloadAssets().catch((error) => setLoadError(error.message));
    }
  }, [assetPage, assetSearch, assetStatus, assetSort, refresh, token, connected, reloadAssets]);

  const clearWorkspace = useCallback(() => {
    setConnected(false);
    setEmployees([]);
    setAssets([]);
    setMetrics(null);
    setAssetPageData(EMPTY_ASSET_PAGE);
  }, []);

  return {
    employees,
    assets,
    metrics,
    connected,
    loading,
    loadError,
    setLoadError,
    assetSearch,
    setAssetSearch,
    assetStatus,
    setAssetStatus,
    assetSort,
    setAssetSort,
    assetPage,
    setAssetPage,
    assetPageData,
    reloadEmployees,
    reloadAssets,
    refreshAssets,
    clearWorkspace,
  };
}
