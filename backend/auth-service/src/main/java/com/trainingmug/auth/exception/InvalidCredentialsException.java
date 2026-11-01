package com.trainingmug.auth.exception;

/** Signals that login credentials are invalid or the account is inactive. */
public class InvalidCredentialsException extends RuntimeException {
    public InvalidCredentialsException() {
        super("Email or password is incorrect");
    }
}
