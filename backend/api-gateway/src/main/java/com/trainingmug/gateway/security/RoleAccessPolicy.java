package com.trainingmug.gateway.security;

import com.trainingmug.gateway.constants.GatewayConstants;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;

/** Centralizes the existing role and route authorization rules. */
@Component
public class RoleAccessPolicy {
    public boolean isAllowed(String path, String method, String role) {
        if (!path.startsWith(GatewayConstants.API_PREFIX)) {
            return true;
        }
        if (GatewayConstants.ADMIN_ROLE.equals(role)) {
            return true;
        }
        if (isDashboardPath(path)) {
            return canReadDashboard(method, role);
        }
        if (path.startsWith(GatewayConstants.EMPLOYEE_PATH_PREFIX)) {
            return canAccessEmployeePath(path, method, role);
        }
        if (path.startsWith(GatewayConstants.ASSET_PATH_PREFIX)) {
            return canAccessAssetPath(path, method, role);
        }
        return false;
    }

    private boolean isDashboardPath(String path) {
        return GatewayConstants.DASHBOARD_OVERVIEW_PATH.equals(path);
    }

    private boolean canReadDashboard(String method, String role) {
        return HttpMethod.GET.matches(method)
                && (GatewayConstants.HR_ROLE.equals(role) || GatewayConstants.IT_ROLE.equals(role));
    }

    private boolean canAccessEmployeePath(String path, String method, String role) {
        if (HttpMethod.GET.matches(method)) {
            return true;
        }
        if (path.contains(GatewayConstants.EMPLOYEE_SYNC_PATH_SEGMENT)) {
            return GatewayConstants.IT_ROLE.equals(role);
        }
        return GatewayConstants.HR_ROLE.equals(role) && isWriteMethod(method);
    }

    private boolean canAccessAssetPath(String path, String method, String role) {
        if (HttpMethod.GET.matches(method)) {
            return true;
        }
        if (!isWriteMethod(method)) {
            return false;
        }
        if (GatewayConstants.IT_ROLE.equals(role)) {
            return true;
        }
        return GatewayConstants.HR_ROLE.equals(role)
                && !path.endsWith("/maintenance");
    }

    private boolean isWriteMethod(String method) {
        return HttpMethod.POST.matches(method)
                || HttpMethod.PUT.matches(method)
                || HttpMethod.PATCH.matches(method);
    }
}
