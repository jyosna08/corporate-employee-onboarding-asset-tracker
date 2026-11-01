import { useCallback, useState } from 'react';
import AccessPage from './pages/AccessPage.jsx';
import AssetInventory from './components/AssetInventory.jsx';
import AssetFormDialog from './components/AssetFormDialog.jsx';
import AuthPage from './pages/AuthPage.jsx';
import DashboardPage from './pages/DashboardPage.jsx';
import EmployeeFormDialog from './components/EmployeeFormDialog.jsx';
import EmployeePage from './pages/EmployeePage.jsx';
import SettingsPage from './pages/SettingsPage.jsx';
import LoadingState from './components/LoadingState.jsx';
import Sidebar from './components/Sidebar.jsx';
import WorkspaceHeader from './components/WorkspaceHeader.jsx';
import LoadError from './components/LoadError.jsx';
import WorkspaceFooter from './components/WorkspaceFooter.jsx';
import WorkspaceHeading from './components/WorkspaceHeading.jsx';
import { EMPTY_ASSET_PAGE, PAGE_TITLES, UI_TIMING } from './constants/app.js';
import { useAssetActions } from './hooks/useAssetActions.js';
import { useAuth } from './hooks/useAuth.js';
import { useEmployeeActions } from './hooks/useEmployeeActions.js';
import { useWorkspaceData } from './hooks/useWorkspaceData.js';
import { canManageAssets, canManageEmployees, canManageSystemSync } from './utils/roles.js';

/** Wires pages, shared layout, and the application hooks together. */
export default function App() {
  const [page, setPage] = useState('overview');
  const [toast, setToast] = useState('');
  const [dialog, setDialog] = useState('');
  const [assetEditing, setAssetEditing] = useState(null);
  const auth = useAuth();
  const workspace = useWorkspaceData(auth.token, auth.clearSession);

  const notify = useCallback((message) => {
    setToast(message);
    window.setTimeout(() => setToast(''), UI_TIMING.toastMilliseconds);
  }, []);

  const employeeActions = useEmployeeActions(
    auth.token, workspace.reloadEmployees, notify);
  const assetActions = useAssetActions(
    auth.token,
    workspace.reloadAssets,
    workspace.reloadEmployees,
    workspace.refreshAssets,
    notify,
  );

  const handleLogin = useCallback(async (email, password) => {
    await auth.login(email, password);
    setPage('overview');
    notify('Signed in to your HR workspace');
  }, [auth.login, notify]);

  const handleLogout = useCallback(() => {
    auth.clearSession();
    workspace.clearWorkspace();
    setPage('overview');
  }, [auth.clearSession, workspace.clearWorkspace]);

  if (!auth.token) {
    return <AuthPage onLogin={handleLogin} onRegister={auth.register} />;
  }

  const canEditPeople = canManageEmployees(auth.role);
  const canEditAssets = canManageAssets(auth.role);
  const canEditSync = canManageSystemSync(auth.role);
  const pageTitle = PAGE_TITLES[page] || PAGE_TITLES.settings;
  const metrics = workspace.metrics || {
    employeesByOnboardingStatus: {},
    systemSync: {},
    assetsByLifecycleStatus: {},
  };

  return (
    <div className="min-h-screen bg-slate-50 font-sans text-ink">
      <Sidebar
        page={page}
        setPage={setPage}
        role={auth.role}
        user={auth.user}
        onLogout={handleLogout}
      />
      <main className="ml-[248px] min-h-screen max-lg:ml-[72px]">
        <WorkspaceHeader
          title={pageTitle}
          connected={workspace.connected}
          user={auth.user}
        />
        <div className="mx-auto max-w-[1500px] space-y-5 p-7 max-sm:p-4">
          <WorkspaceHeading connected={workspace.connected} title={pageTitle} />
          {workspace.loadError && <LoadError message={workspace.loadError} />}
          {workspace.loading
            ? <LoadingState />
            : renderPage({
              page,
              workspace,
              metrics,
              employeeActions,
              assetActions,
              canEditPeople,
              canEditAssets,
              canEditSync,
              role: auth.role,
              openEmployeeDialog: () => setDialog('employee'),
              openAssetDialog: (asset = null) => {
                setAssetEditing(asset);
                setDialog('asset');
              },
              navigateToAssets: () => setPage('assets'),
            })}
          <WorkspaceFooter connected={workspace.connected} />
        </div>
      </main>
      {dialog === 'employee' && (
        <EmployeeFormDialog
          onClose={() => setDialog('')}
          onSave={employeeActions.createEmployee}
        />
      )}
      {dialog === 'asset' && (
        <AssetFormDialog
          initial={assetEditing}
          onClose={() => {
            setDialog('');
            setAssetEditing(null);
          }}
          onSave={assetActions.saveAsset}
        />
      )}
      {toast && (
        <div className="fixed bottom-5 right-5 z-[60] rounded-xl bg-slate-900 px-4 py-3 text-[14px] text-white shadow-xl">
          {toast}
        </div>
      )}
    </div>
  );
}

function renderPage({
  page,
  workspace,
  metrics,
  employeeActions,
  assetActions,
  canEditPeople,
  canEditAssets,
  canEditSync,
  role,
  openEmployeeDialog,
  openAssetDialog,
  navigateToAssets,
}) {
  if (page === 'overview') {
    return (
      <DashboardPage
        employees={workspace.employees}
        assets={workspace.assets}
        metrics={metrics}
        onAdvance={employeeActions.advance}
        onSync={employeeActions.updateSync}
        onAdd={openEmployeeDialog}
        canEdit={canEditPeople}
        canSync={canEditSync}
        onGoAssets={navigateToAssets}
      />
    );
  }

  if (page === 'employees') {
    return (
      <EmployeePage
        employees={workspace.employees}
        onCreate={employeeActions.createEmployee}
        onUpdate={employeeActions.updateEmployee}
        onAdvance={employeeActions.advance}
        onSync={employeeActions.updateSync}
        onEmployment={employeeActions.toggleEmployment}
        canEdit={canEditPeople}
        canSync={canEditSync}
        role={role}
      />
    );
  }

  if (page === 'assets') {
    return (
      <AssetInventory
        pageData={workspace.assetPageData || EMPTY_ASSET_PAGE}
        search={workspace.assetSearch}
        setSearch={(value) => {
          workspace.setAssetSearch(value);
          workspace.setAssetPage(0);
        }}
        status={workspace.assetStatus}
        setStatus={(value) => {
          workspace.setAssetStatus(value);
          workspace.setAssetPage(0);
        }}
        sort={workspace.assetSort}
        setSort={workspace.setAssetSort}
        page={workspace.assetPage}
        setPage={workspace.setAssetPage}
        employees={workspace.employees}
        canEdit={canEditAssets}
        onAdd={() => openAssetDialog()}
        onEdit={(asset) => openAssetDialog(asset)}
        onAssign={assetActions.assignAsset}
        onReturn={assetActions.returnAsset}
        onMaintenance={assetActions.markMaintenance}
        onHistory={assetActions.loadHistory}
      />
    );
  }

  if (page === 'access') {
    return <AccessPage role={role} />;
  }

  return <SettingsPage />;
}
