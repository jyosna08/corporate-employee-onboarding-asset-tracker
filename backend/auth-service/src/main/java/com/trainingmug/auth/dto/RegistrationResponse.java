package com.trainingmug.auth.dto;

import java.util.UUID;

/** Safe account details returned after registration. */
public record RegistrationResponse(UUID id, String fullName, String email, String role) {
}
