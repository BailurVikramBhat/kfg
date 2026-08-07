package com.kfg.user.mapper;

import com.kfg.user.domain.User;
import com.kfg.user.dto.response.UserResponse;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {
    public UserResponse toResponse(User user) {
        return new UserResponse(
                user.getId(),
                buildFullName(user),
                user.getEmail(),
                user.getKycStatus()
        );
    }

    private String buildFullName(User user) {
        return "%s %s"
                .formatted(
                        user.getFirstName(),
                        user.getLastName()
                )
                .trim();
    }
}
