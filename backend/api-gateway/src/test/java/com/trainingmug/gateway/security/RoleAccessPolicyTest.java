package com.trainingmug.gateway.security;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class RoleAccessPolicyTest {
    private final RoleAccessPolicy policy = new RoleAccessPolicy();

    @Test void enforcesRoleMatrixForEmployeeAndAssetRoutes() {
        String employees = "/api/employees";
        String employee = employees + "/123";
        String assets = "/api/assets";
        String asset = assets + "/123";

        for (String method : new String[]{"GET"}) {
            for (String role : new String[]{"ADMIN", "HR", "IT"}) {
                assertAllowed(true, employees, method, role);
                assertAllowed(true, assets, method, role);
            }
        }

        assertAllowed(true, employees, "POST", "ADMIN");
        assertAllowed(true, asset + "/maintenance/resolve", "POST", "ADMIN");
        assertAllowed(true, employee + "/system-sync/HRIS", "PATCH", "ADMIN");
        for (String role : new String[]{"IT"}) {
            assertAllowed(true, asset + "/maintenance", "POST", role);
            assertAllowed(true, asset + "/maintenance/resolve", "POST", role);
        }
        assertAllowed(true, employees, "POST", "HR");
        assertAllowed(true, employee, "PUT", "HR");
        assertAllowed(true, employee + "/onboarding/advance", "POST", "HR");
        assertAllowed(true, assets, "POST", "HR");
        assertAllowed(true, asset, "PUT", "HR");
        assertAllowed(true, asset + "/assignments", "POST", "HR");
        assertAllowed(true, asset + "/return", "POST", "HR");
        assertAllowed(false, asset + "/maintenance", "POST", "HR");
        assertAllowed(false, asset + "/maintenance/resolve", "POST", "HR");
        assertAllowed(false, employee + "/system-sync/HRIS", "PATCH", "HR");
        assertAllowed(false, employees, "POST", "IT");
        assertAllowed(true, assets, "POST", "IT");
        assertAllowed(true, asset, "PUT", "IT");
        assertAllowed(true, asset + "/assignments", "POST", "IT");
        assertAllowed(true, asset + "/return", "POST", "IT");
        assertAllowed(true, asset + "/maintenance", "POST", "IT");
        assertAllowed(true, asset + "/maintenance/resolve", "POST", "IT");
        assertAllowed(false, employees, "POST", "IT");
        assertAllowed(true, employee + "/system-sync/HRIS", "PATCH", "IT");
        assertAllowed(false, "/api/unknown", "GET", "HR");
    }

    private void assertAllowed(boolean expected, String path, String method, String role) {
        assertEquals(expected, policy.isAllowed(path, method, role),
                () -> role + " " + method + " " + path);
    }
}
