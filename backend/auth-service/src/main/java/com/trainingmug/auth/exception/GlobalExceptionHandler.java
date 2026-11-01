package com.trainingmug.auth.exception;

import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.http.converter.HttpMessageNotReadableException;

import java.time.Instant;
import java.util.List;
import java.util.Map;

/** Keeps authentication failures in a stable API error envelope. */
@RestControllerAdvice
public class GlobalExceptionHandler {
    private static final Logger LOGGER = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(InvalidCredentialsException.class)
    public ResponseEntity<Map<String, Object>> unauthorized(
            InvalidCredentialsException exception, HttpServletRequest request
    ) {
        return error(HttpStatus.UNAUTHORIZED, "INVALID_CREDENTIALS",
                exception.getMessage(), List.of(), request);
    }

    @ExceptionHandler(AccountAlreadyExistsException.class)
    public ResponseEntity<Map<String, Object>> duplicate(
            AccountAlreadyExistsException exception, HttpServletRequest request
    ) {
        return error(HttpStatus.CONFLICT, "ACCOUNT_EXISTS", exception.getMessage(),
                List.of(), request);
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<Map<String, Object>> dataConflict(HttpServletRequest request) {
        return error(HttpStatus.CONFLICT, "ACCOUNT_EXISTS",
                "An account already exists for this email", List.of(), request);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> validation(
            MethodArgumentNotValidException exception, HttpServletRequest request
    ) {
        List<Map<String, String>> details = exception.getBindingResult().getFieldErrors()
                .stream()
                .map(fieldError -> Map.of(
                        "field", fieldError.getField(),
                        "message", nonEmpty(fieldError.getDefaultMessage(), "Invalid value")))
                .toList();
        return error(HttpStatus.BAD_REQUEST, "VALIDATION_ERROR",
                "Request validation failed", details, request);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<Map<String, Object>> malformedJson(HttpServletRequest request) {
        return error(HttpStatus.BAD_REQUEST, "MALFORMED_JSON",
                "Request body is missing or contains malformed JSON", List.of(), request);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> unexpected(
            Exception exception, HttpServletRequest request
    ) {
        LOGGER.error("Unexpected authentication API failure", exception);
        return error(HttpStatus.INTERNAL_SERVER_ERROR, "INTERNAL_ERROR",
                "An unexpected error occurred", List.of(), request);
    }

    private ResponseEntity<Map<String, Object>> error(
            HttpStatus status, String code, String message,
            List<?> details, HttpServletRequest request
    ) {
        Map<String, Object> body = Map.of(
                "timestamp", Instant.now().toString(),
                "status", status.value(),
                "code", code,
                "message", nonEmpty(message, "Request failed"),
                "path", request.getRequestURI(),
                "details", details);
        return ResponseEntity.status(status).body(body);
    }

    private String nonEmpty(String value, String fallback) {
        return value == null || value.isBlank() ? fallback : value;
    }
}
