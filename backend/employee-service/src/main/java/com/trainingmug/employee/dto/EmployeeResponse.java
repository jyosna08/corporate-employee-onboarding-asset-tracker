package com.trainingmug.employee.dto;

import com.trainingmug.employee.enums.EmploymentStatus;
import com.trainingmug.employee.enums.OnboardingStatus;

import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

/** Employee profile and onboarding state returned by the API. */
public record EmployeeResponse(
        UUID id,
        String employeeNumber,
        String firstName,
        String lastName,
        String email,
        String jobTitle,
        String department,
        String managerName,
        LocalDate startDate,
        OnboardingStatus onboardingStatus,
        EmploymentStatus employmentStatus,
        List<EmployeeSyncView> systemSyncs,
        Instant createdAt,
        Instant updatedAt
) {
}
