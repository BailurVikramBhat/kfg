package com.kfg.user.dto.response;

import java.util.UUID;

public record UserResponse(
    UUID id,
    String fullName,
    String email,
    String kycStatus
){}
