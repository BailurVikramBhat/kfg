package com.kfg.user.dto.response;

public record BasicDetailsResponse(
        String firstName,
        String lastName,
        String email,
        String phoneNumber
) {
}
