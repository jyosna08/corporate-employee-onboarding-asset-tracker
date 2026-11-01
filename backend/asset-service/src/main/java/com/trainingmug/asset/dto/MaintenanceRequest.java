package com.trainingmug.asset.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/** Required maintenance reason for an asset. */
public record MaintenanceRequest(@NotBlank @Size(max = 500) String reason) {
}
