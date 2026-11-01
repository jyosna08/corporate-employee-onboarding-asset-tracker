package com.trainingmug.asset.repository;

import com.trainingmug.asset.entity.AssetAssignmentEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

/** Assignment lookups for open assignments and asset history. */
public interface AssetAssignmentRepository
        extends JpaRepository<AssetAssignmentEntity, UUID> {
    Optional<AssetAssignmentEntity> findFirstByAssetIdAndReturnedAtIsNull(UUID assetId);

    Page<AssetAssignmentEntity> findByAssetIdOrderByAssignedAtDesc(
            UUID assetId, Pageable pageable);
}
