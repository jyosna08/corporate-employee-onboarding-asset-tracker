package com.trainingmug.employee.mapper;

import com.trainingmug.employee.dto.EmployeeResponse;
import com.trainingmug.employee.dto.EmployeeSyncView;
import com.trainingmug.employee.entity.EmployeeEntity;
import org.springframework.stereotype.Component;

/** Converts employee persistence objects into API response DTOs. */
@Component
public class EmployeeMapper {
    public EmployeeResponse toResponse(EmployeeEntity employee) {
        var syncStates = employee.getSystemSyncs().stream()
                .map(sync -> new EmployeeSyncView(
                        sync.getSystemName(),
                        sync.getSyncStatus(),
                        sync.getFailureMessage(),
                        sync.getLastAttemptAt()))
                .toList();
        return new EmployeeResponse(
                employee.getId(),
                employee.getEmployeeNumber(),
                employee.getFirstName(),
                employee.getLastName(),
                employee.getEmail(),
                employee.getJobTitle(),
                employee.getDepartment(),
                employee.getManagerName(),
                employee.getStartDate(),
                employee.getOnboardingStatus(),
                employee.getEmploymentStatus(),
                syncStates,
                employee.getCreatedAt(),
                employee.getUpdatedAt());
    }
}
