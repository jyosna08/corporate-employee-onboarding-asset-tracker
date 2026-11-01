package com.trainingmug.asset.dto;

import jakarta.validation.constraints.NotNull;

import java.util.UUID;

/** Employee identifier for an asset assignment. */
public record AssignAssetRequest(@NotNull UUID employeeId) {
}
