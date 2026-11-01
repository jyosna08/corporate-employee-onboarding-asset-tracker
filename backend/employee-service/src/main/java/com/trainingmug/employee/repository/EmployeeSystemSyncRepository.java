package com.trainingmug.employee.repository;

import com.trainingmug.employee.entity.EmployeeSystemSyncEntity;
import com.trainingmug.employee.enums.IntegrationSystem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

/** Queries synchronization state using its owning employee and system name. */
public interface EmployeeSystemSyncRepository
        extends JpaRepository<EmployeeSystemSyncEntity, UUID> {
    Optional<EmployeeSystemSyncEntity> findByEmployeeIdAndSystemName(
            UUID employeeId, IntegrationSystem systemName);
}
