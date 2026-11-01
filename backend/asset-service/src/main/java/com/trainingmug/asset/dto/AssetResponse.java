package com.trainingmug.asset.dto;

import com.trainingmug.asset.enums.AssetLifecycleStatus;
import com.trainingmug.asset.enums.AssetType;

import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

/** Asset profile, lifecycle state, and current employee reference. */
public record AssetResponse(
        UUID id,
        String assetTag,
        AssetType type,
        String hardwareSpecs,
        LocalDate purchaseDate,
        AssetLifecycleStatus lifecycleStatus,
        String maintenanceReason,
        UUID employeeId,
        Instant assignedAt,
        Instant createdAt,
        Instant updatedAt
) {
}
