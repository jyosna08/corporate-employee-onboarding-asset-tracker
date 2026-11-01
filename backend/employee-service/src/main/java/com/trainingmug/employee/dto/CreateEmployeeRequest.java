package com.trainingmug.employee.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

/** Validated details needed to create an employee profile. */
public record CreateEmployeeRequest(
        @NotBlank @Size(max = 32) String employeeNumber,
        @NotBlank @Size(max = 100) String firstName,
        @NotBlank @Size(max = 100) String lastName,
        @NotBlank @Email @Size(max = 320) String email,
        @NotBlank @Size(max = 120) String jobTitle,
        @NotBlank @Size(max = 120) String department,
        @Size(max = 200) String managerName,
        @NotNull LocalDate startDate
) {
}
