package com.trainingmug.auth.dto;

/** Login response containing the issued token and a safe user summary. */
public record LoginResponse(
        String accessToken,
        String tokenType,
        long expiresIn,
        UserView user
) {
}
