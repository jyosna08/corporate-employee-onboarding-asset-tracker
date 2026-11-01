package com.trainingmug.auth.service;

import com.trainingmug.auth.dto.LoginRequest;
import com.trainingmug.auth.dto.LoginResponse;
import com.trainingmug.auth.dto.RegisterRequest;
import com.trainingmug.auth.dto.RegistrationResponse;
import com.trainingmug.auth.entity.UserEntity;
import com.trainingmug.auth.enums.UserRole;
import com.trainingmug.auth.exception.AccountAlreadyExistsException;
import com.trainingmug.auth.exception.InvalidCredentialsException;
import com.trainingmug.auth.mapper.AuthMapper;
import com.trainingmug.auth.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Locale;

/** Handles authentication and account registration business rules. */
@Service
public class AuthService {
    private static final Logger LOGGER = LoggerFactory.getLogger(AuthService.class);

    private final UserRepository users;
    private final PasswordEncoder passwords;
    private final TokenService tokens;
    private final AuthMapper mapper;

    public AuthService(
            UserRepository users,
            PasswordEncoder passwords,
            TokenService tokens,
            AuthMapper mapper
    ) {
        this.users = users;
        this.passwords = passwords;
        this.tokens = tokens;
        this.mapper = mapper;
    }

    /** Rejects inactive accounts and credentials that do not match. */
    public LoginResponse login(LoginRequest request) {
        var user = users.findByEmailIgnoreCase(request.email().trim())
                .orElseThrow(InvalidCredentialsException::new);
        if (!user.isActive() || !passwords.matches(request.password(), user.getPasswordHash())) {
            LOGGER.warn("Rejected login for inactive account or invalid credentials");
            throw new InvalidCredentialsException();
        }
        LOGGER.info("User authenticated: {}", user.getId());
        return mapper.toLoginResponse(user, tokens.issue(
                user.getId(), user.getFullName(), user.getEmail(), user.getRole().name()),
                tokens.getExpirationSeconds());
    }

    /** Creates a public registration as an HR user, preserving the existing role rule. */
    public RegistrationResponse register(RegisterRequest request) {
        String email = request.email().trim().toLowerCase(Locale.ROOT);
        if (users.existsByEmailIgnoreCase(email)) {
            LOGGER.warn("Rejected registration for an existing email address");
            throw new AccountAlreadyExistsException();
        }
        try {
            UserEntity user = users.saveAndFlush(new UserEntity(
                    request.fullName().trim(), email,
                    passwords.encode(request.password()), UserRole.HR));
            LOGGER.info("Registered new user: {}", user.getId());
            return mapper.toRegistrationResponse(user);
        } catch (DataIntegrityViolationException exception) {
            LOGGER.warn("Registration conflicted with an existing account");
            throw new AccountAlreadyExistsException();
        }
    }
}
