package com.trainingmug.asset.dto;

import com.trainingmug.asset.enums.AssetType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

/** Validated details required to register an asset. */
public record CreateAssetRequest(
        @NotBlank @Size(max = 40) String assetTag,
        @NotNull AssetType type,
        @NotBlank @Size(max = 1000) String hardwareSpecs,
        @NotNull LocalDate purchaseDate
) {
}
