package com.trainingmug.auth.mapper;

import com.trainingmug.auth.dto.LoginResponse;
import com.trainingmug.auth.dto.RegistrationResponse;
import com.trainingmug.auth.dto.UserView;
import com.trainingmug.auth.entity.UserEntity;
import org.springframework.stereotype.Component;

/** Converts user entities into API response DTOs without exposing credentials. */
@Component
public class AuthMapper {
    public LoginResponse toLoginResponse(UserEntity user, String accessToken, long expiresIn) {
        UserView view = new UserView(
                user.getId(), user.getFullName(), user.getEmail(), user.getRole().name());
        return new LoginResponse(accessToken, "Bearer", expiresIn, view);
    }

    public RegistrationResponse toRegistrationResponse(UserEntity user) {
        return new RegistrationResponse(
                user.getId(), user.getFullName(), user.getEmail(), user.getRole().name());
    }
}
