package com.trainingmug.asset.dto;

import java.util.UUID;

/** Employee fields consumed from employee-service during assignment. */
public record EmployeeServiceEmployeeResponse(UUID id, String employmentStatus) {
}
