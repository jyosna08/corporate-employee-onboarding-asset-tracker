package com.trainingmug.asset.entity;

import com.trainingmug.asset.enums.AssetLifecycleStatus;
import com.trainingmug.asset.enums.AssetType;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.OneToMany;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import jakarta.persistence.Version;

import java.time.Instant;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/** Persistent inventory fields and lifecycle state for a company asset. */
@Entity
@Table(name = "assets", indexes = @Index(
        name = "ix_assets_lifecycle_type", columnList = "lifecycle_status,type"))
public class AssetEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "asset_tag", nullable = false, unique = true, length = 40)
    private String assetTag;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 80)
    private AssetType type;

    @Column(name = "hardware_specs", nullable = false, length = 1000)
    private String hardwareSpecs;

    @Column(name = "purchase_date", nullable = false)
    private LocalDate purchaseDate;

    @Enumerated(EnumType.STRING)
    @Column(name = "lifecycle_status", nullable = false, length = 24)
    private AssetLifecycleStatus lifecycleStatus = AssetLifecycleStatus.AVAILABLE;

    @Column(name = "maintenance_reason", length = 500)
    private String maintenanceReason;

    // Employee IDs are plain UUID references; there is no cross-service foreign key.
    @Column(name = "employee_id")
    private UUID employeeId;

    @OneToMany(mappedBy = "asset", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<AssetAssignmentEntity> assignments = new ArrayList<>();

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    @Version
    @Column(nullable = false)
    private long version;

    protected AssetEntity() {
    }

    public AssetEntity(String assetTag, AssetType type, String hardwareSpecs, LocalDate purchaseDate) {
        this.assetTag = assetTag;
        this.type = type;
        this.hardwareSpecs = hardwareSpecs;
        this.purchaseDate = purchaseDate;
    }

    @PrePersist
    void onCreate() {
        createdAt = Instant.now();
        updatedAt = createdAt;
    }

    @PreUpdate
    void onUpdate() {
        updatedAt = Instant.now();
    }

    public UUID getId() { return id; }
    public String getAssetTag() { return assetTag; }
    public AssetType getType() { return type; }
    public String getHardwareSpecs() { return hardwareSpecs; }
    public LocalDate getPurchaseDate() { return purchaseDate; }
    public AssetLifecycleStatus getLifecycleStatus() { return lifecycleStatus; }
    public String getMaintenanceReason() { return maintenanceReason; }
    public UUID getEmployeeId() { return employeeId; }
    public Instant getCreatedAt() { return createdAt; }
    public Instant getUpdatedAt() { return updatedAt; }

    public void update(String tag, AssetType type, String specs, LocalDate date) {
        assetTag = tag;
        this.type = type;
        hardwareSpecs = specs;
        purchaseDate = date;
    }

    /** Assignment is allowed only after the service confirms availability. */
    public void assign(UUID employee) {
        employeeId = employee;
        lifecycleStatus = AssetLifecycleStatus.CHECKED_OUT;
        maintenanceReason = null;
    }

    public void returnToInventory() {
        employeeId = null;
        lifecycleStatus = AssetLifecycleStatus.AVAILABLE;
        maintenanceReason = null;
    }

    public void markMaintenance(String reason) {
        employeeId = null;
        lifecycleStatus = AssetLifecycleStatus.MAINTENANCE_ALERT;
        maintenanceReason = reason;
    }

    /** Returns a repaired asset to inventory and removes its resolved maintenance note. */
    public void resolveMaintenance() {
        employeeId = null;
        lifecycleStatus = AssetLifecycleStatus.AVAILABLE;
        maintenanceReason = null;
    }
}
