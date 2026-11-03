package com.trainingmug.auth.service;

import com.trainingmug.auth.dto.RegisterRequest;
import com.trainingmug.auth.dto.LoginRequest;
import com.trainingmug.auth.entity.UserEntity;
import com.trainingmug.auth.enums.UserRole;
import com.trainingmug.auth.repository.UserRepository;
import com.trainingmug.auth.mapper.AuthMapper;
import com.trainingmug.auth.exception.AccountAlreadyExistsException;
import com.trainingmug.auth.exception.InvalidCredentialsException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {
  @Mock UserRepository users;
  @Mock TokenService tokens;
  AuthService service;

  @BeforeEach void setUp() {
    service = new AuthService(
        users,
        new org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder(),
        tokens,
        new AuthMapper());
  }

  @Test void registrationCreatesAnHrAccountAndHashesThePassword() {
    when(users.existsByEmailIgnoreCase("jordan@example.com")).thenReturn(false);
    when(users.saveAndFlush(any(UserEntity.class))).thenAnswer(invocation -> invocation.getArgument(0));

    var result = service.register(new RegisterRequest("Jordan Davis", "Jordan@example.com", "strong-password-123"));

    ArgumentCaptor<UserEntity> captor = ArgumentCaptor.forClass(UserEntity.class);
    verify(users).saveAndFlush(captor.capture());
    UserEntity saved = captor.getValue();
    assertEquals("Jordan Davis", saved.getFullName());
    assertEquals("jordan@example.com", saved.getEmail());
    assertEquals(UserRole.HR, saved.getRole());
    assertNotEquals("strong-password-123", saved.getPasswordHash());
    assertTrue(new org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder().matches("strong-password-123", saved.getPasswordHash()));
    assertEquals("HR", result.role());
  }

  @Test void registrationRejectsAnExistingEmail() {
    when(users.existsByEmailIgnoreCase("jordan@example.com")).thenReturn(true);
    assertThrows(AccountAlreadyExistsException.class, () -> service.register(new RegisterRequest("Jordan Davis", "Jordan@example.com", "strong-password-123")));
    verify(users, never()).saveAndFlush(any(UserEntity.class));
  }

  @Test void loginRejectsWrongPassword() {
    UserEntity user = new UserEntity("Jordan Davis", "jordan@example.com",
        new org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder().encode("correct-password"),
        UserRole.HR);
    when(users.findByEmailIgnoreCase("jordan@example.com")).thenReturn(java.util.Optional.of(user));

    assertThrows(InvalidCredentialsException.class,
        () -> service.login(new LoginRequest("jordan@example.com", "wrong-password")));
    verifyNoInteractions(tokens);
  }
}
