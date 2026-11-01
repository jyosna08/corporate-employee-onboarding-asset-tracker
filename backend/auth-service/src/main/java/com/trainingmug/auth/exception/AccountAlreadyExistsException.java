package com.trainingmug.auth.exception;

/** Signals a registration conflict for an email address already in use. */
public class AccountAlreadyExistsException extends RuntimeException {
    public AccountAlreadyExistsException() {
        super("An account already exists for this email");
    }
}
