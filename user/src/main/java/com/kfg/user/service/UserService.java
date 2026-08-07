package com.kfg.user.service;

import com.kfg.user.dto.CreateUserCommand;
import com.kfg.user.dto.response.UserResponse;

import java.util.UUID;

public interface UserService {
    UserResponse createUser(CreateUserCommand command);
    UserResponse getUserById(UUID id);
}
