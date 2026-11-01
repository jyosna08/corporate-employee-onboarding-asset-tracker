package com.trainingmug.employee.exception;

/** Indicates that the requested employee or integration state does not exist. */
public class EmployeeNotFoundException extends RuntimeException {
    public EmployeeNotFoundException(String message) {
        super(message);
    }
}
