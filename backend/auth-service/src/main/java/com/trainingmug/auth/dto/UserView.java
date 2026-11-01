package com.trainingmug.auth.dto;

import java.util.UUID;

/** Public user fields returned as part of a successful login. */
public record UserView(UUID id, String fullName, String email, String role) {
}
