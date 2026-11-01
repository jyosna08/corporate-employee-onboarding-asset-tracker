package com.trainingmug.asset.exception;

/** Indicates that employee-service could not be reached to validate assignment. */
public class EmployeeServiceUnavailableException extends RuntimeException {
    public EmployeeServiceUnavailableException() {
        super("Employee service is unavailable; please retry shortly");
    }
}
