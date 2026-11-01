package com.trainingmug.gateway.controller;

import com.trainingmug.gateway.constants.GatewayConstants;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/** Aggregates employee onboarding, sync, and asset totals for the dashboard. */
@RestController
public class DashboardController {
    private static final ParameterizedTypeReference<List<Map<String, Object>>> EMPLOYEE_LIST =
            new ParameterizedTypeReference<>() { };
    private static final ParameterizedTypeReference<Map<String, Object>> ASSET_PAGE =
            new ParameterizedTypeReference<>() { };

    private final WebClient client;

    public DashboardController(WebClient.Builder builder) {
        this.client = builder.build();
    }

    @GetMapping(GatewayConstants.DASHBOARD_OVERVIEW_PATH)
    public Mono<Map<String, Object>> overview() {
        Mono<List<Map<String, Object>>> employees = loadEmployees();
        Mono<Map<String, Object>> assets = loadAssets();
        return Mono.zip(employees, assets).map(this::createOverview);
    }

    private Mono<List<Map<String, Object>>> loadEmployees() {
        return client.get()
                .uri(GatewayConstants.EMPLOYEE_SERVICE_URL)
                .retrieve()
                .bodyToMono(EMPLOYEE_LIST);
    }

    private Mono<Map<String, Object>> loadAssets() {
        return client.get()
                .uri(GatewayConstants.ASSET_SERVICE_URL)
                .retrieve()
                .bodyToMono(ASSET_PAGE);
    }

    private Map<String, Object> createOverview(
            reactor.util.function.Tuple2<List<Map<String, Object>>, Map<String, Object>> data
    ) {
        Map<String, Integer> onboardingCounts = initializeCounts(
                GatewayConstants.ONBOARDING_STATUSES);
        Map<String, Integer> syncCounts = initializeCounts(GatewayConstants.SYNC_STATUSES);
        Map<String, Integer> assetCounts = initializeCounts(GatewayConstants.ASSET_STATUSES);
        countEmployeeStates(data.getT1(), onboardingCounts, syncCounts);
        countAssetStates(data.getT2(), assetCounts);
        return Map.of(
                "employeesByOnboardingStatus", onboardingCounts,
                "systemSync", syncCounts,
                "assetsByLifecycleStatus", assetCounts);
    }

    private Map<String, Integer> initializeCounts(List<String> statuses) {
        Map<String, Integer> counts = new LinkedHashMap<>();
        statuses.forEach(status -> counts.put(status, 0));
        return counts;
    }

    private void countEmployeeStates(
            List<Map<String, Object>> employees,
            Map<String, Integer> onboardingCounts,
            Map<String, Integer> syncCounts
    ) {
        for (Map<String, Object> employee : employees) {
            increment(onboardingCounts, employee.get("onboardingStatus"));
            countSyncStates(employee.get("systemSyncs"), syncCounts);
        }
    }

    private void countSyncStates(Object rawStates, Map<String, Integer> syncCounts) {
        if (rawStates instanceof List<?> states) {
            for (Object state : states) {
                if (state instanceof Map<?, ?> syncState) {
                    increment(syncCounts, syncState.get("status"));
                }
            }
        }
    }

    private void countAssetStates(
            Map<String, Object> assetPage, Map<String, Integer> assetCounts
    ) {
        Object rawAssets = assetPage.get("content");
        if (!(rawAssets instanceof List<?>)) {
            rawAssets = assetPage.get("items");
        }
        if (rawAssets instanceof List<?> assets) {
            for (Object asset : assets) {
                if (asset instanceof Map<?, ?> assetRow) {
                    increment(assetCounts, assetRow.get("lifecycleStatus"));
                }
            }
        }
    }

    private void increment(Map<String, Integer> counts, Object status) {
        counts.computeIfPresent(String.valueOf(status), (key, count) -> count + 1);
    }
}
