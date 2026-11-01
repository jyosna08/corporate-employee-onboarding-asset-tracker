package com.trainingmug.gateway.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.trainingmug.gateway.constants.GatewayConstants;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.Ordered;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Arrays;
import java.util.Base64;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/** Validates bearer tokens and forwards authenticated identity headers. */
@Component
public class JwtAuthorizationFilter implements WebFilter, Ordered {
    private static final String BEARER_PREFIX = "Bearer ";
    private static final String SIGNATURE_ALGORITHM = "HmacSHA256";
    private static final String JWT_SEPARATOR = ".";

    private final byte[] secret;
    private final ObjectMapper json;
    private final Set<String> allowedOrigins;
    private final RoleAccessPolicy roleAccessPolicy;

    public JwtAuthorizationFilter(
            @Value("${security.jwt.secret}") String secret,
            ObjectMapper json,
            @Value("${CORS_ALLOWED_ORIGINS}") String origins,
            RoleAccessPolicy roleAccessPolicy
    ) {
        this.secret = secret.getBytes(StandardCharsets.UTF_8);
        this.json = json;
        this.allowedOrigins = parseAllowedOrigins(origins);
        this.roleAccessPolicy = roleAccessPolicy;
        if (this.secret.length < 32) {
            throw new IllegalArgumentException("JWT secret must contain at least 32 bytes");
        }
    }

    private Set<String> parseAllowedOrigins(String origins) {
        return Arrays.stream(origins.split(","))
                .map(String::trim)
                .filter(origin -> !origin.isEmpty())
                .collect(Collectors.toUnmodifiableSet());
    }

    @Override
    public int getOrder() {
        return Ordered.HIGHEST_PRECEDENCE;
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        if (isPublicRequest(exchange)) {
            return chain.filter(exchange);
        }
        String authorization = exchange.getRequest().getHeaders()
                .getFirst(HttpHeaders.AUTHORIZATION);
        try {
            Map<String, Object> claims = validateAuthorizationHeader(authorization);
            String role = String.valueOf(claims.get("role"));
            String path = exchange.getRequest().getURI().getPath();
            String method = exchange.getRequest().getMethod().name();
            if (!roleAccessPolicy.isAllowed(path, method, role)) {
                return reject(exchange, HttpStatus.FORBIDDEN, "FORBIDDEN",
                        GatewayConstants.FORBIDDEN_MESSAGE);
            }
            return forwardIdentityHeaders(exchange, chain, claims, role);
        } catch (Exception exception) {
            return reject(exchange, HttpStatus.UNAUTHORIZED, "UNAUTHORIZED",
                    GatewayConstants.AUTHORIZATION_REQUIRED_MESSAGE);
        }
    }

    private boolean isPublicRequest(ServerWebExchange exchange) {
        String path = exchange.getRequest().getURI().getPath();
        String method = exchange.getRequest().getMethod().name();
        return "OPTIONS".equals(method)
                || GatewayConstants.LOGIN_PATH.equals(path)
                || GatewayConstants.REGISTER_PATH.equals(path)
                || path.startsWith(GatewayConstants.ACTUATOR_PATH_PREFIX);
    }

    private Map<String, Object> validateAuthorizationHeader(String header) throws Exception {
        if (header == null || !header.startsWith(BEARER_PREFIX)) {
            throw new IllegalArgumentException("Bearer token is required");
        }
        return validateToken(header.substring(BEARER_PREFIX.length()));
    }

    private Mono<Void> forwardIdentityHeaders(
            ServerWebExchange exchange,
            WebFilterChain chain,
            Map<String, Object> claims,
            String role
    ) {
        var request = exchange.getRequest().mutate()
                .headers(headers -> {
                    headers.remove(GatewayConstants.USER_ID_HEADER);
                    headers.remove(GatewayConstants.USER_ROLE_HEADER);
                    headers.set(GatewayConstants.USER_ID_HEADER,
                            String.valueOf(claims.get("sub")));
                    headers.set(GatewayConstants.USER_ROLE_HEADER, role);
                })
                .build();
        return chain.filter(exchange.mutate().request(request).build());
    }

    private Map<String, Object> validateToken(String token) throws Exception {
        String[] segments = token.split("\\.");
        if (segments.length != 3) {
            throw new IllegalArgumentException("Invalid JWT structure");
        }
        String signedContent = segments[0] + JWT_SEPARATOR + segments[1];
        validateSignature(signedContent, segments[2]);
        Map<String, Object> claims = readClaims(segments[1]);
        validateRequiredClaims(claims);
        return claims;
    }

    private void validateSignature(String signedContent, String encodedSignature)
            throws Exception {
        Mac mac = Mac.getInstance(SIGNATURE_ALGORITHM);
        mac.init(new SecretKeySpec(secret, SIGNATURE_ALGORITHM));
        byte[] expected = mac.doFinal(signedContent.getBytes(StandardCharsets.UTF_8));
        byte[] actual = Base64.getUrlDecoder().decode(encodedSignature);
        if (!java.security.MessageDigest.isEqual(expected, actual)) {
            throw new IllegalArgumentException("Invalid JWT signature");
        }
    }

    @SuppressWarnings("unchecked")
    private Map<String, Object> readClaims(String encodedPayload) throws Exception {
        byte[] payload = Base64.getUrlDecoder().decode(encodedPayload);
        return json.readValue(payload, Map.class);
    }

    private void validateRequiredClaims(Map<String, Object> claims) {
        if (!(claims.get("exp") instanceof Number expiration)
                || expiration.longValue() <= Instant.now().getEpochSecond()) {
            throw new IllegalArgumentException("JWT has expired or has no expiration");
        }
        if (!claims.containsKey("sub") || !claims.containsKey("role")) {
            throw new IllegalArgumentException("JWT is missing identity claims");
        }
    }

    private Mono<Void> reject(
            ServerWebExchange exchange, HttpStatus status, String code, String message
    ) {
        var response = exchange.getResponse();
        response.setStatusCode(status);
        response.getHeaders().setContentType(MediaType.APPLICATION_JSON);
        addAllowedOriginHeaders(exchange, response.getHeaders());
        byte[] body = serializeError(status, code, message);
        DataBuffer buffer = response.bufferFactory().wrap(body);
        return response.writeWith(Mono.just(buffer));
    }

    private void addAllowedOriginHeaders(ServerWebExchange exchange, HttpHeaders headers) {
        String origin = exchange.getRequest().getHeaders().getOrigin();
        if (origin != null && allowedOrigins.contains(origin)) {
            headers.setAccessControlAllowOrigin(origin);
            headers.setAccessControlAllowCredentials(true);
            headers.add(HttpHeaders.VARY, "Origin");
        }
    }

    private byte[] serializeError(HttpStatus status, String code, String message) {
        try {
            return json.writeValueAsBytes(Map.of(
                    "status", status.value(), "code", code, "message", message));
        } catch (Exception exception) {
            return new byte[0];
        }
    }
}
