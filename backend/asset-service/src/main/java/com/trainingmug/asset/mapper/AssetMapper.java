package com.trainingmug.asset.mapper;

import com.trainingmug.asset.dto.AssignmentResponse;
import com.trainingmug.asset.dto.AssetResponse;
import com.trainingmug.asset.entity.AssetAssignmentEntity;
import com.trainingmug.asset.entity.AssetEntity;
import com.trainingmug.asset.repository.AssetAssignmentRepository;
import org.springframework.stereotype.Component;

/** Converts asset entities to API responses and includes current assignment time. */
@Component
public class AssetMapper {
    private final AssetAssignmentRepository assignments;

    public AssetMapper(AssetAssignmentRepository assignments) {
        this.assignments = assignments;
    }

    public AssetResponse toResponse(AssetEntity asset) {
        var currentAssignment = assignments
                .findFirstByAssetIdAndReturnedAtIsNull(asset.getId())
                .orElse(null);
        var assignedAt = currentAssignment == null
                ? null
                : currentAssignment.getAssignedAt();
        return new AssetResponse(
                asset.getId(), asset.getAssetTag(), asset.getType(),
                asset.getHardwareSpecs(), asset.getPurchaseDate(),
                asset.getLifecycleStatus(), asset.getMaintenanceReason(),
                asset.getEmployeeId(), assignedAt, asset.getCreatedAt(), asset.getUpdatedAt());
    }

    public AssignmentResponse toResponse(AssetAssignmentEntity assignment) {
        return new AssignmentResponse(
                assignment.getId(), assignment.getAsset().getId(),
                assignment.getEmployeeId(), assignment.getAssignedAt(),
                assignment.getReturnedAt());
    }
}
