package com.trainingmug.asset.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;

import java.time.Instant;
import java.util.UUID;

/** Assignment history row with a logical employee UUID reference. */
@Entity
@Table(name = "asset_assignments", indexes = @Index(
        name = "ix_assignments_employee", columnList = "employee_id,assigned_at"))
public class AssetAssignmentEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "asset_id", nullable = false)
    private AssetEntity asset;

    // Employee IDs are plain UUID references; there is no cross-service foreign key.
    @Column(name = "employee_id", nullable = false)
    private UUID employeeId;

    @Column(name = "assigned_at", nullable = false, updatable = false)
    private Instant assignedAt;

    @Column(name = "returned_at")
    private Instant returnedAt;

    protected AssetAssignmentEntity() {
    }

    public AssetAssignmentEntity(AssetEntity asset, UUID employeeId) {
        this.asset = asset;
        this.employeeId = employeeId;
    }

    @PrePersist
    void onCreate() {
        assignedAt = Instant.now();
    }

    public UUID getId() { return id; }
    public AssetEntity getAsset() { return asset; }
    public UUID getEmployeeId() { return employeeId; }
    public Instant getAssignedAt() { return assignedAt; }
    public Instant getReturnedAt() { return returnedAt; }

    public void close() {
        returnedAt = Instant.now();
    }
}
