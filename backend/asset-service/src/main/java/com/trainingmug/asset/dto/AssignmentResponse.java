package com.trainingmug.asset.dto;

import java.time.Instant;
import java.util.UUID;

/** One assignment record, including its return timestamp when closed. */
public record AssignmentResponse(
        UUID id,
        UUID assetId,
        UUID employeeId,
        Instant assignedAt,
        Instant returnedAt
) {
}
