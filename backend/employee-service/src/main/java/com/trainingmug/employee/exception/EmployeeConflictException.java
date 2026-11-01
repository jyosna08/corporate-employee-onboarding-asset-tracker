package com.trainingmug.employee.exception;

/** Indicates a profile conflict or an onboarding transition that is not allowed. */
public class EmployeeConflictException extends RuntimeException {
    public EmployeeConflictException(String message) {
        super(message);
    }
}
