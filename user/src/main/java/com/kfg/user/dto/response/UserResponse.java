package com.kfg.user.dto.response;

import com.kfg.user.domain.KycStatus;

import java.util.UUID;

public record UserResponse(
    UUID id,
    String fullName,
    String email,
    KycStatus kycStatus
){}
