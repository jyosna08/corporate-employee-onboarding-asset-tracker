package com.trainingmug.employee.dto;

import com.trainingmug.employee.enums.SyncStatus;
import jakarta.validation.constraints.NotNull;

/** Requested external-system synchronization result. */
public record SyncRequest(@NotNull SyncStatus status, String failureMessage) {
}
