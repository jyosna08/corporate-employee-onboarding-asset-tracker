package com.trainingmug.employee.dto;

import com.trainingmug.employee.enums.EmploymentStatus;
import jakarta.validation.constraints.NotNull;

/** Requested employment status change. */
public record EmploymentStatusRequest(@NotNull EmploymentStatus status) {
}
