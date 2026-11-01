package com.trainingmug.asset.exception;

/** Indicates that employee-service confirmed the employee does not exist. */
public class EmployeeNotFoundException extends RuntimeException {
    public EmployeeNotFoundException() {
        super("Employee not found");
    }
}
