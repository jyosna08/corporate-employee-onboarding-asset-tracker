package com.trainingmug.auth.config;

import com.trainingmug.auth.entity.UserEntity;
import com.trainingmug.auth.enums.UserRole;
import com.trainingmug.auth.repository.UserRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Locale;

/** Creates the configured administrator account once when credentials are supplied. */
@Configuration
public class BootstrapAdminConfiguration {
    private static final int MINIMUM_BOOTSTRAP_PASSWORD_LENGTH = 12;
    private static final String ADMINISTRATOR_NAME = "System Administrator";

    @Bean
    CommandLineRunner bootstrapAdminRunner(
            UserRepository users,
            PasswordEncoder passwordEncoder,
            @Value("${AUTH_BOOTSTRAP_EMAIL:}") String email,
            @Value("${AUTH_BOOTSTRAP_PASSWORD:}") String password
    ) {
        return args -> createBootstrapAdministrator(users, passwordEncoder, email, password);
    }

    private void createBootstrapAdministrator(
            UserRepository users,
            PasswordEncoder passwordEncoder,
            String email,
            String password
    ) {
        if (email.isBlank() || password.isBlank()) {
            return;
        }
        if (password.length() < MINIMUM_BOOTSTRAP_PASSWORD_LENGTH) {
            throw new IllegalArgumentException(
                    "Bootstrap password must be at least 12 characters");
        }
        if (!users.existsByEmailIgnoreCase(email)) {
            String normalizedEmail = email.trim().toLowerCase(Locale.getDefault());
            users.save(new UserEntity(ADMINISTRATOR_NAME, normalizedEmail,
                    passwordEncoder.encode(password), UserRole.ADMIN));
        }
    }
}
