import { useCallback } from 'react';
import * as employeeApi from '../api/employees.js';
import { EMPLOYMENT_STATUS } from '../constants/statuses.js';

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

  const updateSync = useCallback(async (employee, sync, status, failureMessage) => {
    try {
      await employeeApi.updateSystemSync(token, employee, sync, status, failureMessage);
      await reloadEmployees();
      notify(`${sync.system} sync updated to ${status}`);
      return true;
    } catch (error) {
      notify(error.message || 'Could not update system sync.', 'error');
      return false;
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
      notify(error.message || 'Could not update employment status.', 'error');
    }
  }, [token, reloadEmployees, notify]);

  return { createEmployee, updateEmployee, advance, updateSync, toggleEmployment };
}
