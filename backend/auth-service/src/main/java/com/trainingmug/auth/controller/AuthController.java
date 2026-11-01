package com.trainingmug.auth.controller;

import com.trainingmug.auth.dto.LoginRequest;
import com.trainingmug.auth.dto.LoginResponse;
import com.trainingmug.auth.dto.RegisterRequest;
import com.trainingmug.auth.dto.RegistrationResponse;
import com.trainingmug.auth.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/** Exposes authentication endpoints and delegates work to the service layer. */
@RestController
@RequestMapping("/api/auth")
public class AuthController {
    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
        return ResponseEntity.ok(authService.login(request));
    }

    @PostMapping("/register")
    public ResponseEntity<RegistrationResponse> register(
            @Valid @RequestBody RegisterRequest request
    ) {
        return ResponseEntity.status(201).body(authService.register(request));
    }

    @GetMapping("/health")
    public ResponseEntity<Void> health() {
        return ResponseEntity.noContent().build();
    }
}
