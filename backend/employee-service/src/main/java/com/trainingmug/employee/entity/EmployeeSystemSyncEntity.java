package com.trainingmug.employee.entity;

import com.trainingmug.employee.enums.IntegrationSystem;
import com.trainingmug.employee.enums.SyncStatus;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

import java.time.Instant;
import java.util.UUID;

/** Persistence state for one external system integration per employee. */
@Entity
@Table(name = "employee_system_syncs", uniqueConstraints = @UniqueConstraint(
        name = "uk_employee_system", columnNames = {"employee_id", "system_name"}))
public class EmployeeSystemSyncEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "employee_id", nullable = false)
    private EmployeeEntity employee;

    @Enumerated(EnumType.STRING)
    @Column(name = "system_name", nullable = false, length = 16)
    private IntegrationSystem systemName;

    @Enumerated(EnumType.STRING)
    @Column(name = "sync_status", nullable = false, length = 16)
    private SyncStatus syncStatus = SyncStatus.PENDING;

    @Column(name = "failure_message", length = 1000)
    private String failureMessage;

    @Column(name = "last_attempt_at")
    private Instant lastAttemptAt;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    protected EmployeeSystemSyncEntity() {
    }

    public EmployeeSystemSyncEntity(EmployeeEntity employee, IntegrationSystem systemName) {
        this.employee = employee;
        this.systemName = systemName;
    }

    @PrePersist
    @PreUpdate
    void touch() {
        updatedAt = Instant.now();
    }

    public IntegrationSystem getSystemName() { return systemName; }
    public SyncStatus getSyncStatus() { return syncStatus; }
    public String getFailureMessage() { return failureMessage; }
    public Instant getLastAttemptAt() { return lastAttemptAt; }

    public void update(SyncStatus status, String message) {
        syncStatus = status;
        failureMessage = message;
        lastAttemptAt = Instant.now();
    }
}
