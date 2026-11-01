package com.trainingmug.asset.repository;

import com.trainingmug.asset.entity.AssetEntity;
import com.trainingmug.asset.enums.AssetLifecycleStatus;
import com.trainingmug.asset.enums.AssetType;
import jakarta.persistence.LockModeType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.UUID;

/** Inventory persistence queries, including filtered search and row locking. */
public interface AssetRepository extends JpaRepository<AssetEntity, UUID> {
    @Query("select a from AssetEntity a where "
            + "(:status is null or a.lifecycleStatus = :status) and "
            + "(:type is null or a.type = :type) and "
            + "(lower(a.assetTag) like lower(concat('%', :search, '%')) or "
            + "lower(a.hardwareSpecs) like lower(concat('%', :search, '%')) or "
            + "lower(str(a.type)) like lower(concat('%', :search, '%')))")
    Page<AssetEntity> search(
            @Param("search") String search,
            @Param("status") AssetLifecycleStatus status,
            @Param("type") AssetType type,
            Pageable pageable);

    /** Prevents concurrent updates from assigning or returning one asset twice. */
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select a from AssetEntity a where a.id = :id")
    Optional<AssetEntity> findByIdForUpdate(@Param("id") UUID id);
}
