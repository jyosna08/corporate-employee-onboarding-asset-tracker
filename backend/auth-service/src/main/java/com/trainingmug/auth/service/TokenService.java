package com.trainingmug.auth.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Base64;
import java.util.Map;
import java.util.UUID;

/** Issues signed JWT access tokens using the configured HMAC secret. */
@Service
public class TokenService {
    private static final Base64.Encoder ENCODER = Base64.getUrlEncoder().withoutPadding();
    private static final String SIGNATURE_ALGORITHM = "HmacSHA256";

    private final ObjectMapper mapper;
    private final byte[] secret;
    private final long expirationSeconds;

    public TokenService(
            ObjectMapper mapper,
            @Value("${security.jwt.secret}") String secret,
            @Value("${security.jwt.expiration-seconds}") long expirationSeconds
    ) {
        this.mapper = mapper;
        this.secret = secret.getBytes(StandardCharsets.UTF_8);
        this.expirationSeconds = expirationSeconds;
        if (this.secret.length < 32) {
            throw new IllegalArgumentException("JWT secret must contain at least 32 bytes");
        }
    }

    public String issue(UUID id, String fullName, String email, String role) {
        try {
            String header = encode(Map.of("alg", "HS256", "typ", "JWT"));
            String payload = encode(createClaims(id, fullName, email, role));
            String signingInput = header + "." + payload;
            byte[] signature = sign(signingInput);
            return signingInput + "." + ENCODER.encodeToString(signature);
        } catch (Exception exception) {
            throw new IllegalStateException("Could not issue access token", exception);
        }
    }

    private Map<String, Object> createClaims(
            UUID id, String fullName, String email, String role
    ) {
        return Map.of(
                "sub", id.toString(),
                "fullName", fullName,
                "email", email,
                "role", role,
                "iat", Instant.now().getEpochSecond(),
                "exp", Instant.now().plusSeconds(expirationSeconds).getEpochSecond());
    }

    private String encode(Map<String, Object> value) throws Exception {
        return ENCODER.encodeToString(mapper.writeValueAsBytes(value));
    }

    private byte[] sign(String signingInput) throws Exception {
        Mac mac = Mac.getInstance(SIGNATURE_ALGORITHM);
        mac.init(new SecretKeySpec(secret, SIGNATURE_ALGORITHM));
        return mac.doFinal(signingInput.getBytes(StandardCharsets.UTF_8));
    }

    public long getExpirationSeconds() {
        return expirationSeconds;
    }
}
