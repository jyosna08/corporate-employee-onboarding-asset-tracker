package com.trainingmug.gateway.constants;

import java.util.List;

/** Shared route, header, role, and dashboard values for the gateway. */
public final class GatewayConstants {
    public static final String API_PREFIX = "/api/";
    public static final String LOGIN_PATH = "/api/auth/login";
    public static final String REGISTER_PATH = "/api/auth/register";
    public static final String DASHBOARD_OVERVIEW_PATH = "/api/dashboard/overview";
    public static final String EMPLOYEE_PATH_PREFIX = "/api/employees";
    public static final String EMPLOYEE_SYNC_PATH_SEGMENT = "/system-sync/";
    public static final String ASSET_PATH_PREFIX = "/api/assets";
    public static final String ASSET_MAINTENANCE_PATH_SEGMENT = "/maintenance";
    public static final String ACTUATOR_PATH_PREFIX = "/actuator/";
    public static final String USER_ID_HEADER = "X-User-Id";
    public static final String USER_ROLE_HEADER = "X-User-Role";
    public static final String ADMIN_ROLE = "ADMIN";
    public static final String HR_ROLE = "HR";
    public static final String IT_ROLE = "IT";
    public static final String EMPLOYEE_SERVICE_URL = "https://hr-employee.onrender.com/api/employees";
    public static final int DASHBOARD_ASSET_PAGE_SIZE = 100;
    public static final String ASSET_SERVICE_URL =
            "https://hr-asset.onrender.com/api/assets?size=" + DASHBOARD_ASSET_PAGE_SIZE;
    public static final String AUTHORIZATION_REQUIRED_MESSAGE =
            "A valid bearer token is required";
    public static final String FORBIDDEN_MESSAGE = "Your role cannot perform this action";
    public static final List<String> ONBOARDING_STATUSES =
            List.of("PENDING_REVIEW", "IN_PROGRESS", "VERIFIED");
    public static final List<String> SYNC_STATUSES = List.of("PENDING", "FAILED", "SYNCED");
    public static final List<String> ASSET_STATUSES =
            List.of("AVAILABLE", "CHECKED_OUT", "MAINTENANCE_ALERT");

    private GatewayConstants() {
    }
}
