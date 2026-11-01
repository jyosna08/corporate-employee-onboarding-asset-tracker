package com.trainingmug.employee.dto;

import com.trainingmug.employee.enums.IntegrationSystem;
import com.trainingmug.employee.enums.SyncStatus;

import java.time.Instant;

/** Public synchronization state for one external system. */
public record EmployeeSyncView(
        IntegrationSystem system,
        SyncStatus status,
        String failureMessage,
        Instant lastAttemptAt
) {
}
