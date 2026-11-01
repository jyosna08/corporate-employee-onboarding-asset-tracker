import { useCallback } from 'react';
import * as employeeApi from '../api/employees.js';
import { EMPLOYMENT_STATUS, SYNC_STATUS } from '../constants/statuses.js';

/** Provides employee actions and refreshes employee and dashboard data. */
export function useEmployeeActions(token, reloadEmployees, notify) {
  const createEmployee = useCallback(async (form) => {
    await employeeApi.createEmployee(token, form);
    await reloadEmployees();
    notify('Employee profile created');
  }, [token, reloadEmployees, notify]);

  const updateEmployee = useCallback(async (id, form) => {
    await employeeApi.updateEmployee(token, id, form);
    await reloadEmployees();
    notify('Employee profile updated');
  }, [token, reloadEmployees, notify]);

  const advance = useCallback(async (employee) => {
    try {
      await employeeApi.advanceOnboarding(token, employee.id);
      await reloadEmployees();
    } catch (error) {
      notify(error.message);
    }
  }, [token, reloadEmployees, notify]);

  const updateSync = useCallback(async (employee, sync) => {
    const next = sync.status === SYNC_STATUS.pending
      ? SYNC_STATUS.synced
      : sync.status === SYNC_STATUS.synced
        ? SYNC_STATUS.failed
        : SYNC_STATUS.pending;
    const failureMessage = next === SYNC_STATUS.failed
      ? `${sync.system} profile sync failed. Check the integration connection.`
      : null;
    try {
      await employeeApi.updateSystemSync(token, employee, sync, next, failureMessage);
      await reloadEmployees();
    } catch (error) {
      notify(error.message);
    }
  }, [token, reloadEmployees, notify]);

  const toggleEmployment = useCallback(async (employee) => {
    const status = employee.employmentStatus === EMPLOYMENT_STATUS.active
      ? EMPLOYMENT_STATUS.terminated
      : EMPLOYMENT_STATUS.active;
    try {
      await employeeApi.updateEmploymentStatus(token, employee.id, status);
      await reloadEmployees();
    } catch (error) {
      notify(error.message);
    }
  }, [token, reloadEmployees, notify]);

  return { createEmployee, updateEmployee, advance, updateSync, toggleEmployment };
}
